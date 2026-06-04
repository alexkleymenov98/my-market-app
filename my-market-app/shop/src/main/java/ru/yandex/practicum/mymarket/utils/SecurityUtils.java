package ru.yandex.practicum.mymarket.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.OAuth2User;
import reactor.core.publisher.Mono;

public class SecurityUtils {

    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);
    /**
     * Получить username текущего авторизованного пользователя из реактивного SecurityContext.
     * Возвращает пустой Mono для анонимного пользователя.
     */
    /**
     * Получить username текущего авторизованного пользователя
     */
    public static Mono<String> getCurrentUsername() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated())
                .map(auth -> {
                    Object principal = auth.getPrincipal();
                    String username = null;

                    if (principal instanceof OAuth2User) {
                        OAuth2User oauth2User = (OAuth2User) principal;
                        username = oauth2User.getAttribute("preferred_username");
                        if (username == null) username = oauth2User.getAttribute("email");
                        if (username == null) username = oauth2User.getAttribute("name");
                    }

                    if (username == null) {
                        username = auth.getName();
                    }

                    log.debug("Current username: {}", username);
                    return username;
                })
                .doOnError(error -> log.error("Error getting username", error));
    }

    public static Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated()
                        && !"anonymousUser".equals(auth.getPrincipal()))
                .map(auth -> auth.getName());
    }
}