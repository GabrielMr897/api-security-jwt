package com.api.security.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;




@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {


   private final SecurityFilter securityFilter;

   private CustomLogoutHandler logoutHandler;



  @Bean
  protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return         http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(            "/api/auth/login",
                        "/api/auth/refresh-token",
                        "/api/auth/register",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS))
                .logout(l->l
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()
                        ))
                .exceptionHandling(
                        e->e.accessDeniedHandler(
                                        (request, response, accessDeniedException)->response.setStatus(403)
                                )
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class).build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
  }

}
