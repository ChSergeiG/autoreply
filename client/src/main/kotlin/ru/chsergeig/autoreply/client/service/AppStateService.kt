package ru.chsergeig.autoreply.client.service

import ru.chsergeig.autoreply.client.enumeration.SettingKey

interface AppStateService {

    fun getAppSettingByKey(key: SettingKey): String?

    fun setAppSettingByKey(key: SettingKey, value: String?)
}
