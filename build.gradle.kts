import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinpoetVersion: String by project
val symbolProcessingApiVersion: String by project
val autoServiceAnnotationsVersion: String by project
val autoServiceKspVersion: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin"))
    }
}

group = "dev.viesoft"
version = "5.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation("com.squareup", "kotlinpoet", kotlinpoetVersion)
    implementation("com.squareup", "kotlinpoet-ksp", kotlinpoetVersion)
    implementation("com.google.devtools.ksp", "symbol-processing-api", symbolProcessingApiVersion)
    implementation("com.google.auto.service", "auto-service-annotations", autoServiceAnnotationsVersion)
    ksp("dev.zacsweers.autoservice", "auto-service-ksp", autoServiceKspVersion)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.viesoft"
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
