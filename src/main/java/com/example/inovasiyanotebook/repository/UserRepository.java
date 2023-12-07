package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsernameIgnoringCase(String username);

    User findByUsername (String username);
}
