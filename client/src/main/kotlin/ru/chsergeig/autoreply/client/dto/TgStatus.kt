package ru.chsergeig.autoreply.client.dto

data class TgStatus(
    var isClosed: Boolean,
    var isWaitCode: Boolean,
    var isWaitPassword: Boolean,
    var isWaitEmail: Boolean,
    var haveAuthorization: Boolean,
    var statistics: CurrentSessionStatistics? = null,
)
