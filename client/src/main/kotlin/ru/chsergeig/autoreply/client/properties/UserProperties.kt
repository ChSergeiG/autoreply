package ru.chsergeig.autoreply.client.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.user")
data class UserProperties(
    var default: UserDefaultProperties,
)

data class UserDefaultProperties(
    var enabled: Boolean,
    var message: String,
)
