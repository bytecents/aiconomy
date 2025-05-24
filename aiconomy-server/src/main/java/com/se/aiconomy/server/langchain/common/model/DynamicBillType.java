package com.se.aiconomy.server.langchain.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a dynamic bill type, which can be either a system-defined or a custom type.
 * This class is used to encapsulate the type, display name, and whether it is a system type.
 */
@Getter
@ToString
public class DynamicBillType {
    /**
     * The unique identifier for the bill type.
     */
    private final String type;

    /**
     * The display name for the bill type.
     */
    private final String displayName;

    /**
     * Indicates whether this bill type is a system-defined type.
     */
    private final boolean isSystem;

    /**
     * Constructs a new DynamicBillType instance.
     *
     * @param type        the unique identifier for the bill type
     * @param displayName the display name for the bill type
     * @param isSystem    whether this bill type is system-defined
     */
    @JsonCreator
    private DynamicBillType(@JsonProperty("type") String type,
                            @JsonProperty("displayName") String displayName,
                            @JsonProperty("isSystem") boolean isSystem) {
        this.type = type;
        this.displayName = displayName;
        this.isSystem = isSystem;
    }

    /**
     * Creates a DynamicBillType from a system-defined BillType.
     *
     * @param billType the system-defined BillType
     * @return a new DynamicBillType instance representing the system type
     */
    public static DynamicBillType fromBillType(BillType billType) {
        return new DynamicBillType(billType.name(), billType.getType(), true);
    }

    /**
     * Creates a custom DynamicBillType.
     *
     * @param type        the unique identifier for the custom bill type
     * @param displayName the display name for the custom bill type
     * @return a new DynamicBillType instance representing the custom type
     */
    public static DynamicBillType createCustom(String type, String displayName) {
        return new DynamicBillType(type, displayName, false);
    }

    /**
     * Creates a DynamicBillType from a string. If the string matches a system-defined BillType,
     * returns the corresponding system type; otherwise, returns a custom type from the registry.
     *
     * @param type the string representation of the bill type
     * @return a DynamicBillType instance
     */
    @JsonCreator
    public static DynamicBillType fromString(String type) {
        try {
            BillType systemType = BillType.valueOf(type.toUpperCase());
            return fromBillType(systemType);
        } catch (IllegalArgumentException e) {
            return BillTypeRegistry.getInstance().getCustomType(type);
        }
    }

    /**
     * Returns the unique identifier for the bill type.
     *
     * @return the type of the bill
     */
    @JsonValue
    public String getType() {
        return type;
    }
}