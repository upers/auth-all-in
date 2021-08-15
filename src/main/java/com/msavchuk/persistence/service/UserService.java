package com.msavchuk.persistence.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.google.common.collect.Sets;
import com.msavchuk.dto.UserDto;
import com.msavchuk.exception.auth.InvalidTokenException;
import com.msavchuk.exception.auth.UserAlreadyExistException;
import com.msavchuk.persistence.dao.PasswordResetTokenRepository;
import com.msavchuk.persistence.dao.RoleRepository;
import com.msavchuk.persistence.dao.UserRepository;
import com.msavchuk.persistence.dao.VerificationTokenRepository;
import com.msavchuk.persistence.model.PasswordResetToken;
import com.msavchuk.persistence.model.Role;
import com.msavchuk.persistence.model.User;
import com.msavchuk.persistence.model.VerificationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SessionRegistry sessionRegistry;

    @Autowired
    public UserService(UserRepository repository,
                       VerificationTokenRepository tokenRepository,
                       PasswordResetTokenRepository passwordTokenRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       SessionRegistry sessionRegistry) {
        this.repository = repository;
        this.tokenRepository = tokenRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.sessionRegistry = sessionRegistry;
    }


    public User findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public User updateUser(final UserDto userDto) {
        if (userDto.getId() == null || !repository.existsById(userDto.getId()))
            throw new IllegalStateException("User id must be specified");

        User user = map(userDto);

        return repository.save(user);
    }

    private User map(UserDto userDto) {
        final User user = new User();

        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setUsing2FA(userDto.getIsUsing2FA());
        user.setEnabled(userDto.getEnabled());
        user.setRoles(userDto.getRoles());

        return user;
    }

    public void deleteUser(Long id) {
        repository.findById(id).ifPresent(this::deleteUser);
    }

    public void deleteUser(final User user) {
        final VerificationToken verificationToken = tokenRepository.findByUser(user);

        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        final PasswordResetToken passwordToken = passwordTokenRepository.findByUser(user);

        if (passwordToken != null) {
            passwordTokenRepository.delete(passwordToken);
        }

        repository.delete(user);
    }

    public Page<User> findUsers(Pageable pageable) {
        return repository.findAll(pageable);
    }


}