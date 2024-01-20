package dev.example.aero.security.service;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomRedirectFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("(1)Inside CustomRedirectFilter....");
        if (!request.getRequestURI().startsWith("/login")) {
            String redirectUrl = request.getRequestURI();
            System.out.println("prev req..will redirect to..." + redirectUrl);
            request.getSession().setAttribute("redirectUrl", redirectUrl);
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
