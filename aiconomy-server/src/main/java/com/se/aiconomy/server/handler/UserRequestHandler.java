package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.model.dto.user.request.*;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户请求处理器
 * 负责处理所有与用户相关的请求
 */
public class UserRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);
    private final UserService userService;

    public UserRequestHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * 处理用户注册请求
     */
    public UserInfo handleRegisterRequest(UserRegisterRequest request) {
        logger.info("Processing register request for email: {}", request.getEmail());

        // 创建新用户实体
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

        // 注册用户
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
     * 处理用户登录请求
     */
    public UserInfo handleLoginRequest(UserLoginRequest request) {
        logger.info("Processing login request for email: {}", request.getEmail());

        User user;
        try {
            System.out.println(request.getEmail());
            System.out.println(request.getPassword());
            user = userService.login(request.getEmail(), request.getPassword());
            logger.info("User successfully logged in: {}", request.getEmail());
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
        return convertToUserInfo(user);
    }

    /**
     * 处理获取用户信息请求
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
     * 处理更新用户信息请求
     */
    public UserInfo handleUpdateUserRequest(UserUpdateRequest request) {
        logger.info("Processing update user request for userId: {}", request.getUserId());

        try {
            // 获取当前用户信息
            User existingUser = userService.getUserById(request.getUserId());

            // 只更新非null的字段
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
     * 处理删除用户请求
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
     * 将User实体转换为UserInfo DTO
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
            user.getMainExpenseType()
        );
    }
}