package ru.chsergeig.autoreply.client.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import ru.chsergeig.autoreply.client.component.TgClientComponent
import ru.chsergeig.autoreply.client.repository.RepliedChatRepository
import ru.chsergeig.autoreply.client.service.TgMessagingService
import java.time.LocalDateTime

@Configuration
@EnableScheduling
class ScheduleConfiguration(
    private val clientComponent: TgClientComponent,
    private val messagingService: TgMessagingService,
    private val repliedChatRepository: RepliedChatRepository,
) {

    @Scheduled(fixedDelay = 5_000)
    fun processMessages() {
        if (clientComponent.getClientAuthorizationState().haveAuthorization()) {
            messagingService.processNewMessages()
        }
    }

    @Scheduled(fixedDelay = 30_000)
    fun removeOldRepliesFromList() {
        repliedChatRepository.deleteRepliedChatByRepliedTimeBefore(
            LocalDateTime.now().minusMinutes(15),
        )
    }
}
