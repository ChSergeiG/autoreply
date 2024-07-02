package ru.chsergeig.autoreply.client.configuration

import dev.voroby.springframework.telegram.client.TelegramClient
import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationState
import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationStateImpl
import dev.voroby.springframework.telegram.properties.TelegramProperties
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.chsergeig.autoreply.client.component.TgClientComponent

@Configuration
class TgClientConfiguration {

    @Bean
    fun tgClientComponentImpl(
        authorizationState: ClientAuthorizationState,
        telegramClient: TelegramClient,
    ): TgClientComponent = object : TgClientComponent {
        override fun getClientAuthorizationState(): ClientAuthorizationState = authorizationState
        override fun getTelegramClient(): TelegramClient = telegramClient
    }

//    @Bean
    fun mockAuthorizationState(): ClientAuthorizationState {
        val mock = Mockito.mock(ClientAuthorizationStateImpl::class.java)
        Mockito.`when`(mock.haveAuthorization()).thenReturn(true)
        return mock
    }

//    @Bean
    fun mockTelegramClient(
        telegramProperties: TelegramProperties,
        clientAuthorizationState: ClientAuthorizationState
    ): TelegramClient = Mockito.mock(TelegramClient::class.java)

}