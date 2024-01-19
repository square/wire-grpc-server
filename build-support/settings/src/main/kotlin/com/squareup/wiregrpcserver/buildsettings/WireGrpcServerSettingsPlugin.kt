package com.squareup.wiregrpcserver.buildsettings

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

@Suppress("unused") // Invoked reflectively by Gradle.
class WireGrpcServerSettingsPlugin : Plugin<Settings> {
  override fun apply(target: Settings) {
    fun applyWireGrpcServerBuildPlugin(project: Project) {
      project.plugins.apply("com.squareup.wiregrpcserver.build")
    }

    target.gradle.allprojects {
      if (project.path == ":") {
        // The root project needs to evaluate the buildscript classpath before applying.
        // Once we move to the plugins DSL in the main build we can remove this conditional.
        project.afterEvaluate(::applyWireGrpcServerBuildPlugin)
      } else {
        project.beforeEvaluate(::applyWireGrpcServerBuildPlugin)
      }
    }
  }
}
