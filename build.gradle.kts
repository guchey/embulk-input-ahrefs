plugins {
    kotlin("jvm") version "1.8.0"
    id("maven-publish")
    id("jacoco")
    id("org.embulk.embulk-plugins") version "0.5.5"
    id("com.diffplug.spotless") version "6.11.0"
}

group = "io.github.guchey.embulk.input.ahrefs"
version = "0.1.0-ALPHA"
var embulkVersion = "0.10.31"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.embulk:embulk-api:${embulkVersion}")
    compileOnly("org.embulk:embulk-spi:${embulkVersion}")

    implementation("org.embulk:embulk-util-config:0.3.1") {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
        exclude(group = "com.fasterxml.jackson.datatype", module = "jackson-datatype-jdk8")
        exclude(group = "javax.validation", module = "validation-api")
    }
    implementation("org.embulk:embulk-base-restclient:0.10.1") {
        exclude(group = "org.embulk", module = "embulk-core")
    }

    implementation("org.embulk:embulk-util-retryhelper-jaxrs:0.8.1")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation(kotlin("test"))
    testImplementation("org.embulk:embulk-api:${embulkVersion}")
    testImplementation("org.embulk:embulk-spi:${embulkVersion}")
    testImplementation("org.embulk:embulk-junit4:${embulkVersion}")

    testImplementation("org.embulk:embulk-deps:${embulkVersion}")

    testImplementation("io.mockk:mockk:1.13.7")
}

embulkPlugin {
    mainClass.set("io.github.guchey.embulk.input.ahrefs.AhrefsInputPlugin")
    category.set("input")
    type.set("ahrefs")
}

publishing {
    publications {
        register("embulkPluginMaven", MavenPublication::class) {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("${project.buildDir}/mavenPublishLocal")
        }
    }
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
        googleJavaFormat().aosp()
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}