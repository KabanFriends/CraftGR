plugins {
    id("io.github.kabanfriends.craftgr.build.loader")
    alias(libs.plugins.fabric.loom)
}

/* Project Properties */
val modId               = project.property("mod_id")                as String

dependencies {
    minecraft(libs.minecraft)

    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

    implementation(libs.mod.menu)
    compileOnly(libs.yacl.neoforge) // TODO: use fabric / use implementation for test runs

    implementation(libs.jlayer)
    include(libs.jlayer)
    implementation(libs.math3)
    include(libs.math3)
}

loom {
    val aw = file("src/main/resources/${modId}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }

    runs {
        named("client") {
            client()
            ideConfigGenerated(true)
            runDir("run/client")
            configName = "Fabric Client"
        }
        named("server") {
            server()
            ideConfigGenerated(true)
            runDir("run/server")
            configName = "Fabric Server"
        }
    }
}

val attribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { it ->
    configurations.findByName(it)?.let { cfg ->
        cfg.attributes {
            attribute(attribute, "fabric")
        }
    }
}
sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(attribute, "fabric")
            }
        }
    }
}
