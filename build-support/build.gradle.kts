import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  dependencies {
    classpath(libs.pluginz.kotlin)
    classpath(libs.vanniktechPublishPlugin)
    classpath(libs.pluginz.dokka)
    classpath(libs.pluginz.buildConfig)
    classpath(libs.pluginz.spotless)
    classpath(libs.pluginz.kotlinSerialization)
    classpath(libs.pluginz.shadow)
    classpath(libs.pluginz.buildConfig)
    classpath(libs.guava)
  }

  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  kotlin("jvm") version libs.versions.kotlin
}

repositories {
  mavenCentral()
  google()
  gradlePluginPortal()
}

dependencies {
  compileOnly(libs.kotlin.gradleApi)
  implementation(libs.pluginz.android)
  implementation(libs.pluginz.binaryCompatibilityValidator)
  implementation(libs.pluginz.kotlin)
  implementation(libs.vanniktechPublishPlugin)
  implementation(libs.pluginz.dokka)
  implementation(libs.kotlin.serialization)
  implementation(libs.pluginz.buildConfig)
  implementation(libs.pluginz.spotless)
  implementation(libs.pluginz.kotlinSerialization)
  implementation(libs.pluginz.shadow)
  implementation(libs.pluginz.buildConfig)
  implementation(libs.guava)

  // Expose the generated version catalog API to the plugin.
  implementation(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
  plugins {
    create("wireGrpcServerBuild") {
      id = "com.squareup.wiregrpcserver.build"
      displayName = "Wire gRPC Server Build plugin"
      description = "Gradle plugin for Wire gRPC Server build things"
      implementationClass = "com.squareup.wiregrpcserver.buildsupport.WireGrpcServerBuildPlugin"
    }
  }
}

allprojects {
  repositories {
    mavenCentral()
    google()
  }

  plugins.withId("java") {
    configure<JavaPluginExtension> {
      withSourcesJar()
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
    }
  }

  tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
      freeCompilerArgs.add("-Xno-optimized-callable-references")
      freeCompilerArgs.add("-Xjvm-default=all")
      // https://kotlinlang.org/docs/whatsnew13.html#progressive-mode
      freeCompilerArgs.add("-progressive")
    }
  }
}
