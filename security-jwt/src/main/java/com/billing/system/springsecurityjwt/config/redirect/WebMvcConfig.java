package com.billing.system.springsecurityjwt.config.redirect;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerCustomizer() {
        // вместо 404 возвращает /-страницу. Для рендера Vue.js
        return container -> {
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/user/info"));
            container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/user/forbidden"));
        };
    }

}
