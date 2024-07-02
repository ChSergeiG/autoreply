package ru.chsergeig.autoreply.client.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.user")
data class UserProperties(
    var default: UserDefaultProperties,
    var security: UserSecurityProperties,
)

data class UserDefaultProperties(
    var enabled: Boolean,
    var message: String,
)

data class UserSecurityProperties(
    var username: String,
    var password: String,
)
