package ru.chsergeig.autoreply.client.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.chsergeig.autoreply.client.enumeration.SettingKey
import ru.chsergeig.autoreply.client.service.AppStateService

@Controller
class ViewController @Autowired constructor(
    private val appStateService: AppStateService,
) {

    @RequestMapping("/login")
    fun login(): String = "login"

    @RequestMapping(value = ["/", "/index"])
    fun index(
        model: Model,
        @RequestParam(value = "goto", required = false) goto: String?
    ): String {
        model.addAttribute(
            "status",
            appStateService.getAppSettingByKey(SettingKey.STATE),
        )
        return goto ?: "index"
    }
}
