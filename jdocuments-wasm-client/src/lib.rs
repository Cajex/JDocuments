
#[cfg(target_family = "wasm")]
use wasm_bindgen::prelude::*;

mod home;

slint::include_modules!();

#[cfg_attr(target_family = "wasm", wasm_bindgen(start))]
pub fn main() {
    let main_window = ClientDashboardWindow::new().unwrap();
    main_window.run().unwrap();
}