package com.tokenmigration.app.service.impl;

import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CsvRecord {

    @Parsed(field = "id")
    private int id;
    @Parsed(field = "code")
    private String code;
    @Parsed(field = "uuid")
    private String uuid;
    @Parsed(field = "cardNumber")
    private String cardNumber;
    @Parsed(field = "name")
    private String name;
    @Parsed(field = "expirationDate")
    private String expirationDate;


}