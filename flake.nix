{
  description = "gRPC-Kotlin development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

    flake-parts.url = "github:hercules-ci/flake-parts";
    flake-parts.inputs.nixpkgs.follows = "nixpkgs";
    fenix = {
      url = "github:nix-community/fenix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = inputs@{ self, nixpkgs, flake-parts, ... }:
    flake-parts.lib.mkFlake { inherit inputs; } {

      systems = [
        "x86_64-linux"
        "aarch64-linux"
        "x86_64-darwin"
        "aarch64-darwin"
      ];

      perSystem = { system, inputs', pkgs, ... }:
        let
          pkgs = import nixpkgs {
            inherit system;
            config = {
              android_sdk.accept_license = true;
              allowUnfree = true;
            };
          };

          # JDK versions
          jdk8 = pkgs.jdk8;
          jdk17 = pkgs.jdk17;

          # Android SDK composition
          androidComposition = pkgs.androidenv.composeAndroidPackages {
            platformVersions = [ "34" "35" "36" ];
            buildToolsVersions = [ "34.0.0" "35.0.0" ];
            includeEmulator = false;
            includeNDK = false;
            includeSources = false;
            includeSystemImages = false;
            extraLicenses = [
              "android-googletv-license"
              "android-sdk-arm-dbt-license"
              "android-sdk-license"
              "android-sdk-preview-license"
              "google-gdk-license"
              "intel-android-extra-license"
              "intel-android-sysimage-license"
              "mips-android-sysimage-license"
            ];
          };

          androidSdk = androidComposition.androidsdk;

          # Patch script for downloaded Gradle native binaries
          patchProtocPlugin = pkgs.writeShellScript "patch-protoc-plugin" ''
            PLUGIN_DIR="$HOME/.gradle/caches/modules-2/files-2.1/io.grpc/protoc-gen-grpc-java"

            if [ -d "$PLUGIN_DIR" ]; then
              echo "Patching protoc-gen-grpc-java binaries for NixOS..."
              find "$PLUGIN_DIR" -name "*.exe" -type f | while read -r plugin; do
                if ! ${pkgs.patchelf}/bin/patchelf --print-interpreter "$plugin" 2>/dev/null | grep -q nix; then
                  echo "Patching: $plugin"
                  chmod +w "$plugin"
                  ${pkgs.patchelf}/bin/patchelf \
                    --set-interpreter "$(cat ${pkgs.stdenv.cc}/nix-support/dynamic-linker)" \
                    "$plugin" 2>/dev/null || true
                  ${pkgs.patchelf}/bin/patchelf \
                    --set-rpath "${pkgs.lib.makeLibraryPath [ pkgs.stdenv.cc.cc pkgs.zlib ]}" \
                    "$plugin" 2>/dev/null || true
                  chmod -w "$plugin"
                fi
              done

              find "$HOME/.gradle/caches/modules-2/files-2.1/io.grpc/protoc-gen-grpc-kotlin" \
                -name "*.exe" -type f 2>/dev/null | while read -r plugin; do
                if ! ${pkgs.patchelf}/bin/patchelf --print-interpreter "$plugin" 2>/dev/null | grep -q nix; then
                  echo "Patching: $plugin"
                  chmod +w "$plugin"
                  ${pkgs.patchelf}/bin/patchelf \
                    --set-interpreter "$(cat ${pkgs.stdenv.cc}/nix-support/dynamic-linker)" \
                    "$plugin" 2>/dev/null || true
                  ${pkgs.patchelf}/bin/patchelf \
                    --set-rpath "${pkgs.lib.makeLibraryPath [ pkgs.stdenv.cc.cc pkgs.zlib ]}" \
                    "$plugin" 2>/dev/null || true
                  chmod -w "$plugin"
                fi
              done
            fi

            AAPT2_DIR="$HOME/.gradle/caches"
            if [ -d "$AAPT2_DIR" ]; then
              echo "Patching AAPT2 binaries for NixOS..."
              find "$AAPT2_DIR" -path "*/transforms/*/aapt2*" -name "aapt2" -type f 2>/dev/null |
              while read -r aapt2; do
                if [ -x "$aapt2" ] &&
                   ! ${pkgs.patchelf}/bin/patchelf --print-interpreter "$aapt2" 2>/dev/null | grep -q nix; then
                  echo "Patching AAPT2: $aapt2"
                  chmod +w "$aapt2"
                  ${pkgs.patchelf}/bin/patchelf \
                    --set-interpreter "$(cat ${pkgs.stdenv.cc}/nix-support/dynamic-linker)" \
                    "$aapt2" 2>/dev/null || true
                  ${pkgs.patchelf}/bin/patchelf \
                    --set-rpath "${pkgs.lib.makeLibraryPath [ pkgs.stdenv.cc.cc pkgs.zlib ]}" \
                    "$aapt2" 2>/dev/null || true
                  chmod -w "$aapt2"
                fi
              done
            fi
          '';

          gradlewWrapper = pkgs.writeShellScriptBin "gradlew-nix" ''
            ${patchProtocPlugin}
            exec ./gradlew "$@"
          '';

          # -----------------------------
          # Rust toolchain
          # -----------------------------
          fenixPkgs = inputs'.fenix.packages;
          rustChannel = "stable";

          rustToolchain = fenixPkgs.combine [
            fenixPkgs.${rustChannel}.toolchain
            fenixPkgs.targets.aarch64-linux-android.${rustChannel}.rust-std
            fenixPkgs.targets.x86_64-linux-android.${rustChannel}.rust-std
          ];
        in
        {
          devShells.default = pkgs.mkShell {
            buildInputs = with pkgs; [
              jdk8
              jdk17
              gradle
              bazel_7
              protobuf
              androidSdk
              patchelf
              git
              which
              gnused
              coreutils
              gradlewWrapper
              rustToolchain

              pkg-config
              openssl

              nodejs

            ];

            shellHook = ''
              echo "gRPC-Kotlin development environment"
              echo "======================================"
              echo "Java 8: $(${jdk8}/bin/java -version 2>&1 | head -n 1)"
              echo "Java 17: $(${jdk17}/bin/java -version 2>&1 | head -n 1)"
              echo "Gradle version: $(gradle --version | grep Gradle)"
              echo "Protoc version: $(protoc --version)"
              echo "Android SDK: ${androidSdk}/libexec/android-sdk"
              echo ""
              echo "NOTE: Use 'gradlew-nix' instead of './gradlew' on NixOS"
              echo ""

              export JAVA_HOME="${jdk17}"
              export PATH="$JAVA_HOME/bin:$PATH"

              export JDK8_HOME="${jdk8}/lib/openjdk"
              export JDK17_HOME="${jdk17}"

              export ANDROID_HOME="${androidSdk}/libexec/android-sdk"
              export ANDROID_SDK_ROOT="$ANDROID_HOME"

              mkdir -p ~/.gradle
              cat > ~/.gradle/gradle.properties.nix << 'EOF'
org.gradle.java.installations.auto-detect=false
org.gradle.java.installations.fromEnv=JDK8_HOME,JDK17_HOME
EOF

              if [ -f ~/.gradle/gradle.properties ]; then
                if ! grep -q "org.gradle.java.installations.fromEnv" ~/.gradle/gradle.properties; then
                  cat ~/.gradle/gradle.properties.nix >> ~/.gradle/gradle.properties
                fi
              else
                cp ~/.gradle/gradle.properties.nix ~/.gradle/gradle.properties
              fi

              export GRADLE_OPTS="-Xmx2048m -XX:MaxMetaspaceSize=512m"
              export LD_LIBRARY_PATH="${pkgs.lib.makeLibraryPath [ pkgs.stdenv.cc.cc pkgs.zlib ]}:$LD_LIBRARY_PATH"
            '';
          };
        };
    };
}
