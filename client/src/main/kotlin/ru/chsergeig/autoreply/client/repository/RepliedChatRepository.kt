package ru.chsergeig.autoreply.client.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.chsergeig.autoreply.client.entity.RepliedChat
import java.time.ZonedDateTime
import java.util.UUID

@Repository
interface RepliedChatRepository :
    CrudRepository<RepliedChat, UUID> {

    fun deleteRepliedChatByRepliedTimeBefore(
        date: ZonedDateTime,
    )

    fun existsRepliedChatByChatId(
        chatId: Long,
    ): Boolean
}
