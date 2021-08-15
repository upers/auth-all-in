package com.msavchuk.filter.security;

import com.msavchuk.filter.security.abstraction.AuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AuthenticationFilterChain extends OncePerRequestFilter {

    private volatile List<AuthenticationFilter> filters;

    public void init(HttpServletRequest request) {
        if (filters == null) {
            synchronized (this) {
                if (filters != null)
                    return;

                ServletContext servletContext = request.getServletContext();
                WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(
                        servletContext);
                JwtRefreshTokenFilter jwtRefreshTokenFilter =
                        webApplicationContext.getBean(JwtRefreshTokenFilter.class);
                UsernamePasswordAuthFilter usernamePasswordAuthFilter =
                        webApplicationContext.getBean(UsernamePasswordAuthFilter.class);
                //This filter should execute the last it is without any matchers.
                JwtAuthenticationFilter jwtAuthenticationFilter =
                        webApplicationContext.getBean(JwtAuthenticationFilter.class);
                this.filters = List.of(jwtRefreshTokenFilter, usernamePasswordAuthFilter, jwtAuthenticationFilter);
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        init(request);

        for (AuthenticationFilter filter : filters) {
            try {
                boolean executeNext = filter.execute(request, response);
                if (!executeNext)
                    break;
            } catch (IOException | ServletException e) {
                logger.debug(e.getMessage(), e);
                throw e;
            }
        }

        if (response.isCommitted())
            return;

        filterChain.doFilter(request, response);
    }
}
