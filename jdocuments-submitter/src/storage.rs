use jdocuments_commons::*;
use serde::{Deserialize, Serialize};
use std::any::TypeId;
use std::fs::OpenOptions;
use std::io::{Read, Write};
use std::path::{Path, PathBuf};
use std::sync::mpsc;
use std::{env, fs, io, thread};
use tracing::info;

pub const BYTES_PER_CHUNK: usize = 2048;

pub struct SubmitterStorage {
    pub documents: Vec<CloudDocumentObject>,
    pub links: Vec<CloudLinkObject>,
    pub tags: Vec<CloudTagObject>,
    pub path: PathBuf,
}

pub type SubmitterStoragePaths = (PathBuf, PathBuf, PathBuf);

impl Default for SubmitterStorage {
    fn default() -> Self {
        if cfg!(target_os = "windows") {
            if let Ok(profile) = env::var("USERPROFILE") {
                let documents = PathBuf::from(profile).join("Documents").join("jDocuments");
                Self::from(documents.to_str().unwrap())
            } else {
                Self::from("jDocuments")
            }
        } else if cfg!(target_family = "unix") {
            if let Ok(home) = env::var("HOME") {
                let documents = PathBuf::from(home).join("Documents").join("jDocuments");
                Self::from(documents.to_str().unwrap())
            } else {
                Self::from("jDocuments")
            }
        } else {
            Self::from("jDocuments")
        }
    }
}

impl Drop for SubmitterStorage {
    fn drop(&mut self) {
        self.update();
    }
}

impl SubmitterStorage {
    pub fn from(path: impl Into<String>) -> Self {
        let root = PathBuf::from(path.into());
        let (exists, paths) = Self::exists(root.as_path());
        if exists {
            Self::load_from_root(&paths).expect("unable to open storage paths.")
        } else {
            let cached =
                Self::write_defaults(&paths).expect("unable to parse default json values.");
            Self {
                documents: cached.0,
                links: cached.1,
                tags: cached.2,
                path: root,
            }
        }
    }

    pub fn write_defaults<A, B, C>(
        paths: &SubmitterStoragePaths,
    ) -> io::Result<(Vec<A>, Vec<B>, Vec<C>)>
    where
        for<'de> A: CloudObject + Serialize + 'de,
        for<'de> B: CloudObject + Serialize + 'de,
        for<'de> C: CloudObject + Serialize + 'de,
    {
        Ok((
            Self::write_default::<A>(paths.0.as_path())?,
            Self::write_default::<B>(paths.1.as_path())?,
            Self::write_default::<C>(paths.2.as_path())?,
        ))
    }

    pub fn write_default<O>(path: &Path) -> io::Result<Vec<O>>
    where
        for<'de> O: CloudObject + Serialize + 'de,
    {
        let mut opened = OpenOptions::new().write(true).open(path)?;

        let cached;
        if TypeId::of::<CloudDocumentObject>() == TypeId::of::<O>() {
            cached = Vec::<O>::with_capacity(64)
        } else {
            cached = Vec::new();
        }

        opened.write_all(O::list_to_pretty_json(&cached).as_bytes())?;
        O::list_to_json(&Vec::<O>::with_capacity(0));
        Ok(cached)
    }

    pub fn load_from_root(paths: &SubmitterStoragePaths) -> io::Result<Self> {
        Ok(Self {
            documents: Self::load_from_file(paths.0.as_path())?,
            links: Self::load_from_file(paths.1.as_path())?,
            tags: Self::load_from_file(paths.2.as_path())?,
            path: PathBuf::from(paths.0.parent().expect("unable to locate root path.")),
        })
    }

    pub fn load_from_file<O>(path: &Path) -> io::Result<Vec<O>>
    where
        O: CloudObject + for<'de> Deserialize<'de>,
    {
        let mut opened = OpenOptions::new().read(true).open(path)?;
        let mut buf = String::new();
        let _ = opened.read_to_string(&mut buf)?;
        Ok(O::list_from_json(buf.as_str()))
    }

    pub fn exists(path: &Path) -> (bool, SubmitterStoragePaths) {
        if path.exists() {
            if path.is_dir() {
                let paths = Self::storages(path);
                if paths.0.exists() && paths.1.exists() && paths.2.exists() {
                    return (true, paths);
                } else {
                    OpenOptions::new()
                        .create_new(true)
                        .open(paths.0.as_path())
                        .unwrap();
                    OpenOptions::new()
                        .create_new(true)
                        .open(paths.1.as_path())
                        .unwrap();
                    OpenOptions::new()
                        .create_new(true)
                        .open(paths.2.as_path())
                        .unwrap();
                }
            } else {
                panic!("Root path is not a directory!")
            }
        } else {
            fs::create_dir_all(path).unwrap();
        }
        (false, Self::storages(path))
    }

    pub fn storages(path: &Path) -> SubmitterStoragePaths {
        (
            PathBuf::from(format!("{}/documents.json", path.to_str().unwrap())),
            PathBuf::from(format!("{}/links.json", path.to_str().unwrap())),
            PathBuf::from(format!("{}/tags.json", path.to_str().unwrap())),
        )
    }

    pub fn update(&self) {
        let documents_json = CloudDocumentObject::list_to_pretty_json(&self.documents);
        let links_json = CloudLinkObject::list_to_pretty_json(&self.links);
        let tags_json = CloudTagObject::list_to_pretty_json(&self.tags);
        let storages = Self::storages(self.path.as_path());
        let mut thread_handles = vec![];
        thread_handles.push(thread::spawn(move || -> io::Result<()> {
            let mut opened = OpenOptions::new().write(true).open(storages.1)?;
            opened.write_all(links_json.as_bytes())?;
            Ok(())
        }));
        thread_handles.push(thread::spawn(move || -> io::Result<()> {
            let mut opened = OpenOptions::new().write(true).open(storages.2)?;
            opened.write_all(tags_json.as_bytes())?;
            Ok(())
        }));

        let io_channel = mpsc::channel::<[u8; BYTES_PER_CHUNK]>();

        let writer_thread = thread::spawn(move || -> io::Result<()> {
            let mut opened = OpenOptions::new().write(true).open(storages.0)?;
            for chunk in io_channel.1 {
                opened.write_all(&chunk)?;
            }
            Ok(())
        });

        for chunk in documents_json.as_bytes().chunks(BYTES_PER_CHUNK) {
            let chunk = chunk.to_vec();
            info!("opened a new chunk with[{}]!", BYTES_PER_CHUNK);
            let writer = io_channel.0.clone();
            thread_handles.push(thread::spawn(move || -> io::Result<()> {
                let mut buf = [0u8; 2048];
                buf[..chunk.len()].copy_from_slice(&chunk);
                writer
                    .send(buf)
                    .expect("unable to write chunk to writer thread!");
                Ok(())
            }));
        }

        for handle in thread_handles {
            info!("Worker thread [{:?}] joined.", handle.thread());
            handle.join().unwrap().unwrap();
        }
        drop(io_channel.0);
        info!("Origin writer channel dropped.");
        writer_thread.join().unwrap().unwrap();
    }
}
