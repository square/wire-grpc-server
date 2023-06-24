rootProject.name = "wire-grpc-server-parent"

include(":wire-grpc-server")
include(":wire-grpc-server-generator")
include(":sample")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}
