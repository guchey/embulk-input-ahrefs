plugins {
    kotlin("jvm") version "1.8.0"
    id("maven-publish")
    id("jacoco")
    id("org.embulk.embulk-plugins") version "0.5.5"
    id("com.diffplug.spotless") version "6.11.0"
    signing
}

group = "io.github.guchey.embulk.input.ahrefs"
version = "0.1.1-SNAPSHOT"
description = "embulk-input-ahrefs is the gem preparing Embulk input plugins"
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

    testImplementation("io.mockk:mockk:1.13.8")
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
        register("maven", MavenPublication::class) {
            groupId = project.group.toString()
            artifactId = project.name

            from(components["java"])

            pom {  // https://central.sonatype.org/pages/requirements.html
                packaging = "jar"

                name = project.name
                description = project.description.toString()
                // url = "https://github.com/your-github-username/your-plugin-name"

                licenses {
                    // Maven Central requires explicit license specification.
                    // This is an example of the MIT License.
                    license {
                        // http://central.sonatype.org/pages/requirements.html#license-information
                        name = "MIT License"
                        url = "http://www.opensource.org/licenses/mit-license.php"
                    }
                }

                developers {
                    developer {
                        name = "Shingen Taguchi"
                        email = "taguchi8shingen@gmail.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/guchey/embulk-input-ahrefs.git"
                    developerConnection = "scm:git:git@github.com/guchey/embulk-input-ahrefs.git"
                    url = "https://github.com/guchey/embulk-input-ahrefs"
                }
            }
        }
    }
    repositories {
        maven {
            name = "mavenCentral"
            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            } else {
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            }

            // This setting assumes that "~/.gradle/gradle.properties" has "ossrhUsername" and "ossrhPassword".
            // Please put your Sonatype OSSRH username and password in "~/.gradle/gradle.properties".
            // https://central.sonatype.org/publish/publish-gradle/#credentials
            credentials {
                username = if (project.hasProperty("ossrhUsername")) project.property("ossrhUsername").toString() else ""
                password = if (project.hasProperty("ossrhPassword")) project.property("ossrhPassword").toString() else ""
            }
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

// Maven Central requires PGP signature.
// https://central.sonatype.org/publish/requirements/gpg/
// https://central.sonatype.org/publish/publish-gradle/#credentials
signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

tasks.register("generateEmbulkProperties") {
    doLast {
        val embulkDir = file(".embulk")
        embulkDir.mkdirs()
        val propFile = file(".embulk/embulk.properties")
        propFile.writeText("m2_repo=${System.getProperty("user.home")}/.m2/repository\nplugins.input.ahrefs=maven:${project.group}:ahrefs:${project.version}")
    }
}

tasks.withType<Sign>().configureEach {
    onlyIf { !gradle.taskGraph.hasTask("publishToMavenLocal") }
}