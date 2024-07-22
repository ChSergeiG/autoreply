package ru.chsergeig.autoreply.client.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class ViewController {

    @RequestMapping("/login")
    fun login(): String = "login"

    @RequestMapping(value = ["/", "/index"])
    fun index(): String = "index"
}
