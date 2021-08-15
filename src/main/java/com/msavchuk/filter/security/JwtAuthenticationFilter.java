package com.msavchuk.filter.security;

import com.msavchuk.security.jwt.dto.JwtUserDetails;
import com.msavchuk.security.jwt.logic.ClientTokenTool;
import com.msavchuk.filter.security.abstraction.AuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtAuthenticationFilter implements AuthenticationFilter {

    private final ClientTokenTool clientTokenTool;

    @Autowired
    public JwtAuthenticationFilter(@Qualifier("clientTokenTool") ClientTokenTool clientTokenTool) {
        this.clientTokenTool = clientTokenTool;
    }

    @Override
    public boolean execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            JwtUserDetails user = clientTokenTool.getAccessToken(request);

            SecurityContextHolder.getContext()
                                 .setAuthentication(new UsernamePasswordAuthenticationToken(user, "", user
                                         .getAuthorities()));

            return false;
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            throw e;
        }
    }
}
