package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.*;

@Getter
@Setter
@ToString
@Document(collection = "Settings", schemaVersion = "1.0")
@NoArgsConstructor
@AllArgsConstructor
public class Settings implements Identifiable {
    @Id
    private String id;
    private String userId;
    private String currency;
    private String dateFormat;
    private String language;
    private String theme;
    private String notification;
    private String AIFunctionality;

}
