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

/**
 * Unit tests for {@link SettingsService}.
 * <p>
 * This class tests the functionality of the SettingsService implementation,
 * including updating and retrieving user settings.
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SettingsServiceTest {
    /**
     * Logger instance for logging test information.
     */
    private static final Logger log = LoggerFactory.getLogger(Settings.class);

    /**
     * JSON storage service instance used for persisting settings.
     */
    private static JSONStorageService jsonStorageService;

    /**
     * Settings service instance under test.
     */
    private static SettingsService settingsService;

    /**
     * Initializes the required services before all tests.
     */
    @BeforeAll
    static void setUp() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        settingsService = new SettingsServiceImpl(jsonStorageService);
    }

    /**
     * Cleans up all settings before each test to ensure test isolation.
     */
    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(Settings.class)
                .forEach(s -> jsonStorageService.delete(s, Settings.class));
        log.info("Cleaned up all settings before test");
    }

    /**
     * Tests updating user settings.
     * <p>
     * Inserts a settings object, updates its theme, and asserts the update is successful.
     * </p>
     */
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
        Assertions.assertEquals("dark", settingsService.getSettingsByUserId("user1").getFirst().getTheme());
    }

    /**
     * Tests retrieving settings by user ID.
     * <p>
     * Inserts a settings object and asserts it can be retrieved by user ID.
     * </p>
     */
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
        Assertions.assertEquals("settings2", settingsList.getFirst().getId());
    }
}
