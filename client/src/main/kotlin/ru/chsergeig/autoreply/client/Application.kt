package ru.chsergeig.autoreply.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan(
    basePackages = [
        "dev.voroby.springframework.telegram.properties",
        "org.springframework.boot.autoconfigure.security",
        "ru.chsergeig.autoreply.client.properties",
    ],
)
@SpringBootApplication
class MainApplication

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}
