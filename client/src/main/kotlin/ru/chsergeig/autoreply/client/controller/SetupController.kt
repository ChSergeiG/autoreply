package ru.chsergeig.autoreply.client.controller

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import ru.chsergeig.autoreply.client.enumeration.AutoreplyStatus
import ru.chsergeig.autoreply.client.service.TgMessagingService

@Controller
class SetupController(
    private val messagingService: TgMessagingService
) {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["setup"]
    )
    fun status(
        model: Model
    ): String {
        model.addAttribute("status", messagingService.status?.name)
        model.addAttribute("message", messagingService.actualMessage)
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
            messagingService.status = try {
                AutoreplyStatus.valueOf(body["status"] ?: "")
            } catch (ignore: Exception) {
                AutoreplyStatus.DISABLED
            }
        }
        if (body["message"] != null) {
            messagingService.actualMessage = body["message"]
        }
        return "redirect:/setup"
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["setup/enable"],
    )
    fun enableAutoreply(): String {
        messagingService.status = AutoreplyStatus.ENABLED
        return "redirect:/setup"
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["setup/disable"],
    )
    fun disableAutoreply(): String {
        messagingService.status = AutoreplyStatus.DISABLED
        return "redirect:/setup"
    }

}
