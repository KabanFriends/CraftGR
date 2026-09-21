plugins {
    loader
    alias(libs.plugins.loom.back.compat)
}

loom {
    runs {
        named("client") {
            client()
            ideConfigGenerated(true)
            configName = "Fabric Client"
        }
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

    modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric-api")}")

    modImplementation("com.terraformersmc:modmenu:${commonMod.dep("modmenu")}")
    modImplementation("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-fabric")

    implementation(libs.jlayer)
    include(libs.jlayer)
    implementation(libs.math3)
    include(libs.math3)
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }
}
