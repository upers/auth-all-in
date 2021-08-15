package com.msavchuk.filter;

import com.msavchuk.filter.security.JwtRefreshTokenFilter;
import com.msavchuk.tool.jwt.TokenToolTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.msavchuk.config.constant.ApiVersion.API_PREFIX;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class JwtRefreshTokenFilterTest extends TokenToolTest {

    public static final String REFRESH_TOKEN_URI = "/oauth/refresh_token";

    @Before
    public void before() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        super.before();
    }

    @Test
    public void test() throws IOException, ServletException {
        var userDetailsService = mock(UserDetailsService.class);
        //mock UserDetails service
        var userDetails = buildUserDetails("admin@admin.com", "read", "write");
        when(userDetailsService.loadUserByUsername("admin@admin.com")).thenReturn(userDetails);
        //Build refresh token
        User adminUser = buildUser("admin@admin.com", "read", "write");
        String refreshToken = tokenTool.createRefreshToken(adminUser);
        //Init refresh servlet request
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(refreshTokenPrefix + refreshToken);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn(API_PREFIX + REFRESH_TOKEN_URI);

        var response = mock(HttpServletResponse.class);
        var responseOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(responseOutputStream);

        var filter = new JwtRefreshTokenFilter(this.tokenTool, userDetailsService, REFRESH_TOKEN_URI);
        boolean doNotExecuteNextFilter = filter.execute(request, response);
        assertFalse(doNotExecuteNextFilter);
    }


    protected UserDetails buildUserDetails(String email, String... authorities) {
        return new org.springframework.security.core.userdetails.User(email, "123",
                true, true, true, true, AuthorityUtils.createAuthorityList(authorities));
    }
}
