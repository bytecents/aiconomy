package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.langchain.common.model.BillTypeRegistry;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.model.dto.user.request.*;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * User request handler.
 * Responsible for handling all user-related requests.
 */
public class UserRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);
    private static final Set<DynamicBillType> BILL_TYPES = BillTypeRegistry.getInstance().getAllTypes();
    private final UserService userService;

    /**
     * Constructs a UserRequestHandler with the specified UserService.
     *
     * @param userService the user service
     */
    public UserRequestHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles user registration requests.
     *
     * @param request the user registration request
     * @return UserInfo DTO of the newly registered user
     */
    public UserInfo handleRegisterRequest(UserRegisterRequest request) {
        logger.info("Processing register request for email: {}", request.getEmail());

        // Create new user entity
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setPhone(request.getPhoneNumber());
        newUser.setBirthDate(request.getBirthDate());
        newUser.setCurrency(request.getCurrency());
        newUser.setFinancialGoal(request.getFinancialGoal());
        newUser.setMonthlyIncome(request.getMonthlyIncome());
        newUser.setMainExpenseType(request.getMainExpenseType());
        newUser.setBillTypes(BILL_TYPES);

        // Register user
        try {
            userService.register(newUser);
            logger.info("Successfully registered user with email: {}", request.getEmail());
        } catch (Exception e) {
            logger.error("Failed to register user: {}", e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
        return convertToUserInfo(newUser);
    }

    /**
     * Handles user login requests.
     *
     * @param request the user login request
     * @return UserInfo DTO of the logged-in user
     */
    public UserInfo handleLoginRequest(UserLoginRequest request) {
        logger.info("Processing login request for email: {}", request.getEmail());

        User user;
        try {
            user = userService.login(request.getEmail(), request.getPassword());
            logger.info("User successfully logged in: {}", request.getEmail());
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
        return convertToUserInfo(user);
    }

    /**
     * Handles requests to get user information.
     *
     * @param request the user info request
     * @return UserInfo DTO of the user
     */
    public UserInfo handleGetUserInfoRequest(UserInfoRequest request) {
        logger.info("Processing get user info request for userId: {}", request.getUserId());

        try {
            User user = userService.getUserById(request.getUserId());
            logger.info("Successfully retrieved user info for userId: {}", request.getUserId());
            return convertToUserInfo(user);
        } catch (Exception e) {
            logger.error("Failed to get user info: {}", e.getMessage());
            throw new RuntimeException("Failed to get user info: " + e.getMessage());
        }
    }

    /**
     * Handles requests to update user information.
     *
     * @param request the user update request
     * @return UserInfo DTO of the updated user
     */
    public UserInfo handleUpdateUserRequest(UserUpdateRequest request) {
        logger.info("Processing update user request for userId: {}", request.getUserId());

        try {
            // Get current user information
            User existingUser = userService.getUserById(request.getUserId());

            // Only update non-null fields
            if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
            if (request.getPassword() != null) existingUser.setPassword(request.getPassword());
            if (request.getAvatarUrl() != null) existingUser.setAvatarUrl(request.getAvatarUrl());
            if (request.getFirstName() != null) existingUser.setFirstName(request.getFirstName());
            if (request.getLastName() != null) existingUser.setLastName(request.getLastName());
            if (request.getPhone() != null) existingUser.setPhone(request.getPhone());
            if (request.getBirthDate() != null) existingUser.setBirthDate(request.getBirthDate());
            if (request.getCurrency() != null) existingUser.setCurrency(request.getCurrency());
            if (request.getFinancialGoal() != null) existingUser.setFinancialGoal(request.getFinancialGoal());
            if (request.getMonthlyIncome() != null) existingUser.setMonthlyIncome(request.getMonthlyIncome());
            if (request.getMainExpenseType() != null) existingUser.setMainExpenseType(request.getMainExpenseType());

            userService.updateUser(existingUser);
            logger.info("Successfully updated user info for userId: {}", request.getUserId());
            return convertToUserInfo(existingUser);
        } catch (Exception e) {
            logger.error("Failed to update user: {}", e.getMessage());
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    /**
     * Handles requests to delete a user.
     *
     * @param request the user delete request
     * @return true if the user was successfully deleted, otherwise throws an exception
     */
    public Boolean handleDeleteUserRequest(UserDeleteRequest request) {
        logger.info("Processing delete user request for userId: {}", request.getUserId());

        try {
            userService.deleteUserById(request.getUserId());
            logger.info("Successfully deleted user with userId: {}", request.getUserId());
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

    /**
     * Gets the bill types for a user.
     *
     * @param userId the user ID
     * @return a set of DynamicBillType for the user
     */
    public Set<DynamicBillType> getBillTypes(String userId) {
        logger.info("Getting bill types for userId: {}", userId);
        try {
            return userService.getBillTypes(userId);
        } catch (Exception e) {
            logger.error("Failed to get bill types: {}", e.getMessage());
            throw new RuntimeException("Failed to get bill types: " + e.getMessage());
        }
    }

    /**
     * Adds a bill type for a user.
     *
     * @param userId   the user ID
     * @param billType the bill type to add
     * @return a set of DynamicBillType after addition
     */
    public Set<DynamicBillType> addBillType(String userId, DynamicBillType billType) {
        logger.info("Adding bill type for userId: {}", userId);
        try {
            return userService.addBillType(userId, billType);
        } catch (Exception e) {
            logger.error("Failed to add bill type: {}", e.getMessage());
            throw new RuntimeException("Failed to add bill type: " + e.getMessage());
        }
    }

    /**
     * Converts a User entity to a UserInfo DTO.
     *
     * @param user the user entity
     * @return UserInfo DTO
     */
    private UserInfo convertToUserInfo(User user) {
        return new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getBirthDate(),
                user.getCurrency(),
                user.getFinancialGoal(),
                user.getMonthlyIncome(),
                user.getMainExpenseType(),
                user.getBillTypes()
        );
    }
}