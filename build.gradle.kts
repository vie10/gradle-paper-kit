import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin"))
    }
}

group = "online.viestudio"
version = "4.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation("com.squareup", "kotlinpoet", "1.12.0")
    implementation("com.squareup", "kotlinpoet-ksp", "1.12.0")
    implementation("com.google.devtools.ksp", "symbol-processing-api", "1.7.10-1.0.6")
    implementation("com.google.auto.service", "auto-service-annotations", "1.0.1")
    ksp("dev.zacsweers.autoservice", "auto-service-ksp", "1.0.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "online.viestudio"
            artifactId = "paper-kit"
            version = version

            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}

with(tasks) {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}-plugin.jar")
    }
}