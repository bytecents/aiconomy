package com.se.aiconomy.server;

import com.se.aiconomy.server.model.entity.Settings;
import com.se.aiconomy.server.service.SettingsService;
import com.se.aiconomy.server.service.impl.SettingsServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SettingsServiceTest {
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    private static JSONStorageService jsonStorageService;
    private static SettingsService settingsService;

    @BeforeAll
    static void setUp() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        settingsService = new SettingsServiceImpl(jsonStorageService);
    }

    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(Settings.class)
                .forEach(s -> jsonStorageService.delete(s, Settings.class));
        log.info("Cleaned up all settings before test");
    }

    @Test
    @Order(1)
    void testUpdateSettings() {
        Settings settings = new Settings(
                "settings1",
                "user1",
                "YYYY-MM-DD",
                "en",
                "light",
                "on",
                "on");
        jsonStorageService.insert(settings);
        settings.setTheme("dark");
        settingsService.updateSettings(settings);
        Assertions.assertEquals("dark", settingsService.getSettingsByUserId("user1").get(0).getTheme());
    }

    @Test
    @Order(2)
    void testGetSettingsByUserId() {
        Settings settings = new Settings(
                "settings2",
                "user2",
                "YYYY-MM-DD",
                "en",
                "light",
                "on",
                "on");
        jsonStorageService.insert(settings);
        List<Settings> settingsList = settingsService.getSettingsByUserId("user2");
        Assertions.assertEquals(1, settingsList.size());
        Assertions.assertEquals("settings2", settingsList.get(0).getId());
    }

}
