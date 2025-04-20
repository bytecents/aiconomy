package com.se.aiconomy.server.langchain.common.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BillTypeRegistry {
    private static final BillTypeRegistry INSTANCE = new BillTypeRegistry();
    private final Map<String, DynamicBillType> customTypes = new ConcurrentHashMap<>();

    private BillTypeRegistry() {
        Arrays.stream(BillType.values())
            .forEach(type -> customTypes.put(
                type.name(),
                DynamicBillType.fromBillType(type)
            ));
    }

    public static BillTypeRegistry getInstance() {
        return INSTANCE;
    }

    public DynamicBillType registerCustomType(String type, String displayName) {
        String normalizedType = type.toUpperCase();
        DynamicBillType billType = DynamicBillType.createCustom(normalizedType, displayName);
        customTypes.put(normalizedType, billType);
        return billType;
    }

    public DynamicBillType getCustomType(String type) {
        String normalizedType = type.toUpperCase();
        DynamicBillType billType = customTypes.get(normalizedType);
        if (billType == null) {
            return DynamicBillType.fromBillType(BillType.OTHER);
        }
        return billType;
    }

    public Set<DynamicBillType> getAllTypes() {
        return Set.copyOf(customTypes.values());
    }

    public Set<String> getAllTypeNames() {
        return customTypes.values().stream()
            .map(DynamicBillType::getType)
            .collect(Collectors.toSet());
    }

    public void removeCustomType(String type) {
        String normalizedType = type.toUpperCase();
        if (!isSystemType(normalizedType)) {
            customTypes.remove(normalizedType);
        }
    }

    private boolean isSystemType(String type) {
        try {
            BillType.valueOf(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}