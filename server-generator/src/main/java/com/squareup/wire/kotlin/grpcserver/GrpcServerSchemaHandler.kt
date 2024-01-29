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

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.wire.schema.Extend
import com.squareup.wire.schema.Field
import com.squareup.wire.schema.Location
import com.squareup.wire.schema.ProtoFile
import com.squareup.wire.schema.ProtoType
import com.squareup.wire.schema.Schema
import com.squareup.wire.schema.SchemaHandler
import com.squareup.wire.schema.Service
import com.squareup.wire.schema.Type
import com.squareup.wire.schema.internal.javaPackage
import okio.ByteString
import okio.Path
import java.io.IOException

class GrpcServerSchemaHandler(
    options: Map<String, String>,
) : SchemaHandler() {
    private val singleMethodServices =
        options.getOrDefault(key = "singleMethodServices", defaultValue = "true")
            .equals("true", ignoreCase = true)

    private val rpcSuspending =
        options.getOrDefault(key = "rpcCallStyle", defaultValue = "suspending")
            .equals("suspending", ignoreCase = true)

    private lateinit var schema: Schema
    private lateinit var typeToKotlinName: Map<ProtoType, TypeName>

    override fun handle(schema: Schema, context: Context) {
        this.schema = schema
        typeToKotlinName = buildMap {
            fun putAll(kotlinPackage: String, enclosingClassName: ClassName?, types: List<Type>) {
                for (type in types) {
                    val className = enclosingClassName?.nestedClass(type.type.simpleName)
                        ?: ClassName(kotlinPackage, type.type.simpleName)
                    this[type.type] = className
                    putAll(kotlinPackage, className, type.nestedTypes)
                }
            }

            for (protoFile in schema.protoFiles) {
                val kotlinPackage = javaPackage(protoFile)
                putAll(kotlinPackage, null, protoFile.types)

                for (service in protoFile.services) {
                    val className = ClassName(kotlinPackage, service.type.simpleName)
                    this[service.type] = className
                }
            }

            this.putAll(BUILT_IN_TYPES)
        }

        super.handle(schema, context)
    }

    override fun handle(service: Service, context: Context): List<Path> {
        val generatedPaths = mutableListOf<Path>()

        val map = generateGrpcServerAdapter(service)
        for ((className, typeSpec) in map) {
            generatedPaths.add(write(className, typeSpec, service.type, service.location, context))
        }

        return generatedPaths
    }

    /**
     * Generates [TypeSpec]s for gRPC adapter for the given [service].
     *
     * These adapters allow us to use Wire based gRPC as io.grpc.BindableService
     */
    private fun generateGrpcServerAdapter(service: Service): Map<ClassName, TypeSpec> {
        return buildMap {
            val protoFile: ProtoFile? = schema.protoFile(service.location.path)
            val (grpcClassName, grpcSpec) =
                KotlinGrpcGenerator(
                    typeToKotlinName = typeToKotlinName,
                    singleMethodServices = singleMethodServices,
                    suspendingCalls = rpcSuspending,
                ).generateGrpcServer(service, protoFile, schema)
            put(grpcClassName, grpcSpec)
        }
    }

    private fun write(
        name: ClassName,
        typeSpec: TypeSpec,
        source: Any,
        location: Location,
        context: Context,
    ): Path {
        val modulePath = context.outDirectory
        val kotlinFile = FileSpec.builder(name.packageName, name.simpleName)
            .addFileComment(CODE_GENERATED_BY_WIRE)
            .addType(typeSpec)
            .build()
        val filePath = modulePath /
            kotlinFile.packageName.replace(".", "/") /
            "${kotlinFile.name}.kt"

        context.logger.artifactHandled(
            modulePath,
            "${kotlinFile.packageName}.${(kotlinFile.members.first() as TypeSpec).name}",
            "Kotlin",
        )
        try {
            context.fileSystem.createDirectories(filePath.parent!!)
            context.fileSystem.write(filePath) {
                writeUtf8(kotlinFile.toString())
            }
        } catch (e: IOException) {
            throw IOException(
                "Error emitting ${kotlinFile.packageName}.$source to ${context.outDirectory}",
                e,
            )
        }
        return filePath
    }

    override fun handle(extend: Extend, field: Field, context: Context): Path? = null
    override fun handle(type: Type, context: Context): Path? = null

    @Suppress("unused") // Used by the library's consumers.
    class Factory : SchemaHandler.Factory {
        override fun create(): SchemaHandler {
            error("Not used. Wire will remove it soon.")
        }

        override fun create(
            includes: List<String>,
            excludes: List<String>,
            exclusive: Boolean,
            outDirectory: String,
            options: Map<String, String>,
        ): SchemaHandler {
            return GrpcServerSchemaHandler(options)
        }
    }

    companion object {
        // Copies from Wire's `KotlinGenerator`.
        private val BUILT_IN_TYPES = mapOf(
            ProtoType.BOOL to BOOLEAN,
            ProtoType.BYTES to ByteString::class.asClassName(),
            ProtoType.DOUBLE to DOUBLE,
            ProtoType.FLOAT to FLOAT,
            ProtoType.FIXED32 to INT,
            ProtoType.FIXED64 to LONG,
            ProtoType.INT32 to INT,
            ProtoType.INT64 to LONG,
            ProtoType.SFIXED32 to INT,
            ProtoType.SFIXED64 to LONG,
            ProtoType.SINT32 to INT,
            ProtoType.SINT64 to LONG,
            ProtoType.STRING to String::class.asClassName(),
            ProtoType.UINT32 to INT,
            ProtoType.UINT64 to LONG,
            ProtoType.ANY to ClassName("com.squareup.wire", "AnyMessage"),
            ProtoType.DURATION to ClassName("com.squareup.wire", "Duration"),
            ProtoType.TIMESTAMP to ClassName("com.squareup.wire", "Instant"),
            ProtoType.EMPTY to ClassName("kotlin", "Unit"),
            ProtoType.STRUCT_MAP to ClassName("kotlin.collections", "Map")
                .parameterizedBy(ClassName("kotlin", "String"), STAR).copy(nullable = true),
            ProtoType.STRUCT_VALUE to ClassName("kotlin", "Any").copy(nullable = true),
            ProtoType.STRUCT_NULL to ClassName("kotlin", "Nothing").copy(nullable = true),
            ProtoType.STRUCT_LIST to ClassName("kotlin.collections", "List")
                .parameterizedBy(STAR).copy(nullable = true),
            ProtoType.DOUBLE_VALUE to DOUBLE.copy(nullable = true),
            ProtoType.FLOAT_VALUE to FLOAT.copy(nullable = true),
            ProtoType.INT64_VALUE to LONG.copy(nullable = true),
            ProtoType.UINT64_VALUE to LONG.copy(nullable = true),
            ProtoType.INT32_VALUE to INT.copy(nullable = true),
            ProtoType.UINT32_VALUE to INT.copy(nullable = true),
            ProtoType.BOOL_VALUE to BOOLEAN.copy(nullable = true),
            ProtoType.STRING_VALUE to String::class.asClassName().copy(nullable = true),
            ProtoType.BYTES_VALUE to ByteString::class.asClassName().copy(nullable = true),
        )

        private const val CODE_GENERATED_BY_WIRE =
            "Code generated by Wire protocol buffer compiler, do not edit."
    }
}
