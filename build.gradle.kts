plugins {
    java
    kotlin("jvm") version "1.9.24" apply false
    kotlin("plugin.spring") version "1.9.24" apply false
    id("com.diffplug.spotless") version "7.0.0.BETA1"
}

val login = evalGradleParam("GH_LOGIN")
val token = evalGradleParam("GH_TOKEN")

allprojects {
    version = project.properties["version"]!!
    group = project.properties["group"]!!

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/p-vorobyev/*")
            credentials {
                username = login
                password = token
            }
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint("0.50.0")
        }
        kotlin {
            ktlint("0.50.0")
        }
        sql {
            target("src/main/**/*.sql")
            dbeaver()
        }
        yaml {
            target("src/main/**/*.yml", "src/main/**/*.yaml")
            jackson()
        }
    }
}

tasks {
    register("increment", DefaultTask::class) {
        val newVersion = try {
            val versionChunk = "$version".split(".")
            val resultChunk = mutableListOf<String>()
            val length = versionChunk.size
            var cursor = 0
            while (cursor < length) {
                if (cursor == length - 1) {
                    resultChunk.add("${versionChunk.last().toInt() + 1}")
                } else {
                    resultChunk.add(versionChunk[cursor])
                }
                cursor++
            }
            resultChunk.joinToString(separator = ".")
        } catch (ignore: Exception) {
            version
        }
        val propertiesLine = file("gradle.properties")
            .readLines()
            .joinToString(separator = "\n") {
                if (it.startsWith("version=")) {
                    return@joinToString "version=$newVersion"
                } else {
                    return@joinToString it
                }
            }
        file("gradle.properties").writeText("$propertiesLine\n")
        val yamlLines = file(".github/workflows/docker-publish.yml")
            .readLines()
            .joinToString(separator = "\n") {
                if (it.contains("tags:")) {
                    return@joinToString "          tags: chsergeig/tg-autoreply:$newVersion,chsergeig/tg-autoreply:latest"
                } else {
                    return@joinToString it
                }
            }
        file(".github/workflows/docker-publish.yml").writeText("$yamlLines\n")
    }
}

fun evalGradleParam(key: String): String {
    return when {
        System.getProperty(key) != null && System.getProperty(key).isNotBlank() -> System.getProperty(key)
        System.getenv()[key] != null && System.getenv()[key]!!.isNotBlank() -> System.getenv()[key]!!
        properties[key] != null && (properties[key] as String).isNotBlank() -> properties[key] as String
        else -> key
    }
}
