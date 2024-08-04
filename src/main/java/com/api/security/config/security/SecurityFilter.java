package com.api.security.config.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api.security.repositories.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    
    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
           throws ServletException, IOException {
       String token = this.recoveryToken(request);

       if (token != null) {
            String login = tokenService.validateToken(token);
            UserDetails user = userRepository.findByLogin(login);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
       }

       filterChain.doFilter(request, response);
   }


   private String recoveryToken(HttpServletRequest httpServletRequest) {
        String authReader = httpServletRequest.getHeader("Authorization");
        if (authReader == null) {
            return null;
        }

        return authReader.replace("Bearer ","");
   }
}
