use crate::storage::SubmitterStorage;
use clap::{Parser, ValueEnum};
use slint::ComponentHandle;

mod bridge;
mod manual;
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
    let _storage = SubmitterStorage::default();
    if let Some(mode) = arguments.mode {
        match mode {
            SubmitterMode::Manual => manual::setup().unwrap().run().unwrap(),
            SubmitterMode::Capture => {
                panic!("currently not implemented!")
            }
        }
    }
}
