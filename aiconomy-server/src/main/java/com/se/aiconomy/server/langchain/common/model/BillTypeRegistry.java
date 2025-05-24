package com.se.aiconomy.server.langchain.common.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for managing both system-defined and custom bill types.
 * <p>
 * This class provides methods to register, retrieve, list, and remove custom bill types,
 * as well as access all available bill types (including system-defined ones).
 * It uses a thread-safe map to store the types and follows the singleton pattern.
 * </p>
 */
public class BillTypeRegistry {
    /**
     * Singleton instance of the BillTypeRegistry.
     */
    private static final BillTypeRegistry INSTANCE = new BillTypeRegistry();

    /**
     * Thread-safe map storing all bill types, keyed by their normalized type name.
     */
    private final Map<String, DynamicBillType> customTypes = new ConcurrentHashMap<>();

    /**
     * Private constructor that initializes the registry with all system-defined bill types.
     */
    private BillTypeRegistry() {
        Arrays.stream(BillType.values())
                .forEach(type -> customTypes.put(
                        type.name(),
                        DynamicBillType.fromBillType(type)
                ));
    }

    /**
     * Returns the singleton instance of the BillTypeRegistry.
     *
     * @return the singleton instance
     */
    public static BillTypeRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a new custom bill type with the specified type and display name.
     * The type name is normalized to uppercase.
     *
     * @param type        the unique type identifier for the custom bill type
     * @param displayName the display name for the custom bill type
     * @return the created DynamicBillType instance
     */
    public DynamicBillType registerCustomType(String type, String displayName) {
        String normalizedType = type.toUpperCase();
        DynamicBillType billType = DynamicBillType.createCustom(normalizedType, displayName);
        customTypes.put(normalizedType, billType);
        return billType;
    }

    /**
     * Retrieves a bill type (system or custom) by its type name.
     * If the type does not exist, returns the system-defined "OTHER" bill type.
     *
     * @param type the type name to look up
     * @return the corresponding DynamicBillType, or "OTHER" if not found
     */
    public DynamicBillType getCustomType(String type) {
        String normalizedType = type.toUpperCase();
        DynamicBillType billType = customTypes.get(normalizedType);
        if (billType == null) {
            return DynamicBillType.fromBillType(BillType.OTHER);
        }
        return billType;
    }

    /**
     * Returns a set of all registered bill types (system and custom).
     *
     * @return a set of all DynamicBillType instances
     */
    public Set<DynamicBillType> getAllTypes() {
        return Set.copyOf(customTypes.values());
    }

    /**
     * Returns a set of all registered bill type names (system and custom).
     *
     * @return a set of all type names as strings
     */
    public Set<String> getAllTypeNames() {
        return customTypes.values().stream()
                .map(DynamicBillType::getType)
                .collect(Collectors.toSet());
    }

    /**
     * Removes a custom bill type by its type name.
     * System-defined types cannot be removed.
     *
     * @param type the type name to remove
     */
    public void removeCustomType(String type) {
        String normalizedType = type.toUpperCase();
        if (!isSystemType(normalizedType)) {
            customTypes.remove(normalizedType);
        }
    }

    /**
     * Checks if the given type name corresponds to a system-defined bill type.
     *
     * @param type the type name to check
     * @return true if it is a system-defined type, false otherwise
     */
    private boolean isSystemType(String type) {
        try {
            BillType.valueOf(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}