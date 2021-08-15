package com.msavchuk.tool.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msavchuk.security.jwt.dto.JwtRefreshUserDetails;
import com.msavchuk.security.jwt.dto.JwtUserDetails;
import com.msavchuk.security.jwt.dto.TokenDto;
import com.msavchuk.security.jwt.logic.ClientTokenTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component("tokenTool")
public class TokenTool extends ClientTokenTool {

    private static final Logger log = LogManager.getLogger(TokenTool.class);

    @Value("${security.refresh.token.prefix}")
    private final String refreshTokenPrefix;

    @Value("${security.access.token.expiration}")
    private final Long accessTokenExpirationTime;

    @Value("${security.refresh.token.expiration}")
    private final Long refreshTokenExpirationTime;

    private final Algorithm algorithm;

    public TokenTool(
            @Value("${security.access.token.prefix}") String accessTokenPrefix,
            @Value("${security.refresh.token.prefix}") String refreshTokenPrefix,
            @Value("${security.access.token.expiration}") Long accessTokenExpirationTime,
            @Value("${security.refresh.token.expiration}") Long refreshTokenExpirationTime,
            @Qualifier("jwtPrivateAlgorithm") Algorithm algorithm,
            ObjectMapper mapper) {
        super(accessTokenPrefix, algorithm, mapper);
        this.algorithm = algorithm;
        this.refreshTokenPrefix = refreshTokenPrefix;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    public String createAccessToken(User user) throws JsonProcessingException {
        String accessTokenPayload = mapper.writeValueAsString(new JwtUserDetails(user));
        return accessTokenPrefix + JWT.create().withSubject(accessTokenPayload)
                                      .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                                      .sign(algorithm);
    }

    public String createRefreshToken(User user) throws JsonProcessingException {
        String refreshTokenPayload = mapper.writeValueAsString(new JwtRefreshUserDetails(user));

        return refreshTokenPrefix + JWT.create().withSubject(refreshTokenPayload)
                                       .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                                       .sign(algorithm);
    }


    /**
     * Map refresh token from request header "Authorization".
     *
     * @param request {@link HttpServletRequest}
     * @return {@link JwtRefreshUserDetails}
     */
    public JwtRefreshUserDetails getRefreshToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null)
            throw new BadCredentialsException(HttpHeaders.AUTHORIZATION + "not found.");
        if (!token.startsWith(refreshTokenPrefix))
            throw new BadCredentialsException("Invalid type of " + HttpHeaders.AUTHORIZATION + " header. Got " + token);

        // parse the token.
        String payload = jwtVerifier
                            .verify(token.replace(refreshTokenPrefix, "")).getSubject();

        try {
            return mapper.readValue(payload, JwtRefreshUserDetails.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * Send token to client by using {@link HttpServletResponse#getOutputStream()}. </br>
     * Token object is: {@link TokenDto}
     *
     * @throws IOException
     */
    public void sendTokensToUser(HttpServletResponse res, String accessToken, String refreshToken) throws IOException {
        res.addHeader(HttpHeaders.AUTHORIZATION, accessToken);
        res.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        res.setStatus(HttpStatus.OK.value());
        mapper.writeValue(res.getOutputStream(), new TokenDto(accessToken, refreshToken));
    }

}
