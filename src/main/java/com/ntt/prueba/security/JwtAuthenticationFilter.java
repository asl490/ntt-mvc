package com.ntt.prueba.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        String userEmail = null;
        try {
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    SecurityContextHolder.clearContext(); // Clear context before setting attribute
                    request.setAttribute("authenticationException",
                            new AuthenticationServiceException("Invalid JWT token"));
                }
            } else if (userEmail == null) {
                SecurityContextHolder.clearContext(); // Clear context before setting attribute
                request.setAttribute("authenticationException",
                        new AuthenticationServiceException("Invalid JWT token: username not found"));
            }
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            SecurityContextHolder.clearContext(); // Clear context before setting attribute
            request.setAttribute("authenticationException",
                    new AuthenticationServiceException("JWT processing failed", e));
        } catch (AuthenticationServiceException e) {
            SecurityContextHolder.clearContext(); // Clear context before setting attribute
            request.setAttribute("authenticationException", e);
        } catch (Exception e) {
            SecurityContextHolder.clearContext(); // Clear context before setting attribute
            request.setAttribute("authenticationException",
                    new AuthenticationServiceException("An unexpected error occurred during JWT processing", e));
        }

        filterChain.doFilter(request, response);
    }
}