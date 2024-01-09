package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.repository.UserRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements CRUDService<User> {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public void delete(User entity) {
        userRepository.delete(entity);
    }

    public boolean checkUsername (String username) {
        return userRepository.existsByUsernameIgnoringCase(username);
    }

    public User findByUsername(String username) {
       return userRepository.findByUsername(username);
    }

    public boolean hasUser () {
        return userRepository.count() > 0;
    }
}
