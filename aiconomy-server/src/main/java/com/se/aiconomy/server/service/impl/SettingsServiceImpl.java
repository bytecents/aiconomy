package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.model.entity.Settings;
import com.se.aiconomy.server.service.SettingsService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.util.List;

public class SettingsServiceImpl implements SettingsService {
    private final JSONStorageService jsonStorageService;

    public SettingsServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeSettingsCollection();
    }

    public void initializeSettingsCollection() {
        if (!jsonStorageService.collectionExists(Settings.class)) {
            jsonStorageService.initializeCollection(Settings.class);
        }
    }

    @Override
    public void updateSettings(Settings settings) {
        jsonStorageService.update(settings, Settings.class);
    }

    @Override
    public List<Settings> getSettingsByUserId(String userId) {
        List<Settings> settings = jsonStorageService.findAll(Settings.class);
        return settings.stream().filter(setting -> setting.getUserId().equals(userId)).toList();
    }
}
