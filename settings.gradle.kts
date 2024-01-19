rootProject.name = "wire-grpc-server-parent"

pluginManagement {
  includeBuild("build-support/settings")

  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

plugins {
  id("com.squareup.wiregrpcserver.settings")
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

includeBuild("build-support") {
  dependencySubstitution {
    substitute(module("com.squareup.wiregrpcserver.build:gradle-plugin")).using(project(":"))
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":server")
include(":server-generator")
include(":samples:wire-grpc-sample:protos")
include(":samples:wire-grpc-sample:server-plain")
