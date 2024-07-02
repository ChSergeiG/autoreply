package ru.chsergeig.autoreply.client.impl

import dev.voroby.springframework.telegram.client.TdApi
import dev.voroby.springframework.telegram.client.TdApi.Message
import dev.voroby.springframework.telegram.client.TdApi.MessageSenderChat
import dev.voroby.springframework.telegram.client.TdApi.MessageSenderUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import ru.chsergeig.autoreply.client.component.TgClientComponent
import ru.chsergeig.autoreply.client.dto.CurrentSessionStatistics
import ru.chsergeig.autoreply.client.enum.AutoreplyStatus
import ru.chsergeig.autoreply.client.service.TgMessagingService
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class TgMessagingServiceImpl(
    @Lazy private val clientComponent: TgClientComponent
) : TgMessagingService {

    private val log: Logger = LoggerFactory.getLogger(TgMessagingServiceImpl::class.java)

    private val messages: MutableList<Pair<LocalDateTime, Message>> = mutableListOf()

    private val statistics = CurrentSessionStatistics()

    override var actualMessage: String? = null
    override var status: AutoreplyStatus? = null

    override fun saveNewMessage(
        message: Message
    ) {
        messages.add(
            Pair(
                LocalDateTime.now(),
                message,
            )
        )
    }

    override fun processNewMessages() {
        val processed = mutableListOf<Pair<LocalDateTime, Message>>()
        messages.forEach {
            if (it.first.toEpochSecond(ZoneOffset.UTC) + 5 < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                val message = it.second
                val userId = when (val senderId = message.senderId) {
                    is MessageSenderUser -> senderId.userId
                    is MessageSenderChat -> null
                    else -> throw RuntimeException("Cant determine sender ID")
                }
                if (userId != null && userId == message.chatId) {
                    statistics.privateMessagesCount++
                } else {
                    statistics.chatMessagesCount++
                }
                doAutoreply(message)
                processed.add(it)
            }
        }
        messages.removeAll(processed)
    }

    override fun getStatistics(): CurrentSessionStatistics = statistics

    fun doAutoreply(message: Message) {
        log.info("Processed message: {}", message.content.toString())

        if (status == AutoreplyStatus.ENABLED) {
            clientComponent.getTelegramClient().sendAsync(
                TdApi.SendMessage(
                    message.chatId,
                    0,
                    null,
                    null,
                    null,
                    TdApi.InputMessageText(
                        TdApi.FormattedText(
                            actualMessage,
                            emptyArray()
                        ),
                        null,
                        false
                    )
                )
            )
        }
    }
}
