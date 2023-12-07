package com.example.inovasiyanotebook.service;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public void saveNewUser (User user) {
        userRepository.save(user);
    }

    public boolean checkUsername (String username) {
        return userRepository.existsByUsernameIgnoringCase(username);
    }

    public User findByUsername(String username) {
       return userRepository.findByUsername(username);
    }
}
