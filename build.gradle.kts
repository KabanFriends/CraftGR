plugins {
    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.neoforge.moddev) apply false
}

/* Project Properties */
val modGroup            = project.property("mod_group")             as String
val modId               = project.property("mod_id")                as String
val modVersion          = project.property("mod_version")           as String

allprojects {
    group = modGroup
    version = modVersion
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://modmaven.dev/")
        maven("https://maven.quiltmc.org/repository/release")
        maven("https://maven.terraformersmc.com/")
        maven("https://jitpack.io/")
    }
}
