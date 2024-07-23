package ru.chsergeig.autoreply.client.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import java.nio.charset.Charset

@Controller
class ResourceController {

    @GetMapping("/styles/{file}")
    @ResponseBody
    @Throws(IOException::class)
    fun styles(
        @PathVariable("file") file: String,
    ): ResponseEntity<String> {
        val cssStream = ResourceController::class.java.classLoader
            .getResourceAsStream("static/styles/$file")
        val css = StreamUtils.copyToString(cssStream, Charset.defaultCharset())
        val httpHeaders = HttpHeaders()
        httpHeaders.add("Content-Type", "text/css; charset=utf-8")
        httpHeaders.add("Cache-Control", "max-age=604800, public")
        return ResponseEntity<String>(css, httpHeaders, HttpStatus.OK)
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    @Throws(IOException::class)
    fun favicon(): ResponseEntity<ByteArray> {
        val ico = ResourceController::class.java.classLoader
            .getResourceAsStream("static/favicon.ico")
        val httpHeaders = HttpHeaders()
        httpHeaders.add("Content-Type", "image/x-icon")
        httpHeaders.add("Cache-Control", "max-age=604800, public")
        return ResponseEntity<ByteArray>(
            StreamUtils.copyToByteArray(ico),
            httpHeaders,
            HttpStatus.OK,
        )
    }
}
