package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.model.entity.Settings;
import com.se.aiconomy.server.service.SettingsService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.util.List;

/**
 * Implementation of the SettingsService interface for managing user settings.
 */
public class SettingsServiceImpl implements SettingsService {
    private final JSONStorageService jsonStorageService;

    /**
     * Constructs a new SettingsServiceImpl with the specified JSONStorageService.
     *
     * @param jsonStorageService the JSON storage service to be used
     */
    public SettingsServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeSettingsCollection();
    }

    /**
     * Initializes the settings collection if it does not already exist.
     */
    public void initializeSettingsCollection() {
        if (!jsonStorageService.collectionExists(Settings.class)) {
            jsonStorageService.initializeCollection(Settings.class);
        }
    }

    /**
     * Updates the specified settings in the storage.
     *
     * @param settings the settings to update
     */
    @Override
    public void updateSettings(Settings settings) {
        jsonStorageService.update(settings, Settings.class);
    }

    /**
     * Retrieves all settings associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of settings belonging to the user
     */
    @Override
    public List<Settings> getSettingsByUserId(String userId) {
        List<Settings> settings = jsonStorageService.findAll(Settings.class);
        return settings.stream().filter(setting -> setting.getUserId().equals(userId)).toList();
    }
}
