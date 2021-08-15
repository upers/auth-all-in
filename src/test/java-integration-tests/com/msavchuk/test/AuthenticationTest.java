package com.msavchuk.test;

import com.msavchuk.security.jwt.dto.TokenDto;
import com.msavchuk.component.UserRegister;
import com.msavchuk.config.UserConfig;
import com.msavchuk.dto.ErrorDto;
import com.msavchuk.dto.MessageDto;
import com.msavchuk.dto.PasswordDto;
import com.msavchuk.dto.UserRegistrationDto;
import com.msavchuk.email.RegistrationEmailSender;
import com.msavchuk.persistence.dao.PasswordResetTokenRepository;
import com.msavchuk.persistence.dao.RoleRepository;
import com.msavchuk.persistence.dao.UserRepository;
import com.msavchuk.persistence.dao.VerificationTokenRepository;
import com.msavchuk.persistence.model.PasswordResetToken;
import com.msavchuk.persistence.model.Role;
import com.msavchuk.persistence.model.User;
import com.msavchuk.persistence.service.UserService;
import com.msavchuk.tool.HttpEntityBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;


@Slf4j
public class AuthenticationTest extends AbstractIntegrationTest {

    private static boolean setUpIsDone = false;

    private static String userAuthorization;

    private static String adminAuthorization;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRegister userRegister;

    @Autowired
    private RegistrationEmailSender registrationEmailSender;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment environment;

    @Before
    public void setUp() throws IOException, MessagingException {
        //Mock email sender to do nothing.
        doNothing().when(registrationEmailSender)
                   .sendVerification(Mockito.any(String.class), Mockito.any(String.class));

        UserRegistrationDto userDto = UserRegistrationDto.builder()
                                                         .email(UserConfig.userEmail)
                                                         .firstName(UserConfig.userFirstName)
                                                         .lastName(UserConfig.userLastName)
                                                         .password(UserConfig.password)
                                                         .matchingPassword(UserConfig.password)
                                                         .build();

        UserRegistrationDto adminDto = UserRegistrationDto.builder()
                                                          .email(UserConfig.adminEmail)
                                                          .firstName(UserConfig.adminFirstName)
                                                          .lastName(UserConfig.adminLastName)
                                                          .password(UserConfig.adminPassword)
                                                          .matchingPassword(UserConfig.adminPassword)
                                                          .build();

        userAuthorization = userRegister.build(userDto).register().confirm().getAccessToken();

        Role adminRole = roleRepository.findByName("admin");
        adminAuthorization = userRegister.build(adminDto).register().confirm().addRole(adminRole).getAccessToken();
    }

    @After
    public void tearDown() {
        userRepository.deleteByEmailIn(List.of(UserConfig.userEmail, UserConfig.adminEmail));
    }

    @Test
    public void userRole() {
        HttpEntity requestEntity = HttpEntityBuilder.emptyAuthEntity(userAuthorization);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/resources/test-read", GET, requestEntity, String.class);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

        responseEntity = restTemplate
                .exchange(apiEntryPoint + "/resources/test-write", GET, requestEntity, String.class);
        assertEquals(HttpStatus.FORBIDDEN.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void adminRole() {
        HttpEntity requestEntity = HttpEntityBuilder.emptyAuthEntity(adminAuthorization);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/resources/test-read", GET, requestEntity, String.class);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

        responseEntity = restTemplate
                .exchange(apiEntryPoint + "/resources/test-write", GET, requestEntity, String.class);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void registerAndResetPassword() {
        String userEmail = "test@user.com";
        String password = "TrswTEST402wg42";
        UserRegistrationDto userDto =
                UserRegistrationDto.builder().email(userEmail).firstName("test").lastName("test").password(password)
                                   .matchingPassword(password).build();

        String newUserAuthorization = userRegister.build(userDto).register().resendRegistrationToken().confirm()
                                                  .getAccessToken();

        log.info("Token: " + newUserAuthorization);
        HttpEntity requestEntity = HttpEntityBuilder.emptyAuthEntity(newUserAuthorization);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/resources/test-read", GET, requestEntity, String.class);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

        newUserAuthorization = userRegister.build(userDto).resetPassword("New29238521HardPas").getAccessToken();

        requestEntity = HttpEntityBuilder.emptyAuthEntity(newUserAuthorization);
        responseEntity =
                restTemplate.exchange(apiEntryPoint + "/resources/test-read", GET, requestEntity, String.class);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void refreshToken() {
        String userEmail = "testrefresh@user.com";
        String password = "TrswTEST402wg42";
        UserRegistrationDto userDto = UserRegistrationDto.builder()
                                                         .email(userEmail)
                                                         .firstName("test")
                                                         .lastName("test")
                                                         .password(password)
                                                         .matchingPassword(password)
                                                         .build();

        String refreshToken = userRegister.build(userDto)
                                          .register()
                                          .resendRegistrationToken()
                                          .confirm()
                                          .getRefreshToken();

        String oldAccessToken = userRegister.build(userDto).getAccessToken();

        HttpEntity requestEntity = HttpEntityBuilder.authEntity(refreshToken, null);
        ResponseEntity<TokenDto> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/oauth/refresh-token", POST, requestEntity,
                        TokenDto.class);

        assertEquals("Expected 200 responce from \"refresh-token\" endpoint", HttpStatus.OK,
                responseEntity.getStatusCode());
        TokenDto tokenDto = responseEntity.getBody();
        String newAccessToken = tokenDto.getAccessToken();
        log.info("Old token: " + oldAccessToken);
        log.info("New token: " + newAccessToken);
        assertNotEquals("Old access token the same with new access token", oldAccessToken, newAccessToken);
        assertEquals("Refresh token have to be the same.", refreshToken, tokenDto.getRefreshToken());

        requestEntity = HttpEntityBuilder.emptyAuthEntity(newAccessToken);
        ResponseEntity<String> respEntity = restTemplate
                .exchange(apiEntryPoint + "/resources/test-read", GET, requestEntity, String.class);
        assertEquals("New access token don't give access to read", HttpStatus.OK.value(),
                respEntity.getStatusCodeValue());
    }

    @Test
    public void registerAndResetConfirmation() {
        String userEmail = "test2@user.com";
        String password = "TrswWTEST402wg42";
        UserRegistrationDto userDto = UserRegistrationDto.builder()
                                                         .email(userEmail)
                                                         .firstName("test2")
                                                         .lastName("test2")
                                                         .password(password)
                                                         .matchingPassword(password)
                                                         .build();

        String newUserAuthorization = userRegister.build(userDto)
                                                  .register()
                                                  .confirm()
                                                  .updateUserPassword("New29238521HardPas")
                                                  .getAccessToken();

        HttpEntity requestEntity = HttpEntityBuilder.emptyAuthEntity(newUserAuthorization);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/resources/test-read", GET, requestEntity, String.class);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void resetPasswordExceptionHandling() {
        String userEmail = "test12@user.com";
        String password = "TrswTEST402wg42";
        UserRegistrationDto userDto =
                UserRegistrationDto.builder().email(userEmail).firstName("test").lastName("test").password(password)
                                   .matchingPassword(password).build();

        String newUserAuthorization = userRegister.build(userDto).register().resendRegistrationToken().confirm()
                                                  .getAccessToken();
        HttpEntity requestEntity = HttpEntityBuilder.emptyAuthEntity(newUserAuthorization);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/resources/test-read", GET, requestEntity, String.class);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

        ResponseEntity<MessageDto> responseResetEntity = restTemplate
                .postForEntity(apiEntryPoint + "/users/logic/reset-password?email=" + userDto.getEmail() +
                        "&client-url=http://localhost:8080", null, MessageDto.class);
        HttpStatus confirmStatus = responseResetEntity.getStatusCode();
        assertEquals("Reset password status is not correct", HttpStatus.CREATED, confirmStatus);

        User user = userService.findUserByEmail(userDto.getEmail());
        assertNotNull("User not registered", user);
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user);
        assertNotNull("Service didn't create password reset token, check logic.", passwordResetToken);

        passwordResetTokenRepository.delete(passwordResetToken);
        ResponseEntity<ErrorDto> responseConfirmEntity = restTemplate
                .getForEntity(
                        apiEntryPoint + "/users/logic/reset-password-confirm?token=" + passwordResetToken.getToken(),
                        ErrorDto.class);
        confirmStatus = responseConfirmEntity.getStatusCode();
        assertEquals("Expected 404 invalid token", HttpStatus.NOT_FOUND, confirmStatus);

        responseResetEntity = restTemplate
                .postForEntity(apiEntryPoint + "/users/logic/reset-password?email=" + userDto.getEmail() +
                        "&client-url=http://localhost:8080", null, MessageDto.class);
        confirmStatus = responseResetEntity.getStatusCode();
        assertEquals("Reset password status is not correct", HttpStatus.CREATED, confirmStatus);

        user = userService.findUserByEmail(userDto.getEmail());
        assertNotNull("User not registered", user);
        passwordResetToken = passwordResetTokenRepository.findByUser(user);
        assertNotNull("Service didn't create password reset token, check logic.", passwordResetToken);

        passwordResetToken.setExpiryDate(new Date());
        passwordResetTokenRepository.save(passwordResetToken);

        responseConfirmEntity = restTemplate
                .getForEntity(
                        apiEntryPoint + "/users/logic/reset-password-confirm?token=" + passwordResetToken.getToken(),
                        ErrorDto.class);
        confirmStatus = responseConfirmEntity.getStatusCode();
        assertEquals("Expected 400 token expired", HttpStatus.GONE, confirmStatus);
    }

    @Test
    public void updatePasswordExceptionHandling() {
        String userEmail = "test1212@user.com";
        String password = "TrswTEST402wg42";
        UserRegistrationDto userDto =
                UserRegistrationDto.builder().email(userEmail).firstName("test").lastName("test").password(password)
                                   .matchingPassword(password).build();

        String newUserAuthorization = userRegister.build(userDto).register().resendRegistrationToken().confirm()
                                                  .getAccessToken();

        PasswordDto passwordDto = new PasswordDto("IncorrectOldPassword", "NewPassword42361");
        HttpEntity<PasswordDto> requestEntity = HttpEntityBuilder.authEntity(newUserAuthorization, passwordDto);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/users/logic/update-password", POST, requestEntity, String.class);
        HttpStatus confirmStatus = responseEntity.getStatusCode();
        assertEquals("Expected 404 Bad old password", HttpStatus.BAD_REQUEST, confirmStatus);
    }

}
