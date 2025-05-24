package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.UserService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the {@link UserService} interface.
 * Provides user management functionalities such as registration, login, update, and deletion.
 */
public class UserServiceImpl implements UserService {
    private final JSONStorageService jsonStorageService;

    /**
     * Constructs a new UserServiceImpl with the specified JSONStorageService.
     *
     * @param jsonStorageService the JSON storage service to use
     */
    public UserServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeUserCollection();
    }

    /**
     * Initializes the user collection in the storage if it does not exist.
     */
    private void initializeUserCollection() {
        if (!jsonStorageService.collectionExists(User.class)) {
            jsonStorageService.initializeCollection(User.class);
        }
    }

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @throws RuntimeException if the email already exists
     */
    @Override
    public void register(User user) {
        if (emailExists(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        } else {
            jsonStorageService.insert(user);
        }
    }

    /**
     * Logs in a user with the specified email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the logged-in user
     * @throws RuntimeException if the email or password is invalid
     */
    @Override
    public User login(String email, String password) {
        return jsonStorageService.findAll(User.class).stream()
                .filter(u -> email.equals(u.getEmail()) && password.equals(u.getPassword()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }

    /**
     * Checks if an email already exists.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    @Override
    public boolean emailExists(String email) {
        return jsonStorageService.findAll(User.class).stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the user ID
     * @return the user with the specified ID
     * @throws RuntimeException if the user is not found
     */
    @Override
    public User getUserById(String id) {
        Optional<User> user = jsonStorageService.findById(id, User.class);
        return user.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    @Override
    public List<User> getAllUsers() {
        return jsonStorageService.findAll(User.class);
    }

    /**
     * Updates the information of a user.
     *
     * @param user the user to update
     * @throws RuntimeException if the user is not found
     */
    @Override
    public void updateUser(User user) {
        if (jsonStorageService.findById(user.getId(), User.class).isEmpty()) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        jsonStorageService.update(user, User.class);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the user ID
     * @throws RuntimeException if the user is not found
     */
    @Override
    public void deleteUserById(String id) {
        User user = getUserById(id);
        jsonStorageService.delete(user, User.class);
    }

    /**
     * Adds a bill type to the user's set of bill types.
     *
     * @param id       the user ID
     * @param billType the bill type to add
     * @return the updated set of bill types
     * @throws RuntimeException if the user is not found
     */
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

    /**
     * Retrieves the set of bill types for a user.
     *
     * @param id the user ID
     * @return the set of bill types
     * @throws RuntimeException if the user is not found
     */
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
