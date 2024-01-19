rootProject.name = "build-support"

include(":wire-grpc-server-generator")
project(":wire-grpc-server-generator").projectDir = File("../wire-grpc-server-generator")
include(":wire-grpc-server")
project(":wire-grpc-server").projectDir = File("../wire-grpc-server")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs").from(files("../gradle/libs.versions.toml"))
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}
