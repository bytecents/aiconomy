package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.model.dto.Settings.request.SettingsUpdateRequest;
import com.se.aiconomy.server.model.dto.Settings.response.SettingsInfo;
import com.se.aiconomy.server.model.dto.Settings.response.SettingsUserInfo;
import com.se.aiconomy.server.model.dto.user.request.UserUpdateRequest;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import com.se.aiconomy.server.model.entity.Settings;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.SettingsService;
import com.se.aiconomy.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handler for processing user settings related requests.
 * Provides methods to get and update user settings and user information.
 */
public class SettingsRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(SettingsRequestHandler.class);
    private final UserService userService;
    private final SettingsService settingsService;

    /**
     * Constructs a SettingsRequestHandler with the specified user and settings services.
     *
     * @param userService     the user service
     * @param settingsService the settings service
     */
    public SettingsRequestHandler(UserService userService, SettingsService settingsService) {
        this.userService = userService;
        this.settingsService = settingsService;
    }

    /**
     * Handles the request to get user information for settings.
     *
     * @param userId the user ID
     * @return SettingsUserInfo containing user information for settings
     */
    public SettingsUserInfo handleGetSettingsUserInfoRequest(String userId) {
        logger.info("Processing get settings user info request for userId: {}", userId);
        try {
            User user = userService.getUserById(userId);
            return new SettingsUserInfo(user.getAvatarUrl(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhone(),
                    user.getBirthDate(),
                    user.getCurrency());
        } catch (Exception e) {
            logger.error("Failed to get settings user info for userId: {}", e.getMessage());
            throw new RuntimeException("Failed to get settings user info for userId: " + e.getMessage());
        }
    }

    /**
     * Handles the request to update user information.
     * Only non-null fields in the request will be updated.
     *
     * @param request the user update request
     * @return UserInfo containing the updated user information
     */
    public UserInfo handleUpdateUserRequest(UserUpdateRequest request) {
        logger.info("Processing update user request for userId: {}", request.getUserId());

        try {
            // Get current user information
            User existingUser = userService.getUserById(request.getUserId());

            // Update only non-null fields
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
     * Handles the request to update user settings.
     *
     * @param request the settings update request
     * @return SettingsInfo containing the updated settings information
     */
    public SettingsInfo handleUpdateSettingsRequest(SettingsUpdateRequest request) {
        logger.info("handleUpdateSettingsRequest: {}", request);
        try {
            Settings settings = new Settings();
            settings.setUserId(request.getUserId());
            settings.setId(request.getSettingsId());
            settings.setTheme(request.getTheme());
            settings.setNotification(request.getNotification());
            settings.setLanguage(request.getLanguage());
            settings.setDateFormat(request.getDateFormat());
            settings.setAIFunctionality(request.getAIFunctionality());
            return new SettingsInfo(settings.getId(),
                    settings.getUserId(),
                    settings.getDateFormat(),
                    settings.getLanguage(),
                    settings.getTheme(),
                    settings.getNotification(),
                    settings.getAIFunctionality()
            );
        } catch (Exception e) {
            logger.error("handleUpdateSettingsRequest error: {}", e.getMessage());
            throw new RuntimeException("handleUpdateSettingsRequest error: " + e.getMessage());
        }
    }

    /**
     * Handles the request to get all settings for a user by user ID.
     *
     * @param userId the user ID
     * @return a list of SettingsInfo for the user
     */
    public List<SettingsInfo> handleGetSettingsByUserIdRequest(String userId) {
        logger.info("handleGetSettingsByUserIdRequest: {}", userId);
        try {
            List<Settings> settingsList = settingsService.getSettingsByUserId(userId);
            return settingsList.stream().map(s -> new SettingsInfo(s.getId(),
                    s.getUserId(),
                    s.getDateFormat(),
                    s.getLanguage(),
                    s.getTheme(),
                    s.getNotification(),
                    s.getAIFunctionality())).toList();
        } catch (Exception e) {
            logger.error("handleGetSettingsByUserIdRequest error: {}", e.getMessage());
            throw new RuntimeException("handleGetSettingsByUserIdRequest error: " + e.getMessage());
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