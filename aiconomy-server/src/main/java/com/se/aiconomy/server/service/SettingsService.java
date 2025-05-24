package com.se.aiconomy.server.service;

import com.se.aiconomy.server.model.entity.Settings;

import java.util.List;

/**
 * SettingsService interface provides methods for managing user settings.
 */
public interface SettingsService {
    /**
     * Updates the settings for a user.
     *
     * @param settings the settings to update
     */
    void updateSettings(Settings settings);

    /**
     * Retrieves all settings associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of settings belonging to the user
     */
    List<Settings> getSettingsByUserId(String userId);
}
