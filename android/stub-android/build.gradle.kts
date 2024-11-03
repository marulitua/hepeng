plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.protobuf)
}

kotlin {
    jvmToolchain(8)
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    namespace = "com.hepeng.note.stublite"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    protobuf(project(":protos"))

    api(libs.kotlinx.coroutines.core)
    api(libs.grpc.stub)
    api(libs.grpc.protobuf.lite)
    api(libs.grpc.kotlin.stub)
    api(libs.protobuf.kotlin.lite)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

protobuf {
    protoc {
        artifact = libs.protoc.asProvider().get().toString()
    }
    plugins {
        create("java") {
            artifact = libs.protoc.gen.grpc.java.get().toString()
        }
        create("grpc") {
            artifact = libs.protoc.gen.grpc.java.get().toString()
        }
        create("grpckt") {
            //artifact = libs.protoc.gen.grpc.kotlin.get().toString() + ":jdk8@jar"
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("java") {
                    option("lite")
                }
                create("grpc") {
                    option("lite")
                }
                create("grpckt") {
                    option("lite")
                }
            }
            it.builtins {
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}