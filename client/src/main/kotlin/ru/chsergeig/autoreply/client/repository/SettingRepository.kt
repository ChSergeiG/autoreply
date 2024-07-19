package ru.chsergeig.autoreply.client.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.chsergeig.autoreply.client.entity.Setting
import java.util.UUID

@Repository
interface SettingRepository : CrudRepository<Setting, UUID> {

    sealed class SettingKey {
        companion object {
            @JvmStatic
            val appMessage: String = "MESSAGE"

            @JvmStatic
            val appState: String = "STATE"

            @JvmStatic
            val commonMessagesRead: String = "COMMON_MESSAGES_READ"

            @JvmStatic
            val privateMessagesRead: String = "PRIVATE_MESSAGES_READ"

            @JvmStatic
            val privateMessagesResponses: String = "PRIVATE_MESSAGES_RESPONSES"
        }
    }

    fun findBySettingKey(
        key: String
    ): Setting?

}
