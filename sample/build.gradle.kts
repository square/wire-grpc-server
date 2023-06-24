plugins {
  kotlin("jvm")
  id("com.squareup.wire")
  application
}

application {
  mainClassName = "com.squareup.wire.whiteboard.MiskGrpcServerKt"
}

wire {
  kotlin {
    rpcCallStyle = "blocking"
    rpcRole = "server"
    singleMethodServices = true
    grpcServerCompatible = true
  }
}

dependencies {
  implementation(projects.wireGrpcServer)
  implementation(libs.wire.runtime)
  implementation(libs.grpc.netty)
  implementation(libs.grpc.stub)
  implementation(libs.grpc.protobuf)
}
