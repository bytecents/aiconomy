package com.se.aiconomy.server.storage.common;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for JSON storage.
 * <p>
 * Loads configuration properties for JSON-based storage, such as storage location,
 * base package, and cipher key. If the configuration file is not found or cannot be loaded,
 * default values are used.
 * </p>
 */
@Getter
public class JSONStorageConfig {
    /**
     * The name of the configuration file.
     */
    private static final String CONFIG_FILE = "jsonStorage.properties";
    /**
     * The location where JSON files are stored.
     */
    private final String storageLocation;
    /**
     * The base package for storage classes.
     */
    private final String basePackage;
    /**
     * The cipher key used for encryption.
     */
    private final String cipherKey;

    /**
     * Constructs a new JSONStorageConfig with the specified parameters.
     *
     * @param storageLocation the location where JSON files are stored
     * @param basePackage     the base package for storage classes
     * @param cipherKey       the cipher key used for encryption
     */
    private JSONStorageConfig(String storageLocation, String basePackage, String cipherKey) {
        this.storageLocation = storageLocation;
        this.basePackage = basePackage;
        this.cipherKey = cipherKey;
    }

    /**
     * Loads the JSON storage configuration from the properties file.
     * If the file is not found or an error occurs, default configuration is returned.
     *
     * @return the loaded JSONStorageConfig instance
     */
    public static JSONStorageConfig load() {
        Properties props = new Properties();
        try (InputStream input = JSONStorageConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                return getDefaultConfig();
            }
            props.load(input);
            return new JSONStorageConfig(
                    props.getProperty("jsonStorage.location", "src/main/resources/storage"),
                    props.getProperty("jsonStorage.base.package", "com.se.aiconomy.storage"),
                    props.getProperty("jsonStorage.cipher.key", "1r8+24pibarAWgS85/Heeg==")
            );
        } catch (IOException e) {
            return getDefaultConfig();
        }
    }

    /**
     * Returns the default JSON storage configuration.
     *
     * @return the default JSONStorageConfig instance
     */
    private static JSONStorageConfig getDefaultConfig() {
        return new JSONStorageConfig(
                "src/main/resources/storage",
                "com.se.aiconomy.storage",
                "1r8+24pibarAWgS85/Heeg=="
        );
    }
}
