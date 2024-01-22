/*
 * Copyright (C) 2024 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.wire.kotlin.grpcserver

import assertk.assertThat
import com.squareup.wire.WireTestLogger
import com.squareup.wire.buildSchema
import com.squareup.wire.kotlin.grpcserver.GoldenTestUtils.assertFileEquals
import com.squareup.wire.schema.Schema
import com.squareup.wire.schema.SchemaHandler
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.source
import java.io.File
import kotlin.test.Test

class GrpcServerSchemaHandlerTest {
    private val fileSystem = FakeFileSystem()
    private val outDirectory = "generated/wire"
    private val protoContent = File("src/test/proto/RouteGuideProto.proto").source()
        .use { source -> source.buffer().readUtf8() }
    private lateinit var schema: Schema
    private lateinit var context: SchemaHandler.Context

    @Test fun fullFile() {
        val protoPath = "src/test/proto/RouteGuideProto.proto"
        context = SchemaHandler.Context(
            fileSystem = fileSystem,
            outDirectory = outDirectory.toPath(),
            logger = WireTestLogger(),
            sourcePathPaths = setOf(protoPath),
        )
        schema = buildSchema {
            add(protoPath.toPath(), protoContent)
        }
        GrpcServerSchemaHandler.Factory()
            .create(
                includes = listOf(),
                excludes = listOf(),
                exclusive = true,
                outDirectory = outDirectory,
                options = mapOf(
                    "singleMethodServices" to "true",
                    "rpcCallStyle" to "blocking",
                ),
            )
            .handle(schema, context)

        assertThat(fileSystem.findFiles("generated"))
            .containsExactlyInAnyOrderAsRelativePaths(
                "generated/wire/routeguide/RouteGuideWireGrpc.kt",
            )

        val content = fileSystem.read("generated/wire/routeguide/RouteGuideWireGrpc.kt".toPath()) {
            readUtf8()
        }
        assertFileEquals("RouteGuideWireGrpc.kt", content)
    }

    @Test fun `singleMethodService = false adapters`() {
        val protoPath = "service.proto"
        context = SchemaHandler.Context(
            fileSystem = fileSystem,
            outDirectory = outDirectory.toPath(),
            logger = WireTestLogger(),
            sourcePathPaths = setOf(protoPath),
        )
        schema = buildSchema {
            add(protoPath.toPath(), twoMethodSchema)
        }
        GrpcServerSchemaHandler.Factory()
            .create(
                includes = listOf(),
                excludes = listOf(),
                exclusive = true,
                outDirectory = outDirectory,
                options = mapOf(
                    "singleMethodServices" to "false",
                    "rpcCallStyle" to "blocking",
                ),
            )
            .handle(schema, context)

        assertThat(fileSystem.findFiles("generated"))
            .containsExactlyInAnyOrderAsRelativePaths(
                "generated/wire/com/foo/bar/FooServiceWireGrpc.kt",
            )

        val content = fileSystem.read("generated/wire/com/foo/bar/FooServiceWireGrpc.kt".toPath()) {
            readUtf8()
        }
        assertFileEquals("nonSingleMethodService.kt", content)
    }

    @Test fun `singleMethodService = true adapters`() {
        val protoPath = "service.proto"
        context = SchemaHandler.Context(
            fileSystem = fileSystem,
            outDirectory = outDirectory.toPath(),
            logger = WireTestLogger(),
            sourcePathPaths = setOf(protoPath),
        )
        schema = buildSchema {
            add(protoPath.toPath(), twoMethodSchema)
        }
        GrpcServerSchemaHandler.Factory()
            .create(
                includes = listOf(),
                excludes = listOf(),
                exclusive = true,
                outDirectory = outDirectory,
                options = mapOf(
                    "singleMethodServices" to "true",
                    "rpcCallStyle" to "blocking",
                ),
            )
            .handle(schema, context)

        assertThat(fileSystem.findFiles("generated"))
            .containsExactlyInAnyOrderAsRelativePaths(
                "generated/wire/com/foo/bar/FooServiceWireGrpc.kt",
            )

        val content = fileSystem.read("generated/wire/com/foo/bar/FooServiceWireGrpc.kt".toPath()) {
            readUtf8()
        }
        assertFileEquals("singleMethodService.kt", content)
    }

    @Test fun `adapters for Unit return values`() {
        val protoPath = "service.proto"
        context = SchemaHandler.Context(
            fileSystem = fileSystem,
            outDirectory = outDirectory.toPath(),
            logger = WireTestLogger(),
            sourcePathPaths = setOf(protoPath),
        )
        schema = buildSchema {
            add(
                protoPath.toPath(),
                """
          |syntax = "proto3";
          |import "google/protobuf/empty.proto";
          |
          |service MyService {
          |  rpc doSomething(google.protobuf.Empty) returns (google.protobuf.Empty);
          |}
          |
                """.trimMargin(),
            )
        }
        GrpcServerSchemaHandler.Factory()
            .create(
                includes = listOf(),
                excludes = listOf(),
                exclusive = true,
                outDirectory = outDirectory,
                options = mapOf(
                    "singleMethodServices" to "false",
                    "rpcCallStyle" to "suspending",
                ),
            )
            .handle(schema, context)

        assertThat(fileSystem.findFiles("generated"))
            .containsExactlyInAnyOrderAsRelativePaths(
                "generated/wire/MyServiceWireGrpc.kt",
            )

        val content = fileSystem.read("generated/wire/MyServiceWireGrpc.kt".toPath()) {
            readUtf8()
        }
        assertFileEquals("unitService.kt", content)
    }

    companion object {
        private val twoMethodSchema = """
      |syntax = "proto2";
      |
      |package foo;
      |option java_package = "com.foo.bar";
      |
      |message Request {}
      |message Response {}
      |
      |service FooService {
      |  rpc Call1(Request) returns (Response) {}
      |  rpc Call2(Request) returns (Response) {}
      |}
      |
        """.trimMargin()
    } }
