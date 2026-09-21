pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.kikugie.dev/releases/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("dev.kikugie.loom-back-compat") version "0.4.2"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val commonVersions = providers.gradleProperty("stonecutter_common_versions").orNull?.split(",")?.map { it.trim() } ?: emptyList()
val fabricVersions = providers.gradleProperty("stonecutter_fabric_versions").orNull?.split(",")?.map { it.trim() } ?: emptyList()
val neoforgeVersions = providers.gradleProperty("stonecutter_neoforge_versions").orNull?.split(",")?.map { it.trim() } ?: emptyList()

val dists = mapOf(
    "common" to commonVersions,
    "fabric" to fabricVersions,
    "neoforge" to neoforgeVersions,
)
val uniqueVersions = dists.values.flatten().distinct()

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        versions(*uniqueVersions.toTypedArray())

        dists.forEach { (branchName, branchVersions) ->
            branch(branchName) {
                versions(*branchVersions.toTypedArray())
            }
        }

        vcsVersion = "26.3"
    }
}

rootProject.name = "CraftGR"
