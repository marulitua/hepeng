fn main() -> Result<(), Box<dyn std::error::Error>> {
    tonic_build::compile_protos("../android/protos/src/proto/com/hepeng/note/helloworld/helloworld.proto")?;
    Ok(())
}
