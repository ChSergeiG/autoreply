package ru.chsergeig.autoreply.client.service

import dev.voroby.springframework.telegram.client.TdApi.Message
import ru.chsergeig.autoreply.client.dto.CurrentSessionStatistics
import ru.chsergeig.autoreply.client.enumeration.AutoreplyStatus
import java.time.LocalDateTime

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
     * Message statistics
     */
    var statistics: CurrentSessionStatistics?

    /**
     * Chat IDs that was reply`ed with last reply timestamp
     */
    var responseChatList: MutableMap<Long, LocalDateTime>

    fun saveNewMessage(message: Message)

    fun processNewMessages()

}
