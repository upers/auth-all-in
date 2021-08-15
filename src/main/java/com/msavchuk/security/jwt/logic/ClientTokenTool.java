package com.msavchuk.security.jwt.logic;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msavchuk.security.jwt.dto.JwtUserDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component("clientTokenTool")
public class ClientTokenTool {

    private static final Logger log = LogManager.getLogger(ClientTokenTool.class);

    protected final String accessTokenPrefix;

    protected final ObjectMapper mapper;

    protected final JWTVerifier jwtVerifier;

    @Autowired
    public ClientTokenTool(
            @Value("${security.access.token.prefix}") String accessTokenPrefix,
            @Qualifier("jwtPrivateAlgorithm") Algorithm algorithm,
            ObjectMapper mapper
    ) {
        this.accessTokenPrefix = accessTokenPrefix;
        this.mapper = mapper;
        this.jwtVerifier = JWT.require(algorithm).build();
    }


    /**
     * Map access token from request header "Authorization".
     *
     * @param request {@link HttpServletRequest}
     * @return {@link JwtUserDetails}
     */
    public JwtUserDetails getAccessToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null)
            throw new BadCredentialsException(HttpHeaders.AUTHORIZATION + " not found.");
        if (!token.startsWith(accessTokenPrefix))
            throw new BadCredentialsException("Invalid type of " + HttpHeaders.AUTHORIZATION + " header. Got " + token);

        String cleanToken = token.replace(accessTokenPrefix, "");
        // parse the token.
        String payload = jwtVerifier.verify(cleanToken).getSubject();

        try {
            return mapper.readValue(payload, JwtUserDetails.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BadCredentialsException("Invalid token: " + token);
        }
    }

}
