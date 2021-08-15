package com.msavchuk.controller;

import com.msavchuk.config.constant.ApiVersion;
import com.msavchuk.dto.UserDto;
import com.msavchuk.persistence.model.User;
import com.msavchuk.persistence.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
@ResponseBody
@RequestMapping(ApiVersion.API_PREFIX + "/users")
@Slf4j
public class UserController {

    private final String gatewayDns;

    private final UserService userService;

    @Autowired
    public UserController(@Value("${gateway.dns}") String gatewayDns, UserService userService) {
        this.gatewayDns = gatewayDns;
        this.userService = userService;
    }


    @PreAuthorize("hasAuthority('view_users')")
    @GetMapping(value = {"/", ""})
    public ResponseEntity<Page<User>> users(Pageable pageable) {
        Page<User> users = userService.findUsers(pageable);

        return ResponseEntity.ok(users);
    }


    @PreAuthorize("hasAuthority('view_users')")
    @GetMapping(value = {"/{id}"})
    public ResponseEntity<User> user(@PathVariable("id") Long id) {
        User user = userService.findById(id);

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('edit_users')")
    @PutMapping(value = {"/{id}"})
    public ResponseEntity<User> put(@PathVariable("id") Long id, @RequestBody @Validated UserDto userDto) {
        User user = userService.updateUser(userDto);

        return ResponseEntity
                .created(URI.create(gatewayDns + ApiVersion.API_PREFIX + "/users/" + user.getId()))
                .body(user);
    }

    @PreAuthorize("hasAuthority('edit_users')")
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity delete(@PathVariable("id") Long id, final HttpServletRequest request) {
        userService.deleteUser(id);

        return ResponseEntity.ok().build();
    }

}
