package com.tokenmigration.app.mapper;

import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.entity.mongo.MigrationOperationsEntity;
import com.tokenmigration.app.model.response.MigrationOperationResponse;
import com.tokenmigration.app.model.reuest.MigrationRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperationMigrationMapper {
    OperationMigrationMapper INSTANCE = Mappers.getMapper(OperationMigrationMapper.class);

    OperationMigrationDto mapToDto(MigrationOperationsEntity entity);

    OperationMigrationDto mapToDto(MigrationRequest entity);

    MigrationOperationsEntity mapToEntity(OperationMigrationDto dto);

    MigrationOperationResponse mapToResponse(MigrationOperationsEntity dto);

    List<OperationMigrationDto> mapToDto(List<MigrationOperationsEntity> entity);

    List<MigrationOperationsEntity> mapToEntity(List<OperationMigrationDto> dto);

}
