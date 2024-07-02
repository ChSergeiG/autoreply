package ru.chsergeig.autoreply.client.dto

data class CurrentSessionStatistics(
    var chatMessagesCount: Int = 0,
    var privateMessagesCount: Int = 0,
)
