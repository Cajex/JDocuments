use slint::{Color, Model, SharedString, VecModel};
#[cfg(target_family = "wasm")]
use wasm_bindgen::prelude::*;
use log::debug;
use slint::private_unstable_api::debug;

slint::include_modules!();

#[cfg_attr(target_arch = "wasm32",
    wasm_bindgen::prelude::wasm_bindgen(start))]
pub fn main() {
    let main_window = TestWindow::new().unwrap();

    SearchComponentGlobal::get(&main_window).on_push_part(|parts, part| {
        debug(SharedString::from("Hello"));
        let parts = parts.as_any().downcast_ref::<VecModel<(Color, SharedString)>>().unwrap();
        let mut items = vec![];
        for item in parts.iter() {
            items.push(item);
        }
        parts.set_vec(items);
    });

    main_window.run().unwrap();
}