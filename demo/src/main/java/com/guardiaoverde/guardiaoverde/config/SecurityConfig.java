package com.guardiaoverde.guardiaoverde.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // desabilita CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // autorização: todas as principais entidades só podem ser acessadas por usuário autenticado
                .authorizeHttpRequests(auth -> auth
                        // endpoints que exigem login:
                        .requestMatchers(
                                "/v1/usuarios/**",
                                "/v1/ambientes/**",
                                "/v1/regioes/**",
                                "/v1/rotas/**",
                                "/v1/bombeiros/**",
                                "/v1/policias_militares/**",
                                "/v1/chamados/**",
                                "/v1/alertas/**"
                        ).authenticated()
                        // qualquer outra rota (ex.: “/actuator”, páginas estáticas etc.) fica liberada
                        .anyRequest().permitAll()
                )

                // habilita HTTP Basic
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
