package com.se.aiconomy.server.storage.service.impl;

import com.se.aiconomy.server.storage.common.Identifiable;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.common.JSONStorageConfig;
import io.jsondb.JsonDBTemplate;
import io.jsondb.crypto.Default1Cipher;
import io.jsondb.crypto.ICipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

public class JSONStorageServiceImpl implements JSONStorageService {
    private static final Logger log = LoggerFactory.getLogger(JSONStorageServiceImpl.class);
    private final JsonDBTemplate jsonDBTemplate;
    private static JSONStorageServiceImpl instance;

    private JSONStorageServiceImpl(JSONStorageConfig config) {
        try {
            ICipher cipher = new Default1Cipher(config.getCipherKey());
            this.jsonDBTemplate = new JsonDBTemplate(
                config.getStorageLocation(),
                config.getBasePackage(),
                cipher
            );
            log.info("Initialized JSONStorageService with location: {}", config.getStorageLocation());
        } catch (GeneralSecurityException e) {
            log.error("Failed to initialize JSONStorageService", e);
            throw new RuntimeException("Failed to initialize JSONStorageService", e);
        }
    }

    public static synchronized JSONStorageService getInstance() {
        if (instance == null) {
            JSONStorageConfig config = JSONStorageConfig.load();
            instance = new JSONStorageServiceImpl(config);
        }
        return instance;
    }

    @Override
    public <T extends Identifiable> boolean initializeCollection(Class<T> entityClass) {
        if (!collectionExists(entityClass)) {
            jsonDBTemplate.createCollection(entityClass);
            log.info("Created collection for {}", entityClass.getSimpleName());
            return true;
        }
        return false;
    }

    @Override
    public <T extends Identifiable> T insert(T entity) {
        jsonDBTemplate.insert(entity);
        log.info("Inserted {}", entity);
        return entity;
    }

    @Override
    public <T extends Identifiable> T update(T entity, Class<T> entityClass) {
        jsonDBTemplate.save(entity, entityClass);
        log.info("Updated {}", entity);
        return entity;
    }

    @Override
    public <T extends Identifiable> void delete(T entity, Class<T> entityClass) {
        jsonDBTemplate.remove(entity, entityClass);
        log.info("Deleted {}", entity);
    }

    @Override
    public <T extends Identifiable> Optional<T> findById(String id, Class<T> entityClass) {
        T found = jsonDBTemplate.findById(id, entityClass);
        return Optional.ofNullable(found);
    }

    @Override
    public <T extends Identifiable> List<T> findAll(Class<T> entityClass) {
        return jsonDBTemplate.findAll(entityClass);
    }

    @Override
    public <T extends Identifiable> T upsert(T entity) {
        jsonDBTemplate.upsert(entity);
        log.info("Upsert {}", entity);
        return entity;
    }

    @Override
    public <T extends Identifiable> boolean collectionExists(Class<T> entityClass) {
        return jsonDBTemplate.collectionExists(entityClass);
    }
}