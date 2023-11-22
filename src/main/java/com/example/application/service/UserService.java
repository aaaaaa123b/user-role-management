package com.example.application.service;

import com.example.application.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User addUser(User user);

    List<User> findAll();

    void deleteUser(int id);

    User updateUser(int id, User user);

    User findById(int id);

    Optional<User> getUserByUsername(String login);
}
