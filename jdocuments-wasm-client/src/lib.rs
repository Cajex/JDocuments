#[cfg(target_family = "wasm")]
use wasm_bindgen::prelude::*;

slint::include_modules!();

#[cfg_attr(target_arch = "wasm32",
    wasm_bindgen::prelude::wasm_bindgen(start))]
pub fn main() {
    let main_window = ClientDashboard::new().unwrap();
    main_window.run().unwrap();
}