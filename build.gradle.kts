import com.diffplug.gradle.spotless.SpotlessExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  dependencies {
    classpath(libs.pluginz.binaryCompatibilityValidator)
    classpath(platform(libs.wire.bom))
    classpath(libs.wire.gradlePlugin)
    classpath(libs.pluginz.kotlin)
    classpath(libs.pluginz.kotlinSerialization)
    classpath(libs.pluginz.spotless)
    classpath(libs.vanniktechPublishPlugin)
  }

  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

apply(plugin = "com.vanniktech.maven.publish.base")

allprojects {
  group = project.property("GROUP") as String
  version = project.property("VERSION_NAME") as String

  repositories {
    mavenCentral()
    google()
  }

  // Prefer to get dependency versions from BOMs.
  configurations.all {
    val configuration = this
    configuration.dependencies.all {
      val bom = when (group) {
        "com.squareup.okio" -> libs.okio.bom.get()
        "com.squareup.okhttp3" -> libs.okhttp.bom.get()
        "com.squareup.wire" -> libs.wire.bom.get()
        else -> return@all
      }
      configuration.dependencies.add(project.dependencies.platform(bom))
    }
  }

  tasks.withType<Jar>().configureEach {
    if (name == "jar") {
      manifest {
        attributes("Automatic-Module-Name" to project.name)
      }
    }
  }

  plugins.withId("com.vanniktech.maven.publish.base") {
    configure<PublishingExtension> {
      repositories {
        maven {
          name = "test"
          setUrl("file://${project.rootProject.buildDir}/localMaven")
        }
        /**
         * Want to push to an internal repository for testing?
         * Set the following properties in ~/.gradle/gradle.properties.
         *
         * internalUrl=YOUR_INTERNAL_URL
         * internalUsername=YOUR_USERNAME
         * internalPassword=YOUR_PASSWORD
         */
        val internalUrl = providers.gradleProperty("internalUrl")
        if (internalUrl.isPresent) {
          maven {
            name = "internal"
            setUrl(internalUrl)
            credentials(PasswordCredentials::class)
          }
        }
      }
    }

    configure<MavenPublishBaseExtension> {
      publishToMavenCentral(SonatypeHost.S01)
      val inMemoryKey = project.findProperty("signingInMemoryKey") as String?
      if (!inMemoryKey.isNullOrEmpty()) {
        signAllPublications()
      }
      pom {
        description.set("gRPC and protocol buffers for Android, Kotlin, and Java.")
        name.set(project.name)
        url.set("https://github.com/square/wire-grpc-server/")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            distribution.set("repo")
          }
        }
        developers {
          developer {
            id.set("square")
            name.set("Square, Inc.")
          }
        }
        scm {
          url.set("https://github.com/square/wire-grpc-server/")
          connection.set("scm:git:https://github.com/square/wire-grpc-server.git")
          developerConnection.set("scm:git:ssh://git@github.com/square/wire-grpc-server.git")
        }
      }
    }
  }
}

subprojects {
  apply(plugin = "com.diffplug.spotless")
  configure<SpotlessExtension> {
    setEnforceCheck(false)
    kotlin {
      target("**/*.kt")
      ktlint(libs.versions.ktlint.get()).userData(kotlin.collections.mapOf("indent_size" to "2"))
      trimTrailingWhitespace()
      endWithNewline()
      toggleOffOn()
    }
  }

  // The `application` plugin internally applies the `distribution` plugin and
  // automatically adds tasks to create/publish tar and zip artifacts.
  // https://docs.gradle.org/current/userguide/application_plugin.html
  // https://docs.gradle.org/current/userguide/distribution_plugin.html#sec:publishing_distributions_upload
  plugins.withType(DistributionPlugin::class) {
    tasks.findByName("distTar")?.enabled = false
    tasks.findByName("distZip")?.enabled = false
    configurations["archives"].artifacts.removeAll {
      val file: File = it.file
      file.name.contains("tar") || file.name.contains("zip")
    }
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "1.8"
      // Disable optimized callable references. See https://youtrack.jetbrains.com/issue/KT-37435
      freeCompilerArgs += "-Xno-optimized-callable-references"
    }
  }

  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
    options.encoding = Charsets.UTF_8.toString()
  }

  tasks.withType<Test> {
    testLogging {
      events(STARTED, PASSED, SKIPPED, FAILED)
      exceptionFormat = TestExceptionFormat.FULL
      showStandardStreams = false
    }
  }

  if (!(
        project.name.endsWith("-bom") ||
        project.displayName.contains("sample")
      )
  ) {
    apply(plugin = "checkstyle")

    afterEvaluate {
      configure<CheckstyleExtension> {
        toolVersion = "7.7"
        sourceSets = listOf(project.extensions.getByType<SourceSetContainer>()["main"])
      }
    }
  }
}
