package com.tokenmigration.app.file.parser;

import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import lombok.Builder;

import java.util.Objects;

@Builder
public record TsvParserBuilder(
        boolean headerExtractionEnabled,
        boolean ignoreLeadingWhitespaces,
        boolean ignoreTrailingWhitespaces,
        boolean skipEmptyLines,
        RowProcessor processor
) {

    public TsvParserBuilder {
        Objects.requireNonNull(processor, "RowProcessor must not be null");
    }

    public TsvParser build() {
        Objects.requireNonNull(processor, "RowProcessor must not be null");
        TsvParserSettings settings = new TsvParserSettings();
        settings.setHeaderExtractionEnabled(headerExtractionEnabled);
        settings.setIgnoreLeadingWhitespaces(ignoreLeadingWhitespaces);
        settings.setIgnoreTrailingWhitespaces(ignoreTrailingWhitespaces);
        settings.setSkipEmptyLines(skipEmptyLines);
        settings.setProcessor(processor);

        return new TsvParser(settings);
    }


}