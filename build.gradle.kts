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

fun evalGradleParam(key: String): String {
    return when {
        System.getProperty(key) != null && System.getProperty(key).isNotBlank() -> System.getProperty(key)
        System.getenv()[key] != null && System.getenv()[key]!!.isNotBlank() -> System.getenv()[key]!!
        properties[key] != null && (properties[key] as String).isNotBlank() -> properties[key] as String
        else -> key
    }
}
