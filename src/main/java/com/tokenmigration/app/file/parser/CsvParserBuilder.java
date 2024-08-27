package com.tokenmigration.app.file.parser;

import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.Builder;

import java.util.Objects;

@Builder
public record CsvParserBuilder(
        boolean headerExtractionEnabled,
        boolean ignoreLeadingWhitespaces,
        boolean ignoreTrailingWhitespaces,
        boolean skipEmptyLines,
        RowProcessor processor
) {

    public CsvParserBuilder {
        Objects.requireNonNull(processor, "RowProcessor must not be null");
    }

    public CsvParser build() {
        Objects.requireNonNull(processor, "RowProcessor must not be null");
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(headerExtractionEnabled);
        settings.setIgnoreLeadingWhitespaces(ignoreLeadingWhitespaces);
        settings.setIgnoreTrailingWhitespaces(ignoreTrailingWhitespaces);
        settings.setSkipEmptyLines(skipEmptyLines);
        settings.setProcessor(processor);
        return new CsvParser(settings);

    }
}