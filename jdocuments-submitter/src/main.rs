use clap::{Parser, ValueEnum};
use crate::storage::SubmitterStorage;
use slint::ComponentHandle;

mod storage;
mod ui;

#[derive(Parser, Debug)]
#[command()]
pub struct SubmitterArguments {
    #[arg(value_enum, short_alias = 'm')]
    mode: Option<SubmitterMode>,
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
    if let Some(mode) = arguments.mode {
        match mode {
            SubmitterMode::GraphicInterface => {
                ui::setup().unwrap().run().unwrap()
            }
            SubmitterMode::Scanner => {
                panic!("currently not implemented!")
            }
        }
    }

}
