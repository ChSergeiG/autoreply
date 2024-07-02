package ru.chsergeig.autoreply.client.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import ru.chsergeig.autoreply.client.properties.UserProperties

@Configuration
@EnableWebSecurity
class SecurityConfiguration @Autowired constructor(
    private val userProperties: UserProperties
) {

    @Bean
    fun chain(
        http: HttpSecurity
    ): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it
                    .anyRequest()
                    .authenticated()
            }
            .formLogin {
                it
                    .loginPage("/login")
                    .permitAll()
                    .successForwardUrl("/index")
            }
            .logout {
                it
                    .permitAll()
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
            }
        return http.build()
    }

    @Autowired
    fun configureGlobal(
        auth: AuthenticationManagerBuilder,
        passwordEncoder: PasswordEncoder
    ) {
        auth.inMemoryAuthentication()
            .withUser(userProperties.security.username)
            .password(passwordEncoder.encode(userProperties.security.password))
            .roles("ADMIN")
    }

}
