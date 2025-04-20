package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.UserService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserServiceImpl implements UserService {
    private final JSONStorageService jsonStorageService;

    public UserServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeUserCollection();
    }

    private void initializeUserCollection() {
        if (!jsonStorageService.collectionExists(User.class)) {
            jsonStorageService.initializeCollection(User.class);
        }
    }

    @Override
    public void register(User user) {
        if (emailExists(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        } else {
            jsonStorageService.insert(user);
        }
    }

    @Override
    public User login(String email, String password) {
        return jsonStorageService.findAll(User.class).stream()
            .filter(u -> email.equals(u.getEmail()) && password.equals(u.getPassword()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }

    @Override
    public boolean emailExists(String email) {
        return jsonStorageService.findAll(User.class).stream()
            .anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public User getUserById(String id) {
        Optional<User> user = jsonStorageService.findById(id, User.class);
        return user.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return jsonStorageService.findAll(User.class);
    }

    @Override
    public void updateUser(User user) {
        if (jsonStorageService.findById(user.getId(), User.class).isEmpty()) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        jsonStorageService.update(user, User.class);
    }

    @Override
    public void deleteUserById(String id) {
        User user = getUserById(id);
        jsonStorageService.delete(user, User.class);
    }

    @Override
    public Set<DynamicBillType> addBillType(String id, DynamicBillType billType) {
        Optional<User> user = jsonStorageService.findById(id, User.class);
        if (user.isPresent()) {
            user.get().getBillTypes().add(billType);
            jsonStorageService.update(user.get(), User.class);
            return user.get().getBillTypes();
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    @Override
    public Set<DynamicBillType> getBillTypes(String id) {
        Optional<User> user = jsonStorageService.findById(id, User.class);
        if (user.isPresent()) {
            return user.get().getBillTypes();
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }
}
