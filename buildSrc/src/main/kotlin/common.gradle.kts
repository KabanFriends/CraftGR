package io.github.kabanfriends.craftgr.build

plugins {
    `java-library`
}

/* Project Properties */
val modName             = project.property("mod_name")              as String
val modId               = project.property("mod_id")                as String

val minecraftVersion = versionCatalogs.named("libs").findVersion("minecraft").get().toString()

base {
    archivesName.set("${modId}-${project.name}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
}

listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { it ->
    configurations.findByName(it)?.let { cfg ->
        cfg.outgoing {
            capability("${group}:${project.name}:${version}")
            capability("${group}:${base.archivesName.get()}:${version}")
            capability("${group}:${modId}-${project.name}-${minecraftVersion}:${version}")
            capability("${group}:${modId}:${version}")
        }
    }
}

tasks {
    jar {
        from(rootProject.file("LICENSE")) {
            into("/")
        }

        manifest {
            attributes(
                "Specification-Title"   to project.name,
                "Specification-Version" to project.version,
                "Implementation-Title"  to tasks.jar.get().archiveVersion,
                "Implementation-Title"  to project.name,
            )
        }

        archiveClassifier.set("mc${minecraftVersion}")
    }

    processResources {
        val props = mapOf(
            "version"               to project.version,
            "name"                  to modName,
            "id"                    to modId,
            "minecraft_version"     to minecraftVersion.replace("rc-", "rc."),
            "fabric_loader_version" to versionCatalogs.named("libs").findVersion("fabric-loader").get().toString(),
            "fabric_api_version"    to versionCatalogs.named("libs").findVersion("fabric-api").get().toString(),
            "neoforge_version"      to versionCatalogs.named("libs").findVersion("neoforge-loader").get().toString(),
            "mod_menu_version"      to versionCatalogs.named("libs").findVersion("mod-menu").get().toString(),
            "yacl_version_fabric"   to versionCatalogs.named("libs").findVersion("yacl-fabric").get().toString(),
            "yacl_version_neoforge" to versionCatalogs.named("libs").findVersion("yacl-neoforge").get().toString(),
        )

        filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
             expand(props)
        }

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
            expand(props)
        }

        inputs.properties(props)
    }
}
