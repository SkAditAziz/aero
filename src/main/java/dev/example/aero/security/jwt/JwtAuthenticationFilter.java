package dev.example.aero.security.jwt;

import dev.example.aero.config.SecurityConfig;
import dev.example.aero.security.service.JwtService;
import dev.example.aero.security.service.PassengerDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final PassengerDetailsService passengerDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, PassengerDetailsService passengerDetailsService) {
        this.jwtService = jwtService;
        this.passengerDetailsService = passengerDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String userEmail;

        Cookie[] cookies = request.getCookies();
        String jwt = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Bearer".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        if (jwt == null) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendRedirect("/login");
                return;
            }
            jwt = authHeader.substring("Bearer ".length());
        }
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails passengerDetails = passengerDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, passengerDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        passengerDetails,
                        null,
                        passengerDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        if (!shouldNotFilter(request) && SecurityContextHolder.getContext().getAuthentication() == null) {
            response.sendRedirect("/login");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        return Arrays.stream(SecurityConfig.excludedEndpoints)
                .anyMatch( e -> new AntPathMatcher().match(e, request.getServletPath()));
    }
}
