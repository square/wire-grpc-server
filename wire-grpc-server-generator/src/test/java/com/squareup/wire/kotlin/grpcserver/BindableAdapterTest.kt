/*
 * Copyright (C) 2021 Square, Inc.
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

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.wire.buildSchema
import com.squareup.wire.kotlin.grpcserver.BindableAdapterGenerator.addBindableAdapter
import com.squareup.wire.kotlin.grpcserver.GoldenTestUtils.assertFileEquals
import okio.Path.Companion.toPath
import org.junit.Test
import kotlin.test.assertEquals

class BindableAdapterTest {
    @Test
    fun legacyAdapter() {
        val path = "src/test/proto/RouteGuideProto.proto".toPath()
        val schema = buildSchema {
            add(path, "")
            addLocal(path)
        }
        val service = schema.getService("routeguide.RouteGuide")

        val code = FileSpec.builder("routeguide", "RouteGuide")
            .addType(
                TypeSpec.classBuilder("RouteGuideWireGrpc")
                    .apply {
                        addBindableAdapter(
                            generator = ClassNameGenerator(buildClassMap(schema, service!!)),
                            builder = this,
                            service,
                            KotlinGrpcGenerator.Companion.Options(singleMethodServices = true, suspendingCalls = false),
                        )
                    }
                    .build(),
            )
            .build()

        assertFileEquals("BindableAdapter.kt", code)
    }

    @Test
    fun `works on suspending streaming responses`() {
        val code = bindableCodeFor(
            "test",
            "TestService",
            """
      syntax = "proto2";
      package test;

      message Test {}
      service TestService {
        rpc TestRPC(Test) returns (stream Test){}
      }
            """.trimMargin(),
        )
        assertEquals(
            """
      package test

      import com.squareup.wire.kotlin.grpcserver.FlowAdapter
      import kotlin.coroutines.CoroutineContext
      import kotlin.coroutines.EmptyCoroutineContext
      import kotlin.jvm.JvmOverloads
      import kotlinx.coroutines.flow.Flow

      public class TestServiceWireGrpc {
        public class BindableAdapter @JvmOverloads constructor(
          context: CoroutineContext = EmptyCoroutineContext,
          private val service: () -> TestServiceServer,
        ) : TestServiceWireGrpc.TestServiceImplBase(context) {
          override fun TestRPC(request: Test): Flow<Test> = FlowAdapter.serverStream(context, request,
              service()::TestRPC)
        }
      }
            """.trimIndent().trim(),
            code,
        )
    }

    @Test
    fun `works on suspending streaming requests`() {
        val code = bindableCodeFor(
            "test",
            "TestService",
            """
      syntax = "proto2";
      package test;

      message Test {}
      service TestService {
        rpc TestRPC(stream Test) returns (Test){}
      }
            """.trimMargin(),
        )
        assertEquals(
            """
      package test

      import com.squareup.wire.kotlin.grpcserver.FlowAdapter
      import kotlin.coroutines.CoroutineContext
      import kotlin.coroutines.EmptyCoroutineContext
      import kotlin.jvm.JvmOverloads
      import kotlinx.coroutines.flow.Flow

      public class TestServiceWireGrpc {
        public class BindableAdapter @JvmOverloads constructor(
          context: CoroutineContext = EmptyCoroutineContext,
          private val service: () -> TestServiceServer,
        ) : TestServiceWireGrpc.TestServiceImplBase(context) {
          override suspend fun TestRPC(request: Flow<Test>): Test = FlowAdapter.clientStream(context,
              request, service()::TestRPC)
        }
      }
            """.trimIndent().trim(),
            code,
        )
    }

    @Test
    fun `works on suspending streaming bidi rpcs`() {
        val code = bindableCodeFor(
            "test",
            "TestService",
            """
      syntax = "proto2";
      package test;

      message Test {}
      service TestService {
        rpc TestRPC(stream Test) returns (stream Test){}
      }
            """.trimMargin(),
        )
        assertEquals(
            """
      package test

      import com.squareup.wire.kotlin.grpcserver.FlowAdapter
      import kotlin.coroutines.CoroutineContext
      import kotlin.coroutines.EmptyCoroutineContext
      import kotlin.jvm.JvmOverloads
      import kotlinx.coroutines.flow.Flow

      public class TestServiceWireGrpc {
        public class BindableAdapter @JvmOverloads constructor(
          context: CoroutineContext = EmptyCoroutineContext,
          private val service: () -> TestServiceServer,
        ) : TestServiceWireGrpc.TestServiceImplBase(context) {
          override fun TestRPC(request: Flow<Test>): Flow<Test> = FlowAdapter.bidiStream(context, request,
              service()::TestRPC)
        }
      }
            """.trimIndent().trim(),
            code,
        )
    }

    @Test
    fun `works on suspending streaming bidi rpcs with single method services`() {
        val code = bindableCodeFor(
            "test",
            "TestService",
            """
      syntax = "proto2";
      package test;

      message Test {}
      service TestService {
        rpc TestRPC(stream Test) returns (stream Test){}
      }
            """.trimMargin(),
            KotlinGrpcGenerator.Companion.Options(
                singleMethodServices = true,
                suspendingCalls = true,
            ),
        )
        assertEquals(
            """
      package test

      import com.squareup.wire.kotlin.grpcserver.FlowAdapter
      import kotlin.coroutines.CoroutineContext
      import kotlin.coroutines.EmptyCoroutineContext
      import kotlin.jvm.JvmOverloads
      import kotlinx.coroutines.flow.Flow

      public class TestServiceWireGrpc {
        public class BindableAdapter @JvmOverloads constructor(
          context: CoroutineContext = EmptyCoroutineContext,
          private val TestRPC: () -> TestServiceTestRPCServer,
        ) : TestServiceWireGrpc.TestServiceImplBase(context) {
          override fun TestRPC(request: Flow<Test>): Flow<Test> = FlowAdapter.bidiStream(context, request,
              TestRPC()::TestRPC)
        }
      }
            """.trimIndent().trim(),
            code,
        )
    }

    private fun bindableCodeFor(
        pkg: String,
        serviceName: String,
        schemaCode: String,
        options: KotlinGrpcGenerator.Companion.Options = KotlinGrpcGenerator.Companion.Options(
            singleMethodServices = false,
            suspendingCalls = true,
        ),
    ): String {
        val schema = buildSchema { add("test.proto".toPath(), schemaCode) }
        val service = schema.getService("$pkg.$serviceName")!!
        val typeSpec = TypeSpec.classBuilder("${serviceName}WireGrpc")
        val nameGenerator = ClassNameGenerator(buildClassMap(schema, service))

        addBindableAdapter(nameGenerator, typeSpec, service, options)

        return FileSpec.builder(pkg, "test.kt")
            .addType(typeSpec.build())
            .build()
            .toString()
            .trim()
    }
}
