package com.se.aiconomy.server.storage.common;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class JSONStorageConfig {
    private static final String CONFIG_FILE = "jsonStorage.properties";
    private final String storageLocation;
    private final String basePackage;
    private final String cipherKey;

    private JSONStorageConfig(String storageLocation, String basePackage, String cipherKey) {
        this.storageLocation = storageLocation;
        this.basePackage = basePackage;
        this.cipherKey = cipherKey;
    }

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

    private static JSONStorageConfig getDefaultConfig() {
        return new JSONStorageConfig(
                "src/main/resources/storage",
                "com.se.aiconomy.storage",
                "1r8+24pibarAWgS85/Heeg=="
        );
    }

}