package ru.chsergeig.autoreply.client.component

import dev.voroby.springframework.telegram.client.TelegramClient
import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationState

interface TgClientComponent {
    fun getClientAuthorizationState(): ClientAuthorizationState
    fun getTelegramClient(): TelegramClient
}
