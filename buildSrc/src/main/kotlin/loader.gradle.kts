plugins {
    id("java")
    id("idea")
    id("common")
}

val commonClasses: Configuration by configurations.creating {
    isCanBeResolved = true
}
val commonResources: Configuration by configurations.creating {
    isCanBeResolved = true
}

val commonPath = common.hierarchy.toString()
val commonProject = rootProject.project(commonPath)

dependencies {
    compileOnly(project(path = commonPath))
    commonClasses(project(path = commonPath, configuration = "commonClasses"))
    commonResources(project(path = commonPath, configuration = "commonResources"))
}

sourceSets {
    main {
        output.dir(
            mapOf("builtBy" to commonClasses),
            commonProject.layout.buildDirectory.dir("classes/java/main"),
        )
    }
}

tasks {
    classes {
        dependsOn(commonClasses)
    }

    processResources {
        dependsOn(commonResources)
        from(commonResources)
    }
}
