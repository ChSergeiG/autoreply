package ru.chsergeig.autoreply.client.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.chsergeig.autoreply.client.component.TgClientComponent
import ru.chsergeig.autoreply.client.dto.TgStatus
import ru.chsergeig.autoreply.client.service.TgMessagingService
import java.util.concurrent.TimeUnit

@Controller
class TgController(
    private val client: TgClientComponent,
    private val messagingService: TgMessagingService,
) {

    @RequestMapping("tg/status")
    fun status(
        model: Model
    ): String {
        model.addAttribute(
            "status", TgStatus(
                client.getClientAuthorizationState().isStateClosed,
                client.getClientAuthorizationState().isWaitAuthenticationCode,
                client.getClientAuthorizationState().isWaitAuthenticationPassword,
                client.getClientAuthorizationState().isWaitEmailAddress,
                client.getClientAuthorizationState().haveAuthorization(),
                messagingService.getStatistics(),
            )
        )
        return "status"
    }

    @RequestMapping(
        value = ["tg/auth-code"]
    )
    fun authCode(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        client.getClientAuthorizationState().checkAuthenticationCode(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/tg/status"
    }

    @RequestMapping(
        value = ["tg/auth-password"]
    )
    fun authPassword(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        client.getClientAuthorizationState().checkAuthenticationPassword(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/tg/status"
    }

    @RequestMapping(
        value = ["tg/auth-email"]
    )
    fun authEmail(
        @RequestParam(value = "data", required = true) data: String,
    ): String {
        client.getClientAuthorizationState().checkEmailAddress(data)
        TimeUnit.SECONDS.sleep(1)
        return "redirect:/tg/status"
    }

}
