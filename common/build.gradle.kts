plugins {
    common
    alias(libs.plugins.loom.back.compat)
    alias(libs.plugins.blossom)
}

stonecutter {
}

val stonecutterGenerate = tasks.named("stonecutterGenerate")

tasks.configureEach {
    if (name == "generateJavaTemplates" ||
        name == "validateAccessWidener") {
        dependsOn(stonecutterGenerate)
    }
}

loom {
    val accessWidener = sourceSets.main.get().resources.srcDirs
        .map { it.resolve("craftgr.${sc.current.version}.accesswidener") }
        .firstOrNull { it.isFile && it.exists() }

    accessWidener?.let {
        accessWidenerPath.set(accessWidener)
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.minecraft}")
    if (sc.current.parsed < "26") {
        mappings(loom.layered {
            officialMojangMappings()
            commonMod.depOrNull("parchment")?.let {
                parchment("org.parchmentmc.data:parchment-${commonMod.dep("minecraft")}:${it}@zip")
            }
        })
    }

    compileOnly(libs.mixin)

    modCompileOnly("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-fabric")

    compileOnly(libs.jlayer)
    compileOnly(libs.math3)
}

sourceSets {
    main {
        val javaTemplateDirectories = java.srcDirs.map { it.parentFile.resolve("java-templates") }

        blossom {
            javaSources {
                templates(javaTemplateDirectories)
                property("mod_name", commonMod.name)
                property("mod_id", commonMod.id)
            }
        }
    }
}

val commonClasses: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    val compileJava = tasks.named<JavaCompile>("compileJava")
    val processResources = tasks.named<ProcessResources>("processResources")

    add(commonClasses.name, compileJava.flatMap { it.destinationDirectory }) {
        builtBy(compileJava)
    }
    add(commonResources.name, processResources.map { it.destinationDir }) {
        builtBy(processResources)
    }
}
