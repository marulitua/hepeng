#![allow(non_snake_case)]

use dioxus::prelude::*;
use dioxus_logger::tracing;
use crate::views::form::Form;
use crate::views::banner::Banner;

pub mod views;

#[derive(Clone, Routable, Debug, PartialEq, serde::Serialize, serde::Deserialize)]
enum Route {
    #[route("/")]
    Home {},
    #[route("/blog/:id")]
    Blog { id: i32 },
    #[route("/form")]
    Form {},
    #[route("/banner")]
    Banner {},
}

fn main() {
    // Init logger
    dioxus_logger::init(tracing::Level::INFO).expect("failed to init logger");
    tracing::info!("starting app");
    launch(App);
}

fn App() -> Element {
    rsx! {
        Router::<Route> {}
    }
}

#[component]
fn Blog(id: i32) -> Element {
    rsx! {
        Link { to: Route::Home {}, "Go to counter" }
        "Blog post {id}"
    }
}

#[component]
fn Home() -> Element {
    const _TAILWIND_URL: &str = manganis::mg!(file("public/tailwind.css"));
    let mut count = use_signal(|| 0);
    let mut text = use_signal(|| String::from("..."));

    rsx! {
        div { class: "sm:mx-auto sm:w-full sm:max-w-sm",
            img {
                alt: "Your Company",
                src: "https://tailwindui.com/plus/img/logos/mark.svg?color=indigo&shade=600",
                class: "mx-auto h-10 w-auto"
            }
            h2 { class: "mt-10 text-center text-2xl/9 font-bold tracking-tight text-gray-900",
                "Hello World"
            }
        }
        div { class: "mt-10 sm:mx-auto sm:w-full sm:max-w-sm",
            div { class: "space-y-6",
                div {
                    h2 {
                        class: "text-base/7 font-semibold text-gray-900",
                        "High-Five counter"
                    }
                    p {
                        class: "mt-1 text-sm/6 text-gray-600",
                        "{count}"
                    }
                }
                div {
                    class: "inline-flex rounded-lg bg-slate-100 p-0.5",
                    button {
                        onclick: move |_| count += 1,
                        class: "inline-flex items-center gap-2 rounded-md px-4 py-2 text-sm text-gray-500 hover:text-gray-700 focus:relative bg-indigo-600",
                        svg {
                            "viewBox": "0 0 24 24",
                            "fill": "currentColor",
                            "xmlns": "http://www.w3.org/2000/svg",
                            class: "size-4",
                            path { "d": "M0 0h24v24H0z", "stroke": "none", "fill": "none" }
                            path { "d": "M13 3a3 3 0 0 1 2.995 2.824l.005 .176v4h2a3 3 0 0 1 2.98 2.65l.015 .174l.005 .176l-.02 .196l-1.006 5.032c-.381 1.626 -1.502 2.796 -2.81 2.78l-.164 -.008h-8a1 1 0 0 1 -.993 -.883l-.007 -.117l.001 -9.536a1 1 0 0 1 .5 -.865a2.998 2.998 0 0 0 1.492 -2.397l.007 -.202v-1a3 3 0 0 1 3 -3z" }
                            path { "d": "M5 10a1 1 0 0 1 .993 .883l.007 .117v9a1 1 0 0 1 -.883 .993l-.117 .007h-1a2 2 0 0 1 -1.995 -1.85l-.005 -.15v -7a2 2 0 0 1 1.85 -1.995l.15 -.005h1z" }
                        }
                        "Up high!"
                    }
                    button {
                        onclick: move |_| count -= 1,
                        class: "inline-flex items-center gap-2 rounded-md px-4 py-2 text-sm text-gray-500 hover:text-gray-700 focus:relative bg-red-600 text-white",
                        svg {
                           "stroke": "currentColor",
                           "viewBox": "0 0 24 24",
                           "xmlns": "http://www.w3.org/2000/svg",
                           "stroke-width": "2",
                           "fill": "none",
                           "stroke-linecap": "round",
                           "stroke-linejoin": "round",
                           class: "size-4",
                           path { "d": "M0 0h24v24H0z", "stroke": "none", "fill": "none" }
                           path { "d": "M7 13v-8a1 1 0 0 0 -1 -1h-2a1 1 0 0 0 -1 1v7a1 1 0 0 0 1 1h3a4 4 0 0 1 4 4v1a2 2 0 0 0 4 0v-5h3a2 2 0 0 0 2 -2l-1 -5a2 3 0 0 0 -2 -2h-7a3 3 0 0 0 -3 3" }
                       }
                        "Down low!"
                    }
                }
                div {
                    div { class: "flex items-center justify-between",
                        label {
                            r#for: "password",
                            class: "block text-sm/6 font-medium text-gray-900",
                            "Server data"
                        }
                        div { class: "text-sm",
                            button {
                                class: "font-semibold text-indigo-600 hover:text-indigo-500",
                                onclick: move |_| async move {
                                    if let Ok(data) = get_server_data().await {
                                        tracing::info!("Client received: {}", data);
                                        text.set(data.clone());
                                        post_server_data(data).await.unwrap();
                                    }
                                },
                                "Get Server Data"
                            }
                        }
                    }
                    div { class: "mt-2",
                        p {
                            class: "mt-4 text-gray-700",
                            "{text}"
                        }
                    }
                }
            }
            p { class: "mt-10 text-center text-sm/6 text-gray-500",
                "\n      Visit another page ?\n      "
                Link {
                    to: Route::Blog {
                        id: count()
                    },
                    class: "font-semibold text-indigo-600 hover:text-indigo-500",
                    "Go to blog"
                }
            }
        }
    }
}

#[server(PostServerData)]
async fn post_server_data(data: String) -> Result<(), ServerFnError> {
    tracing::info!("Server received: {}", data);
    Ok(())
}

#[server(GetServerData)]
async fn get_server_data() -> Result<String, ServerFnError> {
    Ok("Hello from the server!".to_string())
}
