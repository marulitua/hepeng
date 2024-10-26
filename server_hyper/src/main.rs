use axum::{response::Html, response::Json, routing::get, Router};

use serde_json::{json, Value};

#[tokio::main]
async fn main() {
    println!("Hello, world!");

    let app = Router::new()
        .route("/", get(handler))
        .route("/json", get(json));

    let listener = tokio::net::TcpListener::bind("127.0.0.1:3000")
        .await
        .unwrap();

    println!("listening on {}", listener.local_addr().unwrap());

    axum::serve(listener, app).await.unwrap();
}

async fn handler() -> Html<&'static str> {
    Html("<h1>Hello, world!</h1>")
}

async fn json() -> Json<Value> {
    Json(json!({ "data": 42 }))
}
