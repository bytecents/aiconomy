package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.model.dto.Settings.request.SettingsUpdateRequest;
import com.se.aiconomy.server.model.dto.Settings.response.SettingsInfo;
import com.se.aiconomy.server.model.dto.user.request.UserUpdateRequest;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import com.se.aiconomy.server.model.entity.Settings;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.SettingsService;
import com.se.aiconomy.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(SettingsRequestHandler.class);
    private final UserService userService;
    private final SettingsService settingsService;

    public SettingsRequestHandler(UserService userService, SettingsService settingsService) {
        this.userService = userService;
        this.settingsService = settingsService;
    }

    public SettingsInfo handleUpdateSettingsRequest(SettingsUpdateRequest request) {
        logger.info("handleUpdateSettingsRequest: {}", request);
        try {
            User user = userService.getUserById(request.getUserId());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setBirthDate(request.getBirthDate());
            UserRequestHandler userRequestHandler = new UserRequestHandler(userService);
            UserInfo userInfo = userRequestHandler.handleUpdateUserRequest(convertToUserUpdateRequest(user));

            Settings settings = new Settings(
                    request.getSettingsId(),
                    request.getUserId(),
                    request.getCurrency(),
                    request.getDateFormat(),
                    request.getLanguage(),
                    request.getTheme(),
                    request.getNotification(),
                    request.getAIFunctionality()
            );
            settingsService.updateSettings(settings);
        } catch (Exception e) {
            logger.error("handleUpdateSettingsRequest error: {}", e.getMessage());
            throw new RuntimeException("handleUpdateSettingsRequest error: " + e.getMessage());
        }
        return new SettingsInfo(
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getBirthDate(),
                request.getSettingsId(),
                request.getUserId(),
                request.getCurrency(),
                request.getDateFormat(),
                request.getLanguage(),
                request.getTheme(),
                request.getNotification(),
                request.getAIFunctionality()
        );
    }

    private UserUpdateRequest convertToUserUpdateRequest(User user) {
        return new UserUpdateRequest(
                user.getId(),
                user.getPassword(),
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
