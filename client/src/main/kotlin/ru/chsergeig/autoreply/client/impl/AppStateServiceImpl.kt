package ru.chsergeig.autoreply.client.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.chsergeig.autoreply.client.component.TgClientComponent
import ru.chsergeig.autoreply.client.dto.TgStatus
import ru.chsergeig.autoreply.client.entity.Setting
import ru.chsergeig.autoreply.client.enumeration.AutoreplyStatus
import ru.chsergeig.autoreply.client.enumeration.SettingKey
import ru.chsergeig.autoreply.client.enumeration.SettingKey.COMMON_MESSAGES_READ
import ru.chsergeig.autoreply.client.enumeration.SettingKey.MESSAGE
import ru.chsergeig.autoreply.client.enumeration.SettingKey.PRIVATE_MESSAGES_READ
import ru.chsergeig.autoreply.client.enumeration.SettingKey.PRIVATE_MESSAGES_RESPONSES
import ru.chsergeig.autoreply.client.enumeration.SettingKey.STATE
import ru.chsergeig.autoreply.client.properties.UserProperties
import ru.chsergeig.autoreply.client.repository.RepliedChatRepository
import ru.chsergeig.autoreply.client.repository.SettingRepository
import ru.chsergeig.autoreply.client.service.AppStateService
import ru.chsergeig.autoreply.client.service.TgMessagingService

@Service
class AppStateServiceImpl(
    private val repliedChatRepository: RepliedChatRepository,
    private val settingRepository: SettingRepository,
    private val userProperties: UserProperties,
    private val tgClientComponent: TgClientComponent,
    private val tgMessagingService: TgMessagingService,
) : AppStateService {

    @Transactional
    override fun getAppSettingByKey(key: SettingKey): String {
        var setting = settingRepository.findBySettingKey(key.name)
        if (setting == null) {
            val result = deduceValue(key)
            setting = Setting(null, key.name, result)
            settingRepository.save(setting)
            return result
        }
        return setting.settingValue!!
    }

    @Transactional
    override fun setAppSettingByKey(key: SettingKey, value: String) {
        var setting = settingRepository.findBySettingKey(key.name)
        if (setting == null) {
            setting = Setting(null, key.name, value)
        } else {
            setting.settingValue = value
        }
        settingRepository.save(setting)
    }

    override fun wipeRepliedChats() {
        repliedChatRepository.deleteAll()
    }

    override fun getClientStatus(): TgStatus {
        return TgStatus(
            tgClientComponent.getClientAuthorizationState().isStateClosed,
            tgClientComponent.getClientAuthorizationState().isWaitAuthenticationCode,
            tgClientComponent.getClientAuthorizationState().isWaitAuthenticationPassword,
            tgClientComponent.getClientAuthorizationState().isWaitEmailAddress,
            tgClientComponent.getClientAuthorizationState().haveAuthorization(),
            tgMessagingService.getStatistics(),
        )
    }

    override fun checkAuthenticationCode(
        data: String,
    ) {
        tgClientComponent.getClientAuthorizationState().checkAuthenticationCode(data)
    }

    override fun checkAuthenticationPassword(data: String) {
        tgClientComponent.getClientAuthorizationState().checkAuthenticationPassword(data)
    }

    override fun checkEmailAddress(data: String) {
        tgClientComponent.getClientAuthorizationState().checkEmailAddress(data)
    }

    private fun deduceValue(key: SettingKey): String {
        return when (key) {
            MESSAGE -> userProperties.default.message
            STATE -> if (userProperties.default.enabled) AutoreplyStatus.ENABLED.name else AutoreplyStatus.DISABLED.name
            COMMON_MESSAGES_READ, PRIVATE_MESSAGES_READ, PRIVATE_MESSAGES_RESPONSES -> "0"
        }
    }
}
