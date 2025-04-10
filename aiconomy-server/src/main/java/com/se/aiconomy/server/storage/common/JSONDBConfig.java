package com.se.aiconomy.server.storage.common;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class JSONDBConfig {
    private static final String CONFIG_FILE = "jsondb.properties";
    private final String dbLocation;
    private final String basePackage;
    private final String cipherKey;

    private JSONDBConfig(String dbLocation, String basePackage, String cipherKey) {
        this.dbLocation = dbLocation;
        this.basePackage = basePackage;
        this.cipherKey = cipherKey;
    }

    public static JSONDBConfig load() {
        Properties props = new Properties();
        try (InputStream input = JSONDBConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                return getDefaultConfig();
            }
            props.load(input);
            return new JSONDBConfig(
                props.getProperty("jsondb.location", "src/main/resources/db"),
                props.getProperty("jsondb.base.package", "com.se.aiconomy.storage"),
                props.getProperty("jsondb.cipher.key", "1r8+24pibarAWgS85/Heeg==")
            );
        } catch (IOException e) {
            return getDefaultConfig();
        }
    }

    private static JSONDBConfig getDefaultConfig() {
        return new JSONDBConfig(
            "src/main/resources/db",
            "com.se.aiconomy.storage",
            "1r8+24pibarAWgS85/Heeg=="
        );
    }

}