package io.github.kabanfriends.craftgr.build

plugins {
    id("io.github.kabanfriends.craftgr.build.common")
}

/* Project Properties */
val modId               = project.property("mod_id")                as String

configurations {
    val commonJava by creating {
        isCanBeResolved = true
    }
    val commonResources by creating {
        isCanBeResolved = true
    }
}

dependencies {
    compileOnly(project(":common")) {
        capabilities {
            requireCapability("${group}:${modId}")
        }
        attributes {
            attribute(
                Attribute.of("io.github.mcgradleconventions.loader", String::class.java),
                "common"
            )
        }
    }

    "commonJava"(project(path = ":common", configuration = "commonJava"))
    "commonResources"(project(path = ":common", configuration = "commonResources"))
}

tasks {
    processResources {
        dependsOn(configurations["commonResources"])
        from(configurations["commonResources"])
    }

    named("compileJava", JavaCompile::class) {
        dependsOn(configurations["commonJava"])
        source(configurations["commonJava"])
    }

    named("javadoc", Javadoc::class) {
        dependsOn(configurations["commonJava"])
        source(configurations["commonJava"])
    }

    named("sourcesJar", Jar::class) {
        dependsOn(configurations["commonJava"])
        from(configurations["commonJava"])
        dependsOn(configurations["commonResources"])
        from(configurations["commonResources"])
    }
}
