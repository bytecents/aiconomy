package com.se.aiconomy.server.storage.service.impl;

import com.se.aiconomy.server.storage.common.Identifiable;
import com.se.aiconomy.server.storage.common.JSONStorageConfig;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import io.jsondb.JsonDBTemplate;
import io.jsondb.crypto.Default1Cipher;
import io.jsondb.crypto.ICipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the JSONStorageService interface using JsonDBTemplate for JSON-based storage.
 * <p>
 * This class provides CRUD operations for entities implementing the Identifiable interface,
 * utilizing a singleton pattern for service instantiation. It supports collection initialization,
 * insertion, update, deletion, upsert, and retrieval operations, with optional encryption support.
 * </p>
 */
public class JSONStorageServiceImpl implements JSONStorageService {
    /**
     * Logger instance for logging events and errors.
     */
    private static final Logger log = LoggerFactory.getLogger(JSONStorageServiceImpl.class);
    /**
     * Singleton instance of JSONStorageServiceImpl.
     */
    private static JSONStorageServiceImpl instance;
    /**
     * The JsonDBTemplate instance used for database operations.
     */
    private final JsonDBTemplate jsonDBTemplate;

    /**
     * Private constructor to initialize the storage service with the given configuration.
     *
     * @param config the JSON storage configuration
     */
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

    /**
     * Returns the singleton instance of JSONStorageService.
     * If the instance does not exist, it is created with the loaded configuration.
     *
     * @return the singleton JSONStorageService instance
     */
    public static synchronized JSONStorageService getInstance() {
        if (instance == null) {
            JSONStorageConfig config = JSONStorageConfig.load();
            instance = new JSONStorageServiceImpl(config);
        }
        return instance;
    }

    /**
     * Initializes a collection for the specified entity class if it does not already exist.
     *
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return true if the collection was created, false if it already existed
     */
    @Override
    public <T extends Identifiable> boolean initializeCollection(Class<T> entityClass) {
        if (!collectionExists(entityClass)) {
            jsonDBTemplate.createCollection(entityClass);
            log.info("Created collection for {}", entityClass.getSimpleName());
            return true;
        }
        return false;
    }

    /**
     * Inserts a new entity into the storage. If the entity does not have an ID, a new UUID is generated.
     *
     * @param entity the entity to insert
     * @param <T>    the type of the entity
     * @return the inserted entity with an assigned ID
     */
    @Override
    public <T extends Identifiable> T insert(T entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jsonDBTemplate.insert(entity);
        log.info("Inserted {}", entity);
        return entity;
    }

    /**
     * Updates an existing entity in the storage.
     *
     * @param entity      the entity to update
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return the updated entity
     */
    @Override
    public <T extends Identifiable> T update(T entity, Class<T> entityClass) {
        jsonDBTemplate.save(entity, entityClass);
        log.info("Updated {}", entity);
        return entity;
    }

    /**
     * Deletes an entity from the storage.
     *
     * @param entity      the entity to delete
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     */
    @Override
    public <T extends Identifiable> void delete(T entity, Class<T> entityClass) {
        jsonDBTemplate.remove(entity, entityClass);
        log.info("Deleted {}", entity);
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id          the ID of the entity
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return an Optional containing the found entity, or empty if not found
     */
    @Override
    public <T extends Identifiable> Optional<T> findById(String id, Class<T> entityClass) {
        T found = jsonDBTemplate.findById(id, entityClass);
        return Optional.ofNullable(found);
    }

    /**
     * Retrieves all entities of the specified class from the storage.
     *
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return a list of all entities of the specified class
     */
    @Override
    public <T extends Identifiable> List<T> findAll(Class<T> entityClass) {
        return jsonDBTemplate.findAll(entityClass);
    }

    /**
     * Inserts or updates an entity in the storage. If the entity does not have an ID, a new UUID is generated.
     *
     * @param entity the entity to upsert
     * @param <T>    the type of the entity
     * @return the upserted entity with an assigned ID
     */
    @Override
    public <T extends Identifiable> T upsert(T entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jsonDBTemplate.upsert(entity);
        log.info("Upsert {}", entity);
        return entity;
    }

    /**
     * Checks if a collection exists for the specified entity class.
     *
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return true if the collection exists, false otherwise
     */
    @Override
    public <T extends Identifiable> boolean collectionExists(Class<T> entityClass) {
        return jsonDBTemplate.collectionExists(entityClass);
    }
}
