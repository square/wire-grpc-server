import com.diffplug.gradle.spotless.SpotlessExtension
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
  id("java-library")
  kotlin("jvm")
  id("com.vanniktech.maven.publish.base")
  id("binary-compatibility-validator")
}

dependencies {
  api(libs.wire.schema)
  implementation(libs.wire.runtime)
  implementation(libs.wire.grpcClient)
  implementation(libs.okio.core)
  api(libs.kotlinpoet)
  api(libs.protobuf.java)
  implementation(projects.wireGrpcServer)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.truth)
  testImplementation(libs.assertj)
  testImplementation(libs.kotlin.jsr223)
  testImplementation(libs.wire.schemaHandlerTests)
}

sourceSets {
  val test by getting {
    java.srcDir("src/test/proto")
  }
}

  configure<SpotlessExtension> {
    kotlin {
      targetExclude(
        "src/test/golden/*.kt",
      )
  }
}
