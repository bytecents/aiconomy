package com.se.aiconomy.server.storage.service.impl;

import com.se.aiconomy.server.storage.common.Identifiable;
import com.se.aiconomy.server.storage.service.JSONDBService;
import com.se.aiconomy.server.storage.common.JSONDBConfig;
import io.jsondb.JsonDBTemplate;
import io.jsondb.crypto.Default1Cipher;
import io.jsondb.crypto.ICipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

public class JSONDBServiceImpl implements JSONDBService {
    private static final Logger log = LoggerFactory.getLogger(JSONDBServiceImpl.class);
    private final JsonDBTemplate jsonDBTemplate;
    private static JSONDBServiceImpl instance;

    private JSONDBServiceImpl(JSONDBConfig config) {
        try {
            ICipher cipher = new Default1Cipher(config.getCipherKey());
            this.jsonDBTemplate = new JsonDBTemplate(
                config.getDbLocation(),
                config.getBasePackage(),
                cipher
            );
            log.info("Initialized JSONDBService with location: {}", config.getDbLocation());
        } catch (GeneralSecurityException e) {
            log.error("Failed to initialize JSONDBService", e);
            throw new RuntimeException("Failed to initialize JSONDBService", e);
        }
    }

    public static synchronized JSONDBService getInstance() {
        if (instance == null) {
            JSONDBConfig config = JSONDBConfig.load();
            instance = new JSONDBServiceImpl(config);
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