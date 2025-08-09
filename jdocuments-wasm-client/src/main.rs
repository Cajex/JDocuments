use leptos::prelude::*;

mod home;

#[component]
pub fn Home() -> impl IntoView {
    let counter = signal(1);
    view! {
        <div>
            <button on:click=move|_| { counter.1.set(counter.0.get() + 1) }>"Push"</button>
            <p>"Count" { move|| counter.0.get() }</p>
        </div>
    }
}

fn main() {
    console_error_panic_hook::set_once();

    leptos::mount::mount_to_body(Home)
}