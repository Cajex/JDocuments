use crate::storage::SubmitterStorage;
use slint::ComponentHandle;

mod storage;
mod ui;

fn main() {
    tracing_subscriber::fmt()
        .with_ansi(true)
        .with_thread_names(true)
        .init();
    SubmitterStorage::default();
    ui::setup().unwrap().run().unwrap()
}
