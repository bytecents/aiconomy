package com.se.aiconomy.server.storage.common;

/**
 * Identifiable interface provides methods to get and set the unique identifier for an object.
 */
public interface Identifiable {
    /**
     * Gets the unique identifier of the object.
     *
     * @return the unique identifier as a String
     */
    String getId();

    /**
     * Sets the unique identifier of the object.
     *
     * @param id the unique identifier to set
     */
    void setId(String id);
}
