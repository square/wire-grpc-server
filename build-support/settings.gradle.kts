rootProject.name = "build-support"

include(":server-generator")
project(":server-generator").projectDir = File("../server-generator")

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
