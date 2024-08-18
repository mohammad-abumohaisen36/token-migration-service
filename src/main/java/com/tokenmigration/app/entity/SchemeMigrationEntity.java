package com.tokenmigration.app.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "token-migration")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SchemeMigrationEntity {

    private String schemeTokenId;
    @Field("fileGenerated")
    private boolean fileGenerated;
}
