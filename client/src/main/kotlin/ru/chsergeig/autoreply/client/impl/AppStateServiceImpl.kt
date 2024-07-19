package ru.chsergeig.autoreply.client.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.chsergeig.autoreply.client.entity.Setting
import ru.chsergeig.autoreply.client.enumeration.AutoreplyStatus
import ru.chsergeig.autoreply.client.properties.UserProperties
import ru.chsergeig.autoreply.client.repository.SettingRepository
import ru.chsergeig.autoreply.client.repository.SettingRepository.SettingKey.Companion.appMessage
import ru.chsergeig.autoreply.client.repository.SettingRepository.SettingKey.Companion.appState
import ru.chsergeig.autoreply.client.service.AppStateService

@Service
class AppStateServiceImpl(
    private val settingRepository: SettingRepository,

    private val userProperties: UserProperties,
) : AppStateService {

    @Transactional
    override fun getAppSettingByKey(key: String): String? {
        var setting = settingRepository.findBySettingKey(key)
        if (setting == null) {
            val result = deduceValue(key)
            if (result != null) {
                setting = Setting(null, key, result)
                settingRepository.save(setting)
            }
            return result
        }
        return setting.settingValue
    }

    @Transactional
    override fun setAppSettingByKey(key: String, value: String?) {
        var setting = settingRepository.findBySettingKey(key)
        if (setting == null) {
            setting = Setting(null, key, value)
        } else {
            setting.settingValue = value
        }
        settingRepository.save(setting)
    }

    private fun deduceValue(key: String): String? {
        return when (key) {
            appMessage -> userProperties.default.message
            appState -> if (userProperties.default.enabled) AutoreplyStatus.ENABLED.name else AutoreplyStatus.DISABLED.name
            else -> null
        }
    }
}