package ru.chsergeig.autoreply.client.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import ru.chsergeig.autoreply.client.component.TgClientComponent
import ru.chsergeig.autoreply.client.enum.AutoreplyStatus
import ru.chsergeig.autoreply.client.properties.UserProperties
import ru.chsergeig.autoreply.client.service.TgMessagingService
import java.time.LocalDateTime
import java.time.ZoneOffset

@Configuration
@EnableScheduling
class ScheduleConfiguration(
    private val clientComponent: TgClientComponent,
    private val userProperties: UserProperties,
    private val messagingService: TgMessagingService,
) {

    @Scheduled(fixedDelay = 500)
    fun setupDefaults() {
        if (clientComponent.getClientAuthorizationState().haveAuthorization()) {
            if (messagingService.actualMessage == null) {
                messagingService.actualMessage = userProperties.default.message
            }
            if (messagingService.status == null) {
                messagingService.status =
                    if (userProperties.default.enabled) AutoreplyStatus.ENABLED else AutoreplyStatus.DISABLED
            }
        }
    }

    @Scheduled(fixedDelay = 5_000)
    fun processMessages() {
        if (clientComponent.getClientAuthorizationState().haveAuthorization()) {
            messagingService.processNewMessages()
        }
    }

    @Scheduled(fixedDelay = 30_000)
    fun removeOldReplyesFromList() {
        val ids = messagingService.responseChatList
            .filter {
                it.value.toEpochSecond(ZoneOffset.UTC) + 60 * 15 < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            }
            .map { it.key }
        // it seems that we need to split map with assigment and futher iterator to avoid
        // concurrent modification of map
        ids.forEach { messagingService.responseChatList.remove(it) }
    }
}
