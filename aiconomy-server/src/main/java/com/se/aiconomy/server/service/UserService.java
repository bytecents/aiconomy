package com.se.aiconomy.server.service;

import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.model.entity.User;

import java.util.List;
import java.util.Set;

/**
 * UserService interface provides methods for user management, authentication,
 * and bill type operations.
 */
public interface UserService {

    /**
     * Registers a new user.
     *
     * @param user the user entity to register
     */
    void register(User user);

    /**
     * Authenticates a user with email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the authenticated user entity, or null if authentication fails
     */
    User login(String email, String password);

    /**
     * Checks if an email already exists in the system.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    boolean emailExists(String email);

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the user ID
     * @return the user entity, or null if not found
     */
    User getUserById(String id);

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all user entities
     */
    List<User> getAllUsers();

    /**
     * Updates the information of an existing user.
     *
     * @param user the user entity with updated information
     */
    void updateUser(User user);

    /**
     * Deletes a user by their unique ID.
     *
     * @param id the user ID
     */
    void deleteUserById(String id);

    /**
     * Adds a new bill type for a user.
     *
     * @param id       the user ID
     * @param billType the bill type to add
     * @return the updated set of bill types for the user
     */
    Set<DynamicBillType> addBillType(String id, DynamicBillType billType);

    /**
     * Retrieves the list of bill types for a user.
     *
     * @param id the user ID
     * @return the set of bill types for the user
     */
    Set<DynamicBillType> getBillTypes(String id);
}
