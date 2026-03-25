plugins {
    id("io.github.kabanfriends.craftgr.build.common")
    alias(libs.plugins.neoforge.moddev)
}

neoForge {
    neoFormVersion = libs.versions.neoform.get() as String?

    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras)
    annotationProcessor(libs.mixinextras)

    compileOnly(libs.jlayer)
    compileOnly(libs.math3)

    compileOnly(libs.yacl.fabric)
}

configurations {
    val commonJava by creating {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    val commonResources by creating {
        isCanBeResolved = true
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}

val attribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { it ->
    configurations.findByName(it)?.let { cfg ->
        cfg.attributes {
            attribute(attribute, "common")
        }
    }
}

sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(attribute, "common")
            }
        }
    }
}
