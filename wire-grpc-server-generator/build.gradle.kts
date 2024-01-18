plugins {
  id("java-library")
  kotlin("jvm")
}



dependencies {
  api(libs.wire.schema)
  implementation(libs.wire.runtime)
  implementation(libs.wire.grpcClient)
  implementation(libs.okio.core)
  api(libs.kotlinpoet)
  api(libs.protobuf.java)
  implementation(projects.wireGrpcServer)
  testImplementation(libs.wire.schemaHandlerTests)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.truth)
  testImplementation(libs.assertj)
  testImplementation(libs.kotlin.jsr223)
}

sourceSets {
  val test by getting {
    java.srcDir("src/test/proto")
  }
}
