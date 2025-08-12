use slint::{include_modules, PlatformError};

include_modules!();

pub fn setup() -> Result<SubmitterWindow, PlatformError> {
    let window = SubmitterWindow::new()?;
    Ok(window)
}