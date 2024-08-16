package ru.chsergeig.autoreply.client.impl

import dev.voroby.springframework.telegram.client.TdApi.UpdateNewMessage
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener
import org.springframework.stereotype.Component
import ru.chsergeig.autoreply.client.service.TgMessagingService

@Component
class NewMessageHandlerImpl(
    private val messagingService: TgMessagingService,
) : UpdateNotificationListener<UpdateNewMessage> {

    override fun handleNotification(notification: UpdateNewMessage?) {
        notification?.message?.let {
            messagingService.saveNewMessage(it)
        }
    }

    override fun notificationType(): Class<UpdateNewMessage> {
        return UpdateNewMessage::class.java
    }
}
