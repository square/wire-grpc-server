rootProject.name = "build-support"

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
