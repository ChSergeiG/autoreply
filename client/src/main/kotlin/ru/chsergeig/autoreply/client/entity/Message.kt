package ru.chsergeig.autoreply.client.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.ZonedDateTime
import java.util.UUID

@Entity
data class Message(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var messageId: Long = 0,
    var chatId: Long = 0,
    var senderId: Long = 0,
    var messageContent: String = "",
    var messageTime: ZonedDateTime = ZonedDateTime.now(),

)
