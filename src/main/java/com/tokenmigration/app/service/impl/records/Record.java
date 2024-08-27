package com.tokenmigration.app.service.impl.records;


import com.univocity.parsers.annotations.Parsed;
import lombok.Data;


@Data
public abstract class Record {

    @Parsed(field = "row")
    private int row;
    private String migrationId;

}