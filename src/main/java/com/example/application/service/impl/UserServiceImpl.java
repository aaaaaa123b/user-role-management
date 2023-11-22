package com.example.application.service.impl;

import com.example.application.enumeration.Role;
import com.example.application.model.User;
import com.example.application.service.UserService;


import java.util.*;

public class UserServiceImpl implements UserService {

    public Map<Integer, User> users = new HashMap<>();

    private static UserServiceImpl instance;

    public static UserServiceImpl getInstance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }

        return instance;
    }

    private UserServiceImpl() {
        Set<Role> user1roles = new HashSet<>();
        user1roles.add(Role.USER);
        user1roles.add(Role.ADMIN);
        User user1 = new User(null, "user1", "123", user1roles);

        addUser(user1);

        Set<Role> user2roles = new HashSet<>();
        user2roles.add(Role.USER);
        User user2 = new User(null, "user2", "123", user2roles);

        addUser(user2);
    }


    @Override
    public User addUser(User user) {
        int nextID = 0;

        if (!users.isEmpty()) {
            nextID = users.keySet().stream()
                    .max(Integer::compareTo)
                    .get() + 1;
        }

        user.setId(nextID);

        users.put(nextID, user);
        return user;
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }

    @Override
    public User updateUser(int id, User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public User findById(int id) {
        System.out.println(users.get(id));
        return users.get(id);
    }

    public Optional<User> getUserByUsername(String login) {
        Optional<User> users1 = users.values().stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();

        return users1;
    }
}
