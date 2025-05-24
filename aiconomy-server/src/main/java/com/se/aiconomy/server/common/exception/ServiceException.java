package com.se.aiconomy.server.common.exception;

/**
 * Exception thrown when a service operation fails.
 * This exception is used to wrap and propagate service-level errors.
 */
public class ServiceException extends Exception {
    /**
     * Constructs a new ServiceException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
