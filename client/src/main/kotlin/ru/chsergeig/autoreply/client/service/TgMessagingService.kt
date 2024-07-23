package ru.chsergeig.autoreply.client.service

import dev.voroby.springframework.telegram.client.TdApi.Message
import ru.chsergeig.autoreply.client.dto.CurrentSessionStatistics
import ru.chsergeig.autoreply.client.enumeration.AutoreplyStatus

interface TgMessagingService {

    fun saveNewMessage(
        message: Message,
    )

    fun processNewMessages()

    fun updateReplyMessage(
        newMessage: String,
    )

    fun updateReplierStatus(
        newStatus: AutoreplyStatus,
    )

    fun getStatistics(): CurrentSessionStatistics

    fun removeOldRepliesFromList()
}
