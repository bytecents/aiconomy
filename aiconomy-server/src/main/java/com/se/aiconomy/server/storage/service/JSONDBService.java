package com.se.aiconomy.server.storage.service;

import com.se.aiconomy.server.storage.common.Identifiable;

import java.util.List;
import java.util.Optional;

public interface JSONDBService {
    <T extends Identifiable> boolean initializeCollection(Class<T> entityClass);

    <T extends Identifiable> T insert(T entity);

    <T extends Identifiable> T update(T entity, Class<T> entityClass);

    <T extends Identifiable> void delete(T entity, Class<T> entityClass);

    <T extends Identifiable> Optional<T> findById(String id, Class<T> entityClass);

    <T extends Identifiable> List<T> findAll(Class<T> entityClass);

    <T extends Identifiable> T upsert(T entity);

    <T extends Identifiable> boolean collectionExists(Class<T> entityClass);
}