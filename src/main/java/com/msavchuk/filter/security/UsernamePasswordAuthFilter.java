package com.msavchuk.filter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msavchuk.dto.UserLoginDto;
import com.msavchuk.filter.security.abstraction.RequestMatcherFilter;
import com.msavchuk.tool.jwt.TokenTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
@Slf4j
public class UsernamePasswordAuthFilter extends RequestMatcherFilter {

    private final AuthenticationManager authenticationManager;

    private final ObjectMapper mapper;

    private final TokenTool tokenTool;

    @Autowired
    public UsernamePasswordAuthFilter(
            @Value("${security.login.uri}") String uri,
            ObjectMapper objectMapper,
            @Qualifier("tokenTool") TokenTool tokenTool,
            AuthenticationManager authenticationManager
    ) {
        super(uri, "POST");
        this.tokenTool = tokenTool;
        this.mapper = objectMapper;
        this.authenticationManager = authenticationManager;
    }

    public boolean executeInternal(HttpServletRequest req, HttpServletResponse res) throws IOException {
        UserLoginDto userLoginDto = mapper
                .readValue(req.getInputStream(), UserLoginDto.class);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(),
                        userLoginDto.getPassword(),
                        new ArrayList<>()));

        User user = (User) authentication.getPrincipal();

        String accessToken = tokenTool.createAccessToken(user);
        String refreshToken = tokenTool.createRefreshToken(user);

        //Send token to user. With response outputStream.
        tokenTool.sendTokensToUser(res, accessToken, refreshToken);

        return false;
    }

}
