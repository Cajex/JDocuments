fn main() {
    let config =
        slint_build::CompilerConfiguration::new()
            .with_style("cosmic-dark".into());
    slint_build::compile_with_config("slint/doc-compiler.slint", config).unwrap();
}