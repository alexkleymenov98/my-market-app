package ru.yandex.practicum.mymarket.config;


import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecureConfig {

    private static final Logger log = LoggerFactory.getLogger(SecureConfig.class);

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("=== CONFIGURING SECURITY ===");

        // Явно создаем репозиторий для хранения SecurityContext в WebSession
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                // ВАЖНО: порядок имеет значение! Сначала более конкретные пути
                .authorizeExchange(exchanges -> exchanges
                        // Публичные страницы - доступны всем
                        .pathMatchers("/login", "/", "/items/**").permitAll()
                        .pathMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyExchange().authenticated()
                )
                // Настройка OAuth2 логина
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authenticationSuccessHandler((exchange, authentication) -> {
                            log.info("Authentication successful for user: {}", authentication.getName());
                            RedirectServerAuthenticationSuccessHandler handler =
                                    new RedirectServerAuthenticationSuccessHandler("/");
                            return handler.onAuthenticationSuccess(exchange, authentication);
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .build();
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        handler.setLogoutSuccessUrl(URI.create("/items"));
        return handler;
    }
}