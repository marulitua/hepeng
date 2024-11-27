use dioxus::prelude::*;

use crate::hnews::post::*;
use crate::hnews::async_call::*;

pub fn Home() -> Element {
    use_context_provider(|| Signal::new(PreviewState::Unset));
    rsx! {
        div { display: "flex", flex_direction: "row", width: "100%",
            div { width: "50%", Stories {} }
            div { width: "50%", Preview {} }
        }
    }
}

fn Stories() -> Element {
    // Fetch the top 10 stories on Hackernews
    let stories = use_resource(move || get_stories(10));

    match &*stories.read_unchecked() {
        Some(Ok(list)) => {
            rsx! {
                div {
                    for story in list {
                        StoryListing { story: story.clone() }
                    }
                }
            }

        }
        Some(Err(err)) => {
            rsx! { "An error occured while fetching stories {err}" }
        }
        None => {
            rsx! { "Loading items" }
        }
    }
}

#[derive(Clone, Debug)]
pub enum PreviewState {
    Unset,
    Loading,
    Loaded(StoryPageData),
}

fn Preview() -> Element {
    let preview_state = consume_context::<Signal<PreviewState>>();
    match preview_state() {
        PreviewState::Unset => rsx! {"Hover over a story to preview it here"},
        PreviewState::Loading => rsx! { "Loading..." },
        PreviewState::Loaded(story) => {
            rsx! {
                div { padding: "0.5rem",
                    div { font_size: "1.5rem", a { href: story.item.url, "{story.item.title}" } }
                    div { dangerous_inner_html: story.item.text }
                    for comment in &story.comments {
                        Comment { comment: comment.clone() }
                    }
                }
            }
        }
    }
}

#[component]
fn Comment(comment: CommentData) -> Element {
    rsx! {
        div { padding: "0.5rem",
            div { color: "gray", "by {comment.by}" }
            div { dangerous_inner_html: "{comment.text}" }
            for kid in &comment.sub_comments {
                Comment { comment: kid.clone() }
            }
        }
    }
}
