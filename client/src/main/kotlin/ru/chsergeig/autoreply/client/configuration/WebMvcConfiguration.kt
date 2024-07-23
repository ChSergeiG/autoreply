package ru.chsergeig.autoreply.client.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.util.Locale

@Configuration
@EnableWebMvc
class WebMvcConfiguration {

    @Bean
    fun messageSource(): ResourceBundleMessageSource {
        val source = ResourceBundleMessageSource()
        source.setBasenames("i18n/messages")
        source.setUseCodeAsDefaultMessage(true)
        source.setDefaultLocale(Locale.of("en"))
        source.setDefaultEncoding("UTF-8")
        source.setAlwaysUseMessageFormat(true)
        return source
    }

}
