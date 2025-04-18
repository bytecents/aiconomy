package com.se.aiconomy.server.dao;

import com.se.aiconomy.server.storage.common.Identifiable;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Abstract Data Access Object class that provides standard CRUD operations
 * for entities that implement the Identifiable interface.
 *
 * @param <T> The type of entity this DAO handles, must implement Identifiable
 */
public abstract class AbstractDao<T extends Identifiable> {
    private static final Logger log = LoggerFactory.getLogger(AbstractDao.class);
    protected final JSONStorageService storageService;
    protected final Class<T> entityClass;

    /**
     * Constructor that initializes the DAO with the entity class type.
     *
     * @param entityClass The class type of the entity this DAO manages
     */
    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.storageService = JSONStorageServiceImpl.getInstance();
        initializeCollection();
    }

    /**
     * Initializes the collection in the storage if it doesn't exist.
     */
    protected void initializeCollection() {
        if (storageService.initializeCollection(entityClass)) {
            log.info("Initialized collection for entity: {}", entityClass.getSimpleName());
        }
    }

    /**
     * Creates a new entity in the storage.
     *
     * @param entity The entity to create
     * @return The created entity with generated ID
     */
    public T create(T entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        log.debug("Creating new entity: {}", entity);
        return storageService.insert(entity);
    }

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve
     * @return Optional containing the entity if found, empty otherwise
     */
    public Optional<T> findById(String id) {
        log.debug("Finding entity by ID: {}", id);
        return storageService.findById(id, entityClass);
    }

    /**
     * Retrieves all entities of this type from the storage.
     *
     * @return List of all entities
     */
    public List<T> findAll() {
        log.debug("Finding all entities of type: {}", entityClass.getSimpleName());
        return storageService.findAll(entityClass);
    }

    /**
     * Updates an existing entity in the storage.
     *
     * @param entity The entity to update
     * @return The updated entity
     */
    public T update(T entity) {
        log.debug("Updating entity: {}", entity);
        return storageService.update(entity, entityClass);
    }

    /**
     * Creates or updates an entity in the storage.
     *
     * @param entity The entity to upsert
     * @return The created or updated entity
     */
    public T upsert(T entity) {
        log.debug("Upserting entity: {}", entity);
        return storageService.upsert(entity);
    }

    /**
     * Deletes an entity from the storage.
     *
     * @param entity The entity to delete
     */
    public void delete(T entity) {
        log.debug("Deleting entity: {}", entity);
        storageService.delete(entity, entityClass);
    }

    /**
     * Checks if the collection for this entity type exists in the storage.
     *
     * @return true if the collection exists, false otherwise
     */
    public boolean collectionExists() {
        return storageService.collectionExists(entityClass);
    }
}