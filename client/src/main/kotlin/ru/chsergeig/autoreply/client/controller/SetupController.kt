package ru.chsergeig.autoreply.client.controller

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import ru.chsergeig.autoreply.client.enumeration.AutoreplyStatus
import ru.chsergeig.autoreply.client.enumeration.SettingKey.MESSAGE
import ru.chsergeig.autoreply.client.enumeration.SettingKey.STATE
import ru.chsergeig.autoreply.client.service.AppStateService

@Controller
class SetupController(
    private val appStateService: AppStateService,
) {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["setup"],
    )
    fun status(
        model: Model,
    ): String {
        model.addAttribute(
            "status",
            appStateService.getAppSettingByKey(STATE),
        )
        model.addAttribute(
            "message",
            appStateService.getAppSettingByKey(MESSAGE),
        )
        return "setup"
    }

    @RequestMapping(
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        method = [RequestMethod.POST],
        value = ["setup/message"],
    )
    fun doSetup(
        @RequestParam body: Map<String, String>,
    ): String {
        if (body["status"] != null) {
            val status = try {
                AutoreplyStatus.valueOf(body["status"] ?: "")
            } catch (ignore: Exception) {
                AutoreplyStatus.DISABLED
            }
            appStateService.setAppSettingByKey(STATE, status.name)
        }
        if (body["message"] != null) {
            appStateService.setAppSettingByKey(MESSAGE, body["message"])
        }
        return "redirect:/setup"
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["setup/enable"],
    )
    fun enableAutoreply(): String {
        appStateService.setAppSettingByKey(STATE, AutoreplyStatus.ENABLED.name)
        return "redirect:/setup"
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["setup/disable"],
    )
    fun disableAutoreply(): String {
        appStateService.setAppSettingByKey(STATE, AutoreplyStatus.DISABLED.name)
        return "redirect:/setup"
    }
}
