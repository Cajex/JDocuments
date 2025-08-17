use crate::storage::SubmitterStorage;
use jdocuments_commons::{CloudDocumentObject};
use slint::{include_modules, PlatformError};
use std::sync::{Arc, Mutex};

include_modules!();

#[derive(Debug)]
pub struct ManualFormular {
    pub form_title: Option<String>,
    pub form_author: Option<String>,
    pub form_loc: Option<String>,
    pub form_date: Option<String>,
    pub form_links: Option<String>,
    pub form_tags: Option<String>,
}

impl ManualFormular {
    pub fn new() -> Self {
        Self {
            form_title: None,
            form_author: None,
            form_loc: None,
            form_date: None,
            form_links: None,
            form_tags: None,
        }
    }

    pub fn clear(&mut self) {
        self.form_title = None;
        self.form_author = None;
        self.form_loc = None;
        self.form_date = None;
        self.form_links = None;
        self.form_tags = None;
    }
}

pub fn setup(storage: Arc<Mutex<SubmitterStorage>>) -> Result<SubmitterWindow, PlatformError> {
    let window = SubmitterWindow::new()?;
    let formular = Arc::new(Mutex::new(ManualFormular::new()));

    {
        let formular = formular.clone();
        window.on_document_title_accepted(move |title| {
            if !title.is_empty() {
                formular.lock().as_mut().unwrap().form_title = Some(title.as_str().to_owned());
            } else {
                formular.lock().as_mut().unwrap().form_title = None;
            }
        });
    }

    {
        let formular = formular.clone();
        window.on_document_author_accepted(move |title| {
            if !title.is_empty() {
                formular.lock().as_mut().unwrap().form_author = Some(title.as_str().to_owned());
            } else {
                formular.lock().as_mut().unwrap().form_author = None;
            }
        });
    }

    {
        let formular = formular.clone();
        window.on_document_origin_location_accepted(move |title| {
            if !title.is_empty() {
                formular.lock().as_mut().unwrap().form_loc = Some(title.as_str().to_owned());
            } else {
                formular.lock().as_mut().unwrap().form_loc = None;
            }
        });
    }

    {
        let formular = formular.clone();
        window.on_document_origin_date_accepted(move |title| {
            if !title.is_empty() {
                formular.lock().as_mut().unwrap().form_date = Some(title.as_str().to_owned());
            } else {
                formular.lock().as_mut().unwrap().form_date = None;
            }
        });
    }

    {
        let formular = formular.clone();
        window.on_document_links_accepted(move |title| {
            if !title.is_empty() {
                formular.lock().as_mut().unwrap().form_links = Some(title.as_str().to_owned());
            } else {
                formular.lock().as_mut().unwrap().form_links = None;
            }
        });
    }

    {
        let formular = formular.clone();
        window.on_document_tags_accepted(move |title| {
            if !title.is_empty() {
                formular.lock().as_mut().unwrap().form_tags = Some(title.as_str().to_owned());
            } else {
                formular.lock().as_mut().unwrap().form_tags = None;
            }
        });
    }

    let formular = formular.clone();
    window.on_submit(move || {
        let mut storage = storage.lock().unwrap();

        let formular = formular.lock().unwrap();
        let tags = formular
            .form_tags
            .clone()
            .map(|t| {
                t.split(',')
                    .map(|t| t.trim().to_owned())
                    .collect::<Vec<String>>()
            })
            .unwrap_or_default();
        let links = formular
            .form_links
            .clone()
            .map(|l| {
                l.split(',')
                    .map(|l| l.trim().to_owned())
                    .collect::<Vec<String>>()
            })
            .unwrap_or_default();

        CloudDocumentObject::insert_form(
            formular.form_title.clone(),
            formular.form_author.clone(),
            formular.form_date.clone(),
            formular.form_loc.clone(),
            tags,
            links,
            &mut storage.form,
        );
    });

    Ok(window)
}
