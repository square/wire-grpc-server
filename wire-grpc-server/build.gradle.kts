import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
  id("java-library")
  kotlin("jvm")
  id("com.vanniktech.maven.publish.base")
  id("binary-compatibility-validator")
}

dependencies {
  implementation(libs.wire.runtime)
  // io.grpc.stub relies on guava-android. This module relies on a -jre version of guava.
  implementation(libs.grpc.stub) {
    exclude(group = "com.google.guava", module = "guava")
  }
  implementation("com.google.guava:guava:21.0")
  implementation(libs.kotlin.coroutines.core)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.truth)
  testImplementation(libs.assertj)
}

sourceSets {
  val test by getting {
    java.srcDir("src/test/proto")
  }
}
