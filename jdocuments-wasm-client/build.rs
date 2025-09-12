use slint_build::CompilerConfiguration;

fn main() {
    slint_build::compile_with_config(
        "ui/components.slint",
        CompilerConfiguration::new().with_style("native".to_owned()),
    )
    .unwrap();
}
