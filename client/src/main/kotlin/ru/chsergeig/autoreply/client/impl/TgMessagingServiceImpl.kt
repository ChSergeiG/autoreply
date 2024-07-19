package ru.chsergeig.autoreply.client.impl

import dev.voroby.springframework.telegram.client.TdApi
import dev.voroby.springframework.telegram.client.TdApi.FormattedText
import dev.voroby.springframework.telegram.client.TdApi.InputMessageReplyToMessage
import dev.voroby.springframework.telegram.client.TdApi.InputMessageText
import dev.voroby.springframework.telegram.client.TdApi.Message
import dev.voroby.springframework.telegram.client.TdApi.MessageSenderChat
import dev.voroby.springframework.telegram.client.TdApi.MessageSenderUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import ru.chsergeig.autoreply.client.component.TgClientComponent
import ru.chsergeig.autoreply.client.dto.CurrentSessionStatistics
import ru.chsergeig.autoreply.client.enumeration.AutoreplyStatus
import ru.chsergeig.autoreply.client.repository.SettingRepository.SettingKey.Companion.appMessage
import ru.chsergeig.autoreply.client.repository.SettingRepository.SettingKey.Companion.appState
import ru.chsergeig.autoreply.client.repository.SettingRepository.SettingKey.Companion.commonMessagesRead
import ru.chsergeig.autoreply.client.repository.SettingRepository.SettingKey.Companion.privateMessagesRead
import ru.chsergeig.autoreply.client.repository.SettingRepository.SettingKey.Companion.privateMessagesResponses
import ru.chsergeig.autoreply.client.service.AppStateService
import ru.chsergeig.autoreply.client.service.TgMessagingService
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap

@Service
class TgMessagingServiceImpl(
    private val appStateService: AppStateService,
    @Lazy private val clientComponent: TgClientComponent,
) : TgMessagingService {

    private val log: Logger = LoggerFactory.getLogger(TgMessagingServiceImpl::class.java)

    private val messages: MutableList<Pair<LocalDateTime, Message>> = mutableListOf()

    override var statistics: CurrentSessionStatistics?
        get() = CurrentSessionStatistics(
            appStateService.getAppSettingByKey(commonMessagesRead)!!.toInt(),
            appStateService.getAppSettingByKey(privateMessagesRead)!!.toInt(),
            appStateService.getAppSettingByKey(privateMessagesResponses)!!.toInt(),
        )
        set(value) {
            if (value?.chatMessagesCount != null) {
                appStateService.setAppSettingByKey(
                    commonMessagesRead,
                    value.chatMessagesCount.toString()
                )
            }
            if (value?.privateMessagesCount != null) {
                appStateService.setAppSettingByKey(
                    privateMessagesRead,
                    value.privateMessagesCount.toString()
                )
            }
            if (value?.privateMessagesResponsesCount != null) {
                appStateService.setAppSettingByKey(
                    privateMessagesResponses,
                    value.privateMessagesResponsesCount.toString()
                )
            }
        }

    override var actualMessage: String?
        get() = appStateService.getAppSettingByKey(appMessage)
        set(value) {
            appStateService.setAppSettingByKey(appMessage, value!!)
        }

    override var status: AutoreplyStatus?
        get() = AutoreplyStatus.valueOf(appStateService.getAppSettingByKey(appState)!!)
        set(value) {
            appStateService.setAppSettingByKey(appState, value!!.name)
        }

    override var responseChatList: MutableMap<Long, LocalDateTime> = ConcurrentHashMap()

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
                    appStateService.setAppSettingByKey(
                        privateMessagesRead,
                        (appStateService.getAppSettingByKey(privateMessagesRead)!!.toInt() + 1).toString()
                    )
                    doAutoreply(message)
                } else {
                    appStateService.setAppSettingByKey(
                        commonMessagesRead,
                        (appStateService.getAppSettingByKey(commonMessagesRead)!!.toInt() + 1).toString()
                    )
                }
                processed.add(it)
            }
        }
        messages.removeAll(processed)
    }

    fun doAutoreply(message: Message) {
        if (status == AutoreplyStatus.ENABLED) {
            log.info("Processed message: {}", message.toString())
            if (responseChatList.containsKey(message.chatId)) {
                log.info(">>> Flood protection")
                return
            }
            appStateService.setAppSettingByKey(
                privateMessagesResponses,
                (appStateService.getAppSettingByKey(privateMessagesResponses)!!.toInt() + 1).toString()
            )
            responseChatList[message.chatId] = LocalDateTime.now()
            clientComponent.getTelegramClient().sendAsync(
                TdApi.SendMessage(
                    message.chatId,
                    0,
                    InputMessageReplyToMessage(
                        message.id,
                        null
                    ),
                    null,
                    null,
                    InputMessageText(
                        FormattedText(
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
