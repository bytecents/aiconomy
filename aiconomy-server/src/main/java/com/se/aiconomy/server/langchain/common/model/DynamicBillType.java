package com.se.aiconomy.server.langchain.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DynamicBillType {
    private final String type;
    private final String displayName;
    private final boolean isSystem;

    @JsonCreator
    private DynamicBillType(@JsonProperty("type") String type,
                            @JsonProperty("displayName") String displayName,
                            @JsonProperty("isSystem") boolean isSystem) {
        this.type = type;
        this.displayName = displayName;
        this.isSystem = isSystem;
    }

    public static DynamicBillType fromBillType(BillType billType) {
        return new DynamicBillType(billType.name(), billType.getType(), true);
    }

    public static DynamicBillType createCustom(String type, String displayName) {
        return new DynamicBillType(type, displayName, false);
    }

    @JsonCreator
    public static DynamicBillType fromString(String type) {
        try {
            BillType systemType = BillType.valueOf(type.toUpperCase());
            return fromBillType(systemType);
        } catch (IllegalArgumentException e) {
            return BillTypeRegistry.getInstance().getCustomType(type);
        }
    }

    @JsonValue
    public String getType() {
        return type;
    }
}