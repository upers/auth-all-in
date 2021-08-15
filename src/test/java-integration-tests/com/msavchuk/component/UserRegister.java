package com.msavchuk.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msavchuk.security.jwt.dto.TokenDto;
import com.msavchuk.config.constant.ApiVersion;
import com.msavchuk.dto.MessageDto;
import com.msavchuk.dto.PasswordDto;
import com.msavchuk.dto.UserLoginDto;
import com.msavchuk.dto.UserRegistrationDto;
import com.msavchuk.persistence.dao.PasswordResetTokenRepository;
import com.msavchuk.persistence.dao.UserRepository;
import com.msavchuk.persistence.dao.VerificationTokenRepository;
import com.msavchuk.persistence.model.PasswordResetToken;
import com.msavchuk.persistence.model.Role;
import com.msavchuk.persistence.model.User;
import com.msavchuk.persistence.model.VerificationToken;
import com.msavchuk.persistence.service.UserService;
import com.msavchuk.tool.HttpEntityBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static junit.framework.TestCase.*;
import static org.springframework.http.HttpMethod.POST;

@Component
@Slf4j
public class UserRegister {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper mapper;

    private volatile String apiEntryPoint;

    private volatile String gatewayEntryPoint;

    private volatile UserRegistrationDto userDto;

    @PostConstruct
    public void postConstructor() {
        String port = environment.getProperty("server.port");
        apiEntryPoint = "http://localhost:" + port + ApiVersion.API_PREFIX;
        gatewayEntryPoint = "http://localhost:8080" + ApiVersion.API_PREFIX;
    }

    public UserRegister build(UserRegistrationDto userDto) {
        this.userDto = userDto;
        return this;
    }

    public UserRegister register() {
        assertNotNull(userDto);

        ResponseEntity<?> responseEntity = restTemplate
                .postForEntity(apiEntryPoint + "/users/logic/registration", userDto, MessageDto.class);
        log.info(responseEntity.getBody().toString());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        return this;
    }

    public UserRegister resendRegistrationToken() {
        assertNotNull(userDto);

        User user = userService.findUserByEmail(userDto.getEmail());
        VerificationToken oldVerificationToken = verificationTokenRepository.findByUser(user);
        assertNotNull("Old verification token is not exist", oldVerificationToken);

        ResponseEntity responseEntity = restTemplate
                .getForEntity(apiEntryPoint + "/users/logic/resend-registration-token?email=" + userDto.getEmail(),
                        MessageDto.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        VerificationToken newVerificationToken = verificationTokenRepository.findByUser(user);
        assertNotNull("New verification token wasn't create", oldVerificationToken);
        assertNotSame("Old verification token is the same with new", oldVerificationToken.getToken(),
                newVerificationToken.getToken());

        return this;
    }

    public UserRegister confirm() {
        assertNotNull(userDto);

        User user = userService.findUserByEmail(userDto.getEmail());
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
        ResponseEntity responseEntity = restTemplate
                .getForEntity(apiEntryPoint + "/users/logic/registration-confirm?token=" + verificationToken.getToken(),
                        MessageDto.class);
        HttpStatus confirmStatus = responseEntity.getStatusCode();
        assertEquals(HttpStatus.OK, confirmStatus);

        return this;
    }

    public UserRegister addRole(Role role) {
        assertNotNull(userDto);

        User user = userService.findUserByEmail(userDto.getEmail());
        user.getRoles().add(role);

        userRepository.save(user);

        return this;
    }

    public UserRegister resetPassword(String newPassword) {
        assertNotNull(userDto);

        ResponseEntity responseEntity = restTemplate
                .postForEntity(apiEntryPoint + "/users/logic/reset-password?email=" + userDto.getEmail() +
                        "&client-url=http://localhost:8080/", null, MessageDto.class);
        HttpStatus confirmStatus = responseEntity.getStatusCode();
        log.info(responseEntity.getBody().toString());
        assertEquals("Reset password status is not correct", HttpStatus.CREATED, confirmStatus);

        User user = userService.findUserByEmail(userDto.getEmail());
        assertNotNull("User not registered", user);
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user);
        assertNotNull("Service didn't create password reset token, check logic.", passwordResetToken);

        responseEntity = restTemplate
                .getForEntity(
                        apiEntryPoint + "/users/logic/reset-password-confirm?token=" + passwordResetToken.getToken(),
                        MessageDto.class);
        confirmStatus = responseEntity.getStatusCode();
        assertEquals("Link from email for reset password is incorrect.", HttpStatus.MOVED_PERMANENTLY, confirmStatus);

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setNewPassword(newPassword);

        responseEntity = restTemplate
                .postForEntity(apiEntryPoint + "/users/logic/change-password?token=" + passwordResetToken.getToken(),
                        passwordDto,
                        MessageDto.class);
        confirmStatus = responseEntity.getStatusCode();
        assertEquals("Password wasn't change", HttpStatus.OK, confirmStatus);

        userDto.setPassword(newPassword);
        userDto.setMatchingPassword(newPassword);

        return this;
    }

    public UserRegister updateUserPassword(String newPassword) {
        assertNotNull(userDto);

        String auth = getAccessToken();
        assertNotNull(userDto.getPassword());
        PasswordDto passwordDto = new PasswordDto(userDto.getPassword(), newPassword);
        HttpEntity<PasswordDto> requestEntity = HttpEntityBuilder.authEntity(auth, passwordDto);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/users/logic/update-password", POST, requestEntity, String.class);
        HttpStatus confirmStatus = responseEntity.getStatusCode();
        assertEquals("Update password status is not correct", HttpStatus.OK, confirmStatus);

        userDto.setPassword(newPassword);
        userDto.setMatchingPassword(newPassword);

        return this;
    }

    public String getAccessToken() {
        assertNotNull(userDto);
        UserLoginDto userLoginDto = new UserLoginDto(userDto.getEmail(), userDto.getPassword());

        ResponseEntity responseEntity = restTemplate
                .postForEntity(gatewayEntryPoint + "/oauth/login", userLoginDto, String.class);

        log.info(responseEntity.getBody().toString());
        assertEquals("Authorization response incorrect", HttpStatus.OK, responseEntity.getStatusCode());
        try {
            TokenDto tokenDto = mapper.readValue(responseEntity.getBody().toString(), TokenDto.class);
            String authorization = tokenDto.getAccessToken();

            return authorization;
        } catch (IOException e) {
            assertTrue("Unable to map server response on " + TokenDto.class.getName(), false);
        }

        return null;
    }

    public String getRefreshToken() {
        assertNotNull(userDto);
        UserLoginDto userLoginDto = new UserLoginDto(userDto.getEmail(), userDto.getPassword());

        ResponseEntity responseEntity = restTemplate
                .postForEntity(gatewayEntryPoint + "/oauth/login", userLoginDto, String.class);

        assertEquals("Authorization response incorrect", HttpStatus.OK, responseEntity.getStatusCode());
        try {
            TokenDto tokenDto = mapper.readValue(responseEntity.getBody().toString(), TokenDto.class);
            String refreshToken = tokenDto.getRefreshToken();

            return refreshToken;
        } catch (IOException e) {
            assertTrue("Unable to map server response on " + TokenDto.class.getName(), false);
        }

        return null;
    }

}
