plugins {
    id("java")
    id("java-library")
    id("idea")
}

version = "${loader}-${commonMod.version}+mc${stonecutterBuild.current.version}"

base {
    archivesName = commonMod.id
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(commonProject.prop("java.version")!!)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.minecraftforge.net/")
    maven("https://maven.quiltmc.org/repository/release")
    maven("https://maven.parchmentmc.org")
    maven("https://repo.spongepowered.org/repository/maven-public")
    maven("https://maven.terraformersmc.com/")
}

tasks {
    processResources {
        val expandProps = mapOf(
            "stonecutterVersion" to stonecutterBuild.current.version,

            "javaVersion" to commonMod.propOrNull("java.version"),

            "modId" to commonMod.id,
            "modName" to commonMod.name,
            "modVersion" to commonMod.version,
            "modGroup" to commonMod.group,
            "modDescription" to commonMod.description,
            "modAuthors" to commonMod.authors,
            "modLicense" to commonMod.license,
            "modWebsite" to commonMod.website,
            "modSource" to commonMod.source,
            "modIssues" to commonMod.issues,

            "minecraftVersion" to commonMod.minecraft,

            "fabricLoaderVersion" to commonMod.depOrNull("fabric-loader"),
            "fabricApiVersion" to commonMod.depOrNull("fabric-api"),

            "neoforgeVersion" to commonMod.depOrNull("neoforge"),

            "yaclVersion" to commonMod.depOrNull("yacl"),
            "modMenuVersion" to commonMod.depOrNull("modmenu"),
        ).filterValues { it?.isNotEmpty() == true }.mapValues { (_, v) -> v!! }

        filesMatching(listOf(
            "fabric.mod.json",
            "META-INF/mods.toml",
            "META-INF/neoforge.mods.toml",
        )) {
            expand(expandProps)
        }

        inputs.properties(expandProps)
    }
}

tasks.named("processResources") {
    dependsOn(":common:${commonMod.minecraft}:stonecutterGenerate")
}
