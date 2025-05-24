package com.se.aiconomy.server.storage.service;

import com.se.aiconomy.server.storage.common.Identifiable;

import java.util.List;
import java.util.Optional;

/**
 * Interface for JSON-based storage service.
 * <p>
 * Provides CRUD operations and collection management for entities implementing the {@link Identifiable} interface.
 * </p>
 */
public interface JSONStorageService {
    /**
     * Initializes a collection for the specified entity class if it does not already exist.
     *
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return true if the collection was created, false if it already existed
     */
    <T extends Identifiable> boolean initializeCollection(Class<T> entityClass);

    /**
     * Inserts a new entity into the storage. If the entity does not have an ID, a new UUID is generated.
     *
     * @param entity the entity to insert
     * @param <T>    the type of the entity
     * @return the inserted entity with an assigned ID
     */
    <T extends Identifiable> T insert(T entity);

    /**
     * Updates an existing entity in the storage.
     *
     * @param entity      the entity to update
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return the updated entity
     */
    <T extends Identifiable> T update(T entity, Class<T> entityClass);

    /**
     * Deletes an entity from the storage.
     *
     * @param entity      the entity to delete
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     */
    <T extends Identifiable> void delete(T entity, Class<T> entityClass);

    /**
     * Finds an entity by its ID.
     *
     * @param id          the ID of the entity
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return an {@link Optional} containing the found entity, or empty if not found
     */
    <T extends Identifiable> Optional<T> findById(String id, Class<T> entityClass);

    /**
     * Retrieves all entities of the specified class from the storage.
     *
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return a list of all entities of the specified class
     */
    <T extends Identifiable> List<T> findAll(Class<T> entityClass);

    /**
     * Inserts or updates an entity in the storage. If the entity does not have an ID, a new UUID is generated.
     *
     * @param entity the entity to upsert
     * @param <T>    the type of the entity
     * @return the upserted entity with an assigned ID
     */
    <T extends Identifiable> T upsert(T entity);

    /**
     * Checks if a collection exists for the specified entity class.
     *
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return true if the collection exists, false otherwise
     */
    <T extends Identifiable> boolean collectionExists(Class<T> entityClass);
}
