import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("com.squareup.wire")
  application
}

application {
  mainClass.set("com.squareup.wire.whiteboard.MiskGrpcServerKt")
}

wire {
  sourcePath {
    srcDir("$rootDir/samples/wire-grpc-sample/protos/src/main/proto")
  }
  custom {
    schemaHandlerFactory = com.squareup.wire.kotlin.grpcserver.GrpcServerSchemaHandler.Factory()
    options = mapOf(
      "singleMethodServices" to "true",
      "rpcCallStyle" to "blocking",
    )
    exclusive = false
  }

  kotlin {
    rpcCallStyle = "blocking"
    rpcRole = "server"
    singleMethodServices = true
  }
}

dependencies {
  implementation(libs.grpc.kotlin.stub)
  implementation(projects.samples.wireGrpcSample.protos)
  implementation(projects.server)
  implementation(libs.grpc.netty)
  implementation(libs.grpc.protobuf)
  implementation(libs.grpc.stub)
  implementation(libs.kotlin.coroutines.core)
  implementation(libs.wire.runtime)
}

tasks.withType<JavaCompile>().configureEach {
  sourceCompatibility = JavaVersion.VERSION_17.toString()
  targetCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.withType<KotlinCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
    freeCompilerArgs.add("-Xjvm-default=all")
  }
}
