package ru.chsergeig.autoreply.client.configuration.profile

import dev.voroby.springframework.telegram.TelegramClientAutoConfiguration
import dev.voroby.springframework.telegram.client.TelegramClient
import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationState
import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationStateImpl
import dev.voroby.springframework.telegram.properties.TelegramProperties
import org.mockito.Mockito
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableAutoConfiguration(exclude = [TelegramClientAutoConfiguration::class])
@Profile("dev")
class DevProfileConfiguration {

    @Bean
    fun mockAuthorizationState(): ClientAuthorizationState {
        val mock = Mockito.mock(ClientAuthorizationStateImpl::class.java)
        Mockito.`when`(mock.haveAuthorization()).thenReturn(true)
        return mock
    }

    @Bean
    fun mockTelegramClient(
        telegramProperties: TelegramProperties,
        clientAuthorizationState: ClientAuthorizationState
    ): TelegramClient = Mockito.mock(TelegramClient::class.java)


}