package ru.chsergeig.autoreply.client.impl

import dev.voroby.springframework.telegram.client.TdApi
import dev.voroby.springframework.telegram.client.TdApi.FormattedText
import dev.voroby.springframework.telegram.client.TdApi.InputMessageReplyToMessage
import dev.voroby.springframework.telegram.client.TdApi.InputMessageText
import dev.voroby.springframework.telegram.client.TdApi.Message
import dev.voroby.springframework.telegram.client.TdApi.MessageSenderChat
import dev.voroby.springframework.telegram.client.TdApi.MessageSenderUser
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import ru.chsergeig.autoreply.client.component.TgClientComponent
import ru.chsergeig.autoreply.client.dto.CurrentSessionStatistics
import ru.chsergeig.autoreply.client.entity.RepliedChat
import ru.chsergeig.autoreply.client.enumeration.AutoreplyStatus
import ru.chsergeig.autoreply.client.enumeration.SettingKey
import ru.chsergeig.autoreply.client.enumeration.SettingKey.COMMON_MESSAGES_READ
import ru.chsergeig.autoreply.client.enumeration.SettingKey.MESSAGE
import ru.chsergeig.autoreply.client.enumeration.SettingKey.PRIVATE_MESSAGES_READ
import ru.chsergeig.autoreply.client.enumeration.SettingKey.PRIVATE_MESSAGES_RESPONSES
import ru.chsergeig.autoreply.client.enumeration.SettingKey.STATE
import ru.chsergeig.autoreply.client.repository.MessageRepository
import ru.chsergeig.autoreply.client.repository.RepliedChatRepository
import ru.chsergeig.autoreply.client.service.AppStateService
import ru.chsergeig.autoreply.client.service.TgMessagingService
import java.time.ZonedDateTime
import ru.chsergeig.autoreply.client.entity.Message as MessagePojo

@Service
class TgMessagingServiceImpl(
    @Lazy private val appStateService: AppStateService,
    @Lazy private val clientComponent: TgClientComponent,
    private val messageRepository: MessageRepository,
    private val repliedChatRepository: RepliedChatRepository,
) : TgMessagingService {

    private val log: Logger = LoggerFactory.getLogger(TgMessagingServiceImpl::class.java)

    override fun saveNewMessage(
        message: Message,
    ) {
        val messagePojo = MessagePojo(
            null,
            message.id,
            message.chatId,
            when (val senderId = message.senderId) {
                is MessageSenderUser -> senderId.userId
                is MessageSenderChat -> 0L
                else -> throw RuntimeException("Cant determine sender ID")
            },
            message.content.toString(),
            ZonedDateTime.now(),
        )

        messageRepository.save(messagePojo)

        if (messagePojo.senderId == messagePojo.chatId) {
            // it means that it is private message
            increment(PRIVATE_MESSAGES_READ)
        } else {
            // not a private message
            increment(COMMON_MESSAGES_READ)
        }
    }

    override fun processNewMessages() {
        messageRepository.findAll().forEach {
            if (it.messageTime.isBefore(ZonedDateTime.now().minusSeconds(5))) {
                try {
                    if (it.senderId == it.chatId) {
                        increment(PRIVATE_MESSAGES_RESPONSES)
                        doAutoreply(it)
                    }
                } finally {
                    messageRepository.delete(it)
                }
            }
        }
    }

    override fun updateReplyMessage(
        newMessage: String,
    ) = appStateService.setAppSettingByKey(MESSAGE, newMessage)

    override fun updateReplierStatus(
        newStatus: AutoreplyStatus,
    ) = appStateService.setAppSettingByKey(STATE, newStatus.name)

    override fun getStatistics(): CurrentSessionStatistics = CurrentSessionStatistics(
        appStateService.getAppSettingByKey(COMMON_MESSAGES_READ).toInt(),
        appStateService.getAppSettingByKey(PRIVATE_MESSAGES_READ).toInt(),
        appStateService.getAppSettingByKey(PRIVATE_MESSAGES_RESPONSES).toInt(),
    )

    @Transactional
    override fun removeOldRepliesFromList() {
        repliedChatRepository.deleteRepliedChatByRepliedTimeBefore(
            ZonedDateTime.now().minusMinutes(15),
        )
    }

    fun doAutoreply(message: MessagePojo) {
        if (appStateService.getAppSettingByKey(STATE) == AutoreplyStatus.ENABLED.name) {
            if (repliedChatRepository.existsRepliedChatByChatId(message.chatId)) {
                log.warn("Flood protection in chat {}", message.chatId)
                return
            }
            repliedChatRepository.save(
                RepliedChat(
                    null,
                    message.chatId,
                    ZonedDateTime.now(),
                ),
            )
            log.warn("Reply sent to {}", message.chatId)
            clientComponent.getTelegramClient().sendAsync(
                TdApi.SendMessage(
                    message.chatId,
                    0,
                    InputMessageReplyToMessage(
                        message.messageId,
                        null,
                    ),
                    null,
                    null,
                    InputMessageText(
                        FormattedText(
                            appStateService.getAppSettingByKey(MESSAGE),
                            emptyArray(),
                        ),
                        null,
                        false,
                    ),
                ),
            )
        }
    }

    @Transactional
    fun increment(key: SettingKey) {
        appStateService.setAppSettingByKey(
            key,
            (appStateService.getAppSettingByKey(key).toInt() + 1).toString(),
        )
    }
}
