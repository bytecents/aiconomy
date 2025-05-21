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
    private static final Logger log = LoggerFactory.getLogger(SettingsServiceTest.class);
    private static JSONStorageService jsonStorageService;
    private static SettingsService settingsService;

    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        settingsService = new SettingsServiceImpl(jsonStorageService);
    }

    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(Settings.class)
                .forEach(s -> jsonStorageService.delete(s, Settings.class));
        log.info("Clean up done.");
    }

    @Test
    @Order(1)
    void testUpdateSettings() {
        Settings settings = new Settings();
        settings.setId("settings1");
        settings.setUserId("user1");
        settings.setLanguage("en");
        settings.setTheme("light");
        settings.setAIFunctionality("yes");
        settings.setAIFunctionality("yes");
        jsonStorageService.insert(settings);
        settings.setTheme("dark");
        settingsService.updateSettings(settings);
        Assertions.assertTrue(jsonStorageService.findAll(Settings.class)
                .stream()
                .anyMatch(s -> s.getId().equals("settings1") && s.getTheme().equals("dark")));
        log.info("Update settings test passed.");
    }

    @Test
    @Order(2)
    void testGetSettingsByUserId() {
        Settings settings = new Settings();
        settings.setId("settings2");
        settings.setUserId("user2");
        settings.setLanguage("en");
        settings.setTheme("light");
        settings.setAIFunctionality("yes");
        settings.setAIFunctionality("yes");
        jsonStorageService.insert(settings);
        List<Settings> result = settingsService.getSettingsByUserId("user2");
        Assertions.assertTrue(result.stream()
                .anyMatch(s -> s.getId().equals("settings2")));
        log.info("Get settings by user id test passed.");
    }
}
