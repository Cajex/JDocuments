use crate::storage::SubmitterStorage;
use clap::{Parser, ValueEnum};
use slint::ComponentHandle;
use std::sync::{Arc, Mutex};

mod bridge;
mod manual;
mod scanner;
mod storage;

#[derive(Parser, Debug)]
#[command()]
pub struct SubmitterArguments {
    #[arg(value_enum, short_alias = 'm')]
    mode: Option<SubmitterMode>,
}

#[derive(Debug, Clone, ValueEnum)]
pub enum SubmitterMode {
    Manual,
    Capture,
}

fn main() {
    let arguments = SubmitterArguments::parse();

    tracing_subscriber::fmt()
        .with_ansi(true)
        .with_thread_names(true)
        .init();
    let storage = Arc::new(Mutex::new(SubmitterStorage::default()));
    if let Some(mode) = arguments.mode {
        match mode {
            SubmitterMode::Manual => manual::setup(storage.clone())
                .unwrap()
                .run()
                .unwrap(),
            SubmitterMode::Capture => {
                panic!("currently not implemented!")
            }
        }
    }
}
