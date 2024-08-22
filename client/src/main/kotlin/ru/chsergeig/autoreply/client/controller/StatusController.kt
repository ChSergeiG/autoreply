package ru.chsergeig.autoreply.client.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.chsergeig.autoreply.client.enumeration.SettingKey.MESSAGE
import ru.chsergeig.autoreply.client.enumeration.SettingKey.STATE
import ru.chsergeig.autoreply.client.service.AppStateService
import java.util.concurrent.TimeUnit

@Controller
class StatusController @Autowired constructor(
    @Value("\${version}") private val appVersion: String,
    private val appStateService: AppStateService,
) {

    @RequestMapping("status")
    fun status(
        model: Model,
    ): String {
        model.addAttribute("clientStatus", appStateService.getClientStatus())
        model.addAttribute("appStatus", appStateService.getAppSettingByKey(STATE))
        model.addAttribute("appMessage", appStateService.getAppSettingByKey(MESSAGE))
        model.addAttribute("appVersion", appVersion)
        return "status"
    }

    @RequestMapping(
        value = ["auth-code"],
    )
    fun authCode(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        appStateService.checkAuthenticationCode(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/status"
    }

    @RequestMapping(
        value = ["auth-password"],
    )
    fun authPassword(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        appStateService.checkAuthenticationPassword(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/status"
    }

    @RequestMapping(
        value = ["auth-email"],
    )
    fun authEmail(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        appStateService.checkEmailAddress(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/status"
    }
}
