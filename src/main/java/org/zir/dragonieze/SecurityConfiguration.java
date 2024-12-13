package org.zir.dragonieze;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {



    @Bean
    @Order(3)
    @Profile("cookie")
    public SecurityFilterChain securityOpenAmFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/protected-openam", OpenAmAuthenticationFilter.OPENAM_AUTH_URI)
                .addFilterAt(openAmAuthenticationFilter(), RememberMeAuthenticationFilter.class)
                .authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().fullyAuthenticated())
                .exceptionHandling(e ->
                        e.authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect(OpenAmAuthenticationFilter.OPENAM_AUTH_URI)));
        return http.build();
    }
    @Bean
    @Order(0)
    public SecurityFilterChain securityPermitAllFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/", "/error", "/logout")
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
                .logout(logout ->
                        logout.logoutSuccessUrl("/?logout")
                                .logoutRequestMatcher(AntPathRequestMatcher.antMatcher("/logout")));

        return http.build();
    }

    @Bean
    public OpenAmAuthenticationFilter openAmAuthenticationFilter() {
        return new OpenAmAuthenticationFilter();
    }
}
