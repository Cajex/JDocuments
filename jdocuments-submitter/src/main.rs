mod storage;

fn main() {
    tracing_subscriber::fmt()
        .with_ansi(true)
        .with_thread_names(true)
        .init();
    println!("Hello, world!");
}
