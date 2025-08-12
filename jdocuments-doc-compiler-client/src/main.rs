slint::include_modules!();

fn main() {
    let application = DocumentCompilerUI::new().unwrap();

    application.on_submit(|| {

    });

    application.run().unwrap()
}
