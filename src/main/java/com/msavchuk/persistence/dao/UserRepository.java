package com.msavchuk.persistence.dao;


import com.msavchuk.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    @Override
    void delete(User user);

    void deleteByEmail(String email);

    void deleteByEmailIn(Collection<String> email);

}
