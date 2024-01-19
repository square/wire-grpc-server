import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

buildscript {
  dependencies {
    classpath(libs.pluginz.dokka)
    classpath(libs.pluginz.android)
    classpath(libs.pluginz.binaryCompatibilityValidator)
    classpath(libs.pluginz.kotlin)
    classpath(libs.pluginz.kotlinSerialization)
    classpath(libs.pluginz.shadow)
    classpath(libs.pluginz.spotless)
    classpath(libs.protobuf.gradlePlugin)
    classpath(libs.vanniktechPublishPlugin)
    classpath(libs.pluginz.buildConfig)
    classpath(libs.guava)
    classpath(libs.asm)

    classpath(libs.wire.gradlePlugin)
    classpath(libs.wire.runtime)
    classpath("com.squareup.wiregrpcserver.build:gradle-plugin")
  }

  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

allprojects {
  group = project.property("GROUP") as String
  version = project.property("VERSION_NAME") as String

  repositories {
    mavenCentral()
    google()
  }
}
