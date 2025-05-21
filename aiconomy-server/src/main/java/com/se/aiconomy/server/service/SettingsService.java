package com.se.aiconomy.server.service;

import com.se.aiconomy.server.model.entity.Settings;

import java.util.List;

public interface SettingsService {
    void updateSettings(Settings settings);

    List<Settings> getSettingsByUserId(String userId);
}
