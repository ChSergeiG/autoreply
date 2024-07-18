package ru.chsergeig.autoreply.client.service

import dev.voroby.springframework.telegram.client.TdApi.Message
import ru.chsergeig.autoreply.client.dto.CurrentSessionStatistics
import ru.chsergeig.autoreply.client.enum.AutoreplyStatus
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

interface TgMessagingService {
    /**
     * Actual reply message
     */
    var actualMessage: String?

    /**
     * Replyer status
     */
    var status: AutoreplyStatus?

    /**
     * Chat IDs that was reply`ed with last reply timestamp
     */
    var responseChatList: MutableMap<Long, LocalDateTime>

    fun saveNewMessage(message: Message)

    fun processNewMessages()

    fun getStatistics() : CurrentSessionStatistics
}
