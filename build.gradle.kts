@file:Suppress("HardCodedStringLiteral")

import com.github.javaparser.printer.concretesyntaxmodel.CsmElement.token
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.internal.execution.caching.CachingState.enabled
import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String): String = providers.gradleProperty(key).get()
fun environment(key: String): Provider<String> = providers.environmentVariable(key)

plugins {
  id("java")
  alias(libs.plugins.kotlin)
  alias(libs.plugins.gradleIntelliJPlugin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
}

//val pluginGroup: String by project
//val pluginName: String by project
//val pluginVersion: String by project
//val pluginSinceBuild: String by project
//val pluginUntilBuild: String by project

//val pluginsVersion: String = properties("pluginsVersion")
//val rustVersion: String = properties("rustVersion")
//val llmVersion: String = properties("llmVersion")

val platformType: String = properties("platformType")
val platformVersion: String = properties("platformVersion")

val pluginName: String = properties("pluginName")
val pluginGroup: String = properties("pluginGroup")
//val pluginID: String = properties("pluginID")
val pluginVersion: String = properties("pluginVersion")
//val pluginDescription: String = properties("pluginDescription")
val pluginSinceBuild: String = properties("pluginSinceBuild")
val pluginUntilBuild: String = properties("pluginUntilBuild")

//val pluginCode: String = properties("pluginCode")
//val pluginReleaseDate: String = properties("pluginReleaseDate")
//val pluginReleaseVersion: String = properties("pluginReleaseVersion")
//
//val pluginVendorName: String = properties("pluginVendorName")
//val pluginVendorEmail: String = properties("pluginVendorEmail")
//val pluginVendorUrl: String = properties("pluginVendorUrl")
//
//val pluginChannels: String = properties("pluginChannels")

val javaVersion: String = properties("javaVersion")
val gradleVersion: String = properties("gradleVersion")

group = properties("pluginGroup")
version = properties("pluginVersion")

//val javaVersion: String by project

repositories {
  mavenCentral()
  mavenLocal()
  gradlePluginPortal()

  intellijPlatform {
    defaultRepositories()
    jetbrainsRuntime()
  }
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
  implementation("commons-io:commons-io:2.22.0")
  implementation("org.apache.commons:commons-lang3:3.20.0")

  intellijPlatform {
    create(platformType, platformVersion) {
      useInstaller = false
    }
    bundledModule("intellij.regexp")
    pluginVerifier()
    zipSigner()
  }
}

kotlin {
  jvmToolchain(javaVersion.toInt())
}

detekt {
  config.setFrom("./detekt-config.yml")
  buildUponDefaultConfig = true
  autoCorrect = true
}

changelog {
  path.set("${project.projectDir}/docs/CHANGELOG.md")
  version.set(properties("pluginVersion"))
  itemPrefix.set("-")
  keepUnreleasedSection.set(true)
  unreleasedTerm.set("Changelog")
  groups.set(listOf("Features", "Fixes", "Removals", "Other"))
}

intellijPlatform {
  pluginConfiguration {
    id = pluginGroup
    name = pluginName
    version = pluginVersion

    ideaVersion {
      sinceBuild = pluginSinceBuild
      untilBuild = pluginUntilBuild
    }

    val changelog = project.changelog // local variable for configuration cache compatibility
    changeNotes = provider {
      with(changelog) {
        renderItem(
          (getOrNull(pluginVersion) ?: getUnreleased())
            .withHeader(false)
            .withEmptySections(false),
          Changelog.OutputType.HTML,
        )
      }
    }
  }

  publishing {
    token = environment("INTELLIJ_PUBLISH_TOKEN")
    channels = listOf(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
  }

  signing {
    certificateChain = environment("CERTIFICATE_CHAIN")
    privateKey = environment("PRIVATE_KEY")
    password = environment("PRIVATE_KEY_PASSWORD")
  }
}

tasks {
  wrapper {
    gradleVersion = "9.7.1"
  }

  buildSearchableOptions {
    enabled = false
  }

  withType<JavaCompile> {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked")
  }

  withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(javaVersion))
  }

  withType<Detekt> {
    jvmTarget = javaVersion
  }
}
