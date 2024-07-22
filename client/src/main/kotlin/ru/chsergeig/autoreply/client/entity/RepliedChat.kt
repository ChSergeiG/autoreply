package ru.chsergeig.autoreply.client.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class RepliedChat(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var chatId: Long = 0,
    var repliedTime: LocalDateTime = LocalDateTime.now(),

)
