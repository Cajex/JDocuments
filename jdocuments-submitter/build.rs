use slint_build::CompilerConfiguration;

fn main() {
    slint_build::compile_with_config(
        "ui/submitter.slint",
        CompilerConfiguration::new().with_style("cosmic-dark".to_owned()),
    )
    .unwrap()
}
