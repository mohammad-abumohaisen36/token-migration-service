package com.tokenmigration.app.entity.redis;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class TMMigrationEntity extends BaseMigrationRedisEntity {

    private String tmTokenId;


}
