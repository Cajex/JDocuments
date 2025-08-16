use slint_build::CompilerConfiguration;

fn main() {
    slint_build::compile_with_config(
        "ui/manual.slint",
        CompilerConfiguration::new().with_style("cosmic-dark".to_owned()),
    )
    .unwrap();
    cc::Build::new()
        .cpp(false)
        .flag("-std=c99")
        .include("../jdocuments-bridge/include/")
        .file("../jdocuments-bridge/bridge.c")
        .compile("bridge");
}
