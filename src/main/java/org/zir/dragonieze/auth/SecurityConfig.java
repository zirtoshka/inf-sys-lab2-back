package org.zir.dragonieze.auth;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zir.dragonieze.openam.auth.OpenAmAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private OpenAmAuthenticationFilter openAmFilter;

    @Bean
    public SecurityFilterChain securityOpenAmFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .anonymous(AnonymousConfigurer::disable)
                .addFilterBefore(openAmFilter, RememberMeAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/dragon/auth/**").permitAll()
                        .requestMatchers("/dragon/am/**").hasAnyRole("USER")
                        .requestMatchers("/dragon/app/**").hasAnyRole("USER")
                        .requestMatchers("/dragon/").hasAnyRole("USER")
                        .requestMatchers("/dragon/admin/**").hasAnyRole("ADMIN")
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().permitAll()
                ).cors(cors -> cors.configurationSource(corsConfigurationSource()))
        ;
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://openam.example.org:8083", "http://openam.example.org:8080"));  //todo another path
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE")); //todo add methods
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type",
                "Accept-API-Version", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
