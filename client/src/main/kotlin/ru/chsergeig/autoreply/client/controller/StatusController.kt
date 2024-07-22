package ru.chsergeig.autoreply.client.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.chsergeig.autoreply.client.AppVersion
import ru.chsergeig.autoreply.client.component.TgClientComponent
import ru.chsergeig.autoreply.client.dto.TgStatus
import ru.chsergeig.autoreply.client.enumeration.SettingKey.MESSAGE
import ru.chsergeig.autoreply.client.enumeration.SettingKey.STATE
import ru.chsergeig.autoreply.client.service.AppStateService
import ru.chsergeig.autoreply.client.service.TgMessagingService
import java.util.concurrent.TimeUnit

@Controller
class StatusController(
    private val appStateService: AppStateService,
    private val client: TgClientComponent,
    private val messagingService: TgMessagingService,
) {

    @RequestMapping("status")
    fun status(
        model: Model,
    ): String {
        model.addAttribute(
            "clientStatus",
            TgStatus(
                client.getClientAuthorizationState().isStateClosed,
                client.getClientAuthorizationState().isWaitAuthenticationCode,
                client.getClientAuthorizationState().isWaitAuthenticationPassword,
                client.getClientAuthorizationState().isWaitEmailAddress,
                client.getClientAuthorizationState().haveAuthorization(),
                messagingService.getStatistics(),
            ),
        )
        model.addAttribute(
            "appStatus",
            appStateService.getAppSettingByKey(STATE),
        )
        model.addAttribute(
            "appMessage",
            appStateService.getAppSettingByKey(MESSAGE),
        )
        model.addAttribute(
            "appVersion",
            AppVersion.version,
        )
        return "status"
    }

    @RequestMapping(
        value = ["auth-code"],
    )
    fun authCode(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        client.getClientAuthorizationState().checkAuthenticationCode(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/status"
    }

    @RequestMapping(
        value = ["auth-password"],
    )
    fun authPassword(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        client.getClientAuthorizationState().checkAuthenticationPassword(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/status"
    }

    @RequestMapping(
        value = ["auth-email"],
    )
    fun authEmail(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        client.getClientAuthorizationState().checkEmailAddress(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/status"
    }
}
