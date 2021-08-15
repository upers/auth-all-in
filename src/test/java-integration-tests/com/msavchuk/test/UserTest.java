package com.msavchuk.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.msavchuk.component.UserRegister;
import com.msavchuk.config.UserConfig;
import com.msavchuk.dto.UserRegistrationDto;
import com.msavchuk.email.RegistrationEmailSender;
import com.msavchuk.persistence.dao.RoleRepository;
import com.msavchuk.persistence.dao.UserRepository;
import com.msavchuk.persistence.model.Role;
import com.msavchuk.persistence.model.User;
import com.msavchuk.tool.HttpEntityBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpMethod.*;

@Slf4j
public class UserTest extends AbstractIntegrationTest {
    private static boolean setUpIsDone = false;

    private static String userAuthorization;

    private static String adminAuthorization;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRegister userRegister;

    @Autowired
    private RegistrationEmailSender registrationEmailSender;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

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
    public void crudNewUser() {
        String userEmail = "newuser32@gmail.com";
        String password = "TrswTEST402wg42";
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                                                                     .email(userEmail)
                                                                     .firstName("test")
                                                                     .lastName("test").password(password)
                                                                     .matchingPassword(password)
                                                                     .roles(Sets.newHashSet(Role
                                                                             .builder().id(2l).build()))
                                                                     .build();

        try {
            String str = null;
            str = mapper.writeValueAsString(userRegistrationDto);
            log.info(str);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //POST
        HttpEntity requestEntity = HttpEntityBuilder.authEntity(adminAuthorization, userRegistrationDto);

        ResponseEntity<User> responseEntity = restTemplate
                .exchange(apiEntryPoint + "/users", POST, requestEntity, User.class);
        assertEquals("Unable to post new user", HttpStatus.CREATED.value(), responseEntity.getStatusCodeValue());
        assertNotNull("Location header is null", responseEntity.getHeaders().get(HttpHeaders.LOCATION));
        assertNotNull("Posted user returned withoud id", responseEntity.getBody().getId());

        //GET
        String url = responseEntity.getHeaders().get(HttpHeaders.LOCATION).get(0);
        requestEntity = HttpEntityBuilder.emptyAuthEntity(adminAuthorization);
        User recentlyPostedUser = responseEntity.getBody();
        assertNotNull("Recently posted user not found by location url", recentlyPostedUser);

        //PUT
        userRegistrationDto.setId(recentlyPostedUser.getId());
        userRegistrationDto.setEmail("changedemail@gmail.com");
        userRegistrationDto.setRoles(Sets.newHashSet(Role.builder().id(2l).build(), Role.builder().id(1l).build()));
        requestEntity = HttpEntityBuilder.authEntity(adminAuthorization, userRegistrationDto);
        responseEntity = restTemplate
                .exchange(apiEntryPoint + "/users/" + recentlyPostedUser.getId(), PUT, requestEntity, User.class);
        assertEquals("Unable to put user", HttpStatus.CREATED.value(), responseEntity.getStatusCodeValue());
        assertNotNull("Location header is null", responseEntity.getHeaders().get(HttpHeaders.LOCATION));
        User putUser = responseEntity.getBody();
        assertEquals("Put method created new id!!!!!", recentlyPostedUser.getId(), putUser.getId());

        //DELETE
        requestEntity = HttpEntityBuilder.emptyAuthEntity(adminAuthorization);
        responseEntity = restTemplate
                .exchange(apiEntryPoint + "/users/" + recentlyPostedUser.getId(), DELETE, requestEntity, User.class);
        assertEquals("Unable to delete user", HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

    }


}
