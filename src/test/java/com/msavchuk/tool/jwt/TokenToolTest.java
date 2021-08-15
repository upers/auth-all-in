package com.msavchuk.tool.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msavchuk.security.jwt.dto.JwtGrantedAuthority;
import com.msavchuk.security.jwt.dto.JwtRefreshUserDetails;
import com.msavchuk.security.jwt.logic.ClientTokenTool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TokenToolTest {

    protected static final String accessTokenPrefix = "Bearer ";

    protected static final String refreshTokenPrefix = "Refresh ";

    protected static final long accessTokenExpirationTime = 111115000l;

    protected static final long refreshTokenExpirationTime = 10000l;

    protected ClientTokenTool clientTokenTool;

    protected TokenTool tokenTool;

    protected Algorithm algorithm;

    protected Algorithm publicAlgorithm;

    @Before
    public void before() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String prKey = FileUtils.readFileFromClassPath("pk.key");
        String pubKey = FileUtils.readFileFromClassPath("pub.key");
        algorithm = encryptAlgorithm(prKey, pubKey);
        publicAlgorithm = publicEncryptAlgorithm(pubKey);

        clientTokenTool = new ClientTokenTool(accessTokenPrefix, publicAlgorithm, new ObjectMapper());
        tokenTool = new TokenTool(accessTokenPrefix,
                refreshTokenPrefix,
                accessTokenExpirationTime,
                refreshTokenExpirationTime,
                algorithm,
                new ObjectMapper());
    }

    @Test
    public void validRefreshToken() throws JsonProcessingException {
        User admin = buildUser("admin", "read", "write");
        String refreshToken = tokenTool.createRefreshToken(admin);

        HttpServletRequest refreshRequest = Mockito.mock(HttpServletRequest.class);
        when(refreshRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(refreshTokenPrefix + refreshToken);

        JwtRefreshUserDetails jwtRefreshUserDetails = tokenTool.getRefreshToken(refreshRequest);
        String userName = jwtRefreshUserDetails.getEmail();
        Set<String> authorities = jwtRefreshUserDetails.getAuthorities()
                                                .stream()
                                                .map(JwtGrantedAuthority::getAuthority)
                                                .collect(Collectors.toSet());
        assertEquals( "admin", userName, "Username is not correct");
        assertTrue(authorities.contains("read"), "Token does not contain 'read' authority");
        assertTrue(authorities.contains("write"), "Token does not contain 'write' authority");
        assertEquals(2, authorities.size(), "Authorities is not correct");
        assertNotNull(jwtRefreshUserDetails.getRefreshTokenSalt());
        assertNotNull(jwtRefreshUserDetails.getSalt());

    }

    protected Algorithm encryptAlgorithm(String pk, String pub) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(java.util.Base64.getDecoder().decode(pk));
        RSAPrivateKey prK = (RSAPrivateKey) kf.generatePrivate(ks);
        X509EncodedKeySpec ks1 = new X509EncodedKeySpec(Base64.getDecoder().decode(pub));
        RSAPublicKey pubK = (RSAPublicKey) kf.generatePublic(ks1);

        return Algorithm.RSA256(pubK, prK);
    }


    protected Algorithm publicEncryptAlgorithm(String pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec ks1 = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey));
        RSAPublicKey pub = (RSAPublicKey) kf.generatePublic(ks1);

        return Algorithm.RSA256(pub, null);
    }

    protected User buildUser(String username, String... authorities) {
        return new User(
                username,
                "123",
                AuthorityUtils.createAuthorityList(authorities)
        );
    }

}
