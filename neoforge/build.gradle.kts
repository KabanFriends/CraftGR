plugins {
    loader
    alias(libs.plugins.moddev)
}

val projectPath = project.path

val stonecutterGenerate = tasks.named("stonecutterGenerate")

tasks.configureEach {
    if (name == "createMinecraftArtifacts") {
        dependsOn(stonecutterGenerate)
    }
}

neoForge {
    val accessTransformer = sourceSets.main.get().resources.srcDirs
        .map { it.resolve("META-INF/accesstransformer.${sc.current.version}.cfg") }
        .firstOrNull { it.isFile && it.exists() }

    accessTransformer?.let {
        accessTransformers.from(accessTransformer)
    }

    enable {
        version = commonMod.dep("neoforge")
    }

    runs {
        register("client") {
            client()
            ideName = "NeoForge Client ($projectPath)"
        }
    }

    commonMod.depOrNull("parchment")?.let {
        parchment {
            mappingsVersion = it
            minecraftVersion = commonMod.dep("minecraft")
        }
    }

    mods {
        register(commonMod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    implementation("net.neoforged:neoforge:${commonMod.dep("neoforge")}")

    compileOnly("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-neoforge")

    implementation(libs.jlayer)
    jarJar(libs.jlayer)
    implementation(libs.math3)
    jarJar(libs.math3)
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }
}
