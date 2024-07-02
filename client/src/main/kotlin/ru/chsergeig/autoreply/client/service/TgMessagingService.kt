package ru.chsergeig.autoreply.client.service

import dev.voroby.springframework.telegram.client.TdApi.Message
import ru.chsergeig.autoreply.client.dto.CurrentSessionStatistics
import ru.chsergeig.autoreply.client.enum.AutoreplyStatus

interface TgMessagingService {
    var actualMessage: String?
    var status: AutoreplyStatus?

    fun saveNewMessage(message: Message)

    fun processNewMessages()

    fun getStatistics() : CurrentSessionStatistics
}
