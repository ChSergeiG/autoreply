package ru.chsergeig.autoreply.client.service

interface AppStateService {

    fun getAppSettingByKey(key: String): String?

    fun setAppSettingByKey(key: String, value: String?)

}