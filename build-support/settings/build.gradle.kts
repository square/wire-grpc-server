plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
}

gradlePlugin {
  plugins {
    create("wireGrpcServerSettings") {
      id = "com.squareup.wiregrpcserver.settings"
      displayName = "Wire gRPC Server settings plugin"
      description = "Gradle plugin for Wire gRPC Server build settings"
      implementationClass = "com.squareup.wiregrpcserver.buildsettings.WireGrpcServerSettingsPlugin"
    }
  }
}

repositories {
  mavenCentral()
  gradlePluginPortal()
  google()
}
