use clap::{Parser, ValueEnum};
use crate::storage::SubmitterStorage;
use slint::ComponentHandle;

mod storage;
mod ui;

#[derive(Parser, Debug)]
#[command()]
pub struct SubmitterArguments {
    #[arg(value_enum, short_alias = 'm')]
    mode: SubmitterMode,
}

#[derive(Debug, Clone, ValueEnum)]
pub enum SubmitterMode {
    GraphicInterface,
    Scanner,
}

fn main() {
    let arguments = SubmitterArguments::parse();

    tracing_subscriber::fmt()
        .with_ansi(true)
        .with_thread_names(true)
        .init();
    let _storage = SubmitterStorage::default();
    match arguments.mode {
        SubmitterMode::GraphicInterface => {
            ui::setup().unwrap().run().unwrap()
        }
        SubmitterMode::Scanner => {}
    }
}
