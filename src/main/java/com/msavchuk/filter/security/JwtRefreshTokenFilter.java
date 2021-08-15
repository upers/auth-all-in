package com.msavchuk.filter.security;

import com.msavchuk.security.jwt.dto.JwtRefreshUserDetails;
import com.msavchuk.filter.security.abstraction.RequestMatcherFilter;
import com.msavchuk.tool.jwt.TokenTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtRefreshTokenFilter extends RequestMatcherFilter {
    protected final TokenTool tokenTool;

    protected final UserDetailsService userDetailsService;

    public JwtRefreshTokenFilter(@Qualifier("tokenTool") TokenTool tokenTool,
                                 UserDetailsService userDetailsService,
                                 @Value("${security.refresh.token.uri}") String uri
    ) {
        super(uri, "POST");
        this.tokenTool = tokenTool;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean executeInternal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //If request is from URI that provided for refresh token then true.
        boolean match = this.requiresAuthentication(request);
        if (!match)
            return true;
        //Get refresh token details from header.
        JwtRefreshUserDetails jwtRefreshUserDetails = tokenTool.getRefreshToken(request);
        User user = (User) userDetailsService.loadUserByUsername(jwtRefreshUserDetails.getEmail());
        String accessToken = tokenTool.createAccessToken(user);
        String refreshToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        //Send token to user. With response outputStream.
        tokenTool.sendTokensToUser(response, accessToken, refreshToken);

        return false;
    }

}
