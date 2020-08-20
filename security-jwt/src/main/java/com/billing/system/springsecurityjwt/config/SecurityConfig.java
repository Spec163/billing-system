package com.billing.system.springsecurityjwt.config;

import com.billing.system.springsecurityjwt.config.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(final JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .httpBasic().disable()
            .csrf().disable()
            //                .cors().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/admin/*").hasRole("ADMIN")
            .antMatchers("/user/*").hasAnyRole("ADMIN", "USER")
            .antMatchers("/register", "/auth").permitAll()
            .and()
            .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
