package ru.chsergeig.autoreply.client.service

import ru.chsergeig.autoreply.client.dto.TgStatus
import ru.chsergeig.autoreply.client.enumeration.SettingKey

interface AppStateService {

    fun getAppSettingByKey(key: SettingKey): String

    fun setAppSettingByKey(key: SettingKey, value: String)

    fun wipeRepliedChats()

    fun getClientStatus(): TgStatus

    fun checkAuthenticationCode(data: String)

    fun checkAuthenticationPassword(data: String)

    fun checkEmailAddress(data: String)
}
