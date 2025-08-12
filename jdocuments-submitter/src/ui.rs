use slint::{PlatformError, include_modules};

include_modules!();

pub fn setup() -> Result<SubmitterWindow, PlatformError> {
    let window = SubmitterWindow::new()?;
    Ok(window)
}
