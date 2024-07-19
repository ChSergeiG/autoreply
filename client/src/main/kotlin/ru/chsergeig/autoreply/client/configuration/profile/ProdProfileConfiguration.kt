package ru.chsergeig.autoreply.client.configuration.profile

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableAutoConfiguration
@Profile("prod")
class ProdProfileConfiguration {

}