plugins {
    id("io.github.kabanfriends.craftgr.build.loader")
    alias(libs.plugins.neoforge.moddev)
}

/* Project Properties */
val modId               = project.property("mod_id")                as String

dependencies {
    implementation(libs.neoforge.loader)

    compileOnly(libs.yacl.neoforge) // TODO: use implementation for test runs

    implementation(libs.jlayer)
    jarJar(libs.jlayer)
    implementation(libs.math3)
    jarJar(libs.math3)
}

neoForge {
    version = libs.versions.neoforge.loader.get() as String?

    val at = file("${rootDir}/common/src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }

    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            ideName = "NeoForge ${name.capitalize()} (${project.path})"
        }
        create("client") {
            client()
        }
        create("data") {
            clientData()
        }
        create("server") {
            server()
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

val attribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { it ->
    configurations.findByName(it)?.let { cfg ->
        cfg.attributes {
            attribute(attribute, "neoforge")
        }
    }
}
sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(attribute, "neoforge")
            }
        }
    }
}
