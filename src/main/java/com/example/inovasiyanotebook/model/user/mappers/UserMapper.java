package com.example.inovasiyanotebook.model.user.mappers;

import com.example.inovasiyanotebook.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {


    public User createNewUser (String username, String password, String name, String surname, String email) {

        return User.builder()
                .username(username)
                .password(password)
                .firstName(name)
                .lastName(surname)
                .email(email)
                .build();
    }
}
