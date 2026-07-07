package com.example.trabalho_loja_virtual.Config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                //Rotas públicas
                .requestMatchers(
                    "/", 
                    "/login", 
                    "/login/", 
                    "/login/**",
                    "/images/**"
                ).permitAll()

                //Rotas autenticadas
                .requestMatchers("/lojista/**").hasRole("lOJISTA")
                .requestMatchers("/cliente/**").hasRole("CLIENTE")

                //Qualquer outra requisição exige login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());
        return http.build();
    }
}
