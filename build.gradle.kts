plugins {
    java
    id("fabric-loom")
}

val minecraftVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val projectVersion: String by project

val isRelease = System.getenv("BUILD_RELEASE").toBoolean()
val isActions = System.getenv("GITHUB_ACTIONS").toBoolean()

group = "net.kjp12"
version = when {
    isRelease -> projectVersion
    isActions -> "$projectVersion-build.${System.getenv("GITHUB_RUN_NUMBER")}-commit.${System.getenv("GITHUB_SHA").substring(0, 7)}-branch.${System.getenv("GITHUB_REF")?.substring(11)?.replace('/', '.') ?: "unknown"}"
    else -> "$projectVersion-build.local"
}

repositories {
    mavenCentral()
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "Minecraft"
        url = uri("https://libraries.minecraft.net/")
    }
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    minecraft("com.mojang", "minecraft", minecraftVersion)
    mappings("net.fabricmc", "yarn", yarnMappings, classifier = "v2")
    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
    // I'm sorry...
    include(modImplementation("com.github.the-glitch-network", "minecraft-gudasm", "v0.3.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.isDeprecation = true
        options.isWarnings = true
    }
    processResources {
        val map = mapOf(
            "version" to project.version,
            "project_version" to projectVersion
        )
        inputs.properties(map)

        filesMatching("fabric.mod.json") {
            expand(map)
        }
    }
    withType<Jar> {
        from("LICENSE")
    }
    test {
        useJUnitPlatform()
    }
}