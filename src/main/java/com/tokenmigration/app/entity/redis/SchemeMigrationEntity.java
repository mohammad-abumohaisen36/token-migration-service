package com.tokenmigration.app.entity.redis;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class SchemeMigrationEntity extends BaseMigrationRedisEntity {

    private String schemeTokenId;

}
