package com.tokenmigration.app.service.impl.records;


import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.ParsingContext;

public class CustomBeanListProcessor<T> extends BeanListProcessor<T> {
    private final String migrationId;

    public CustomBeanListProcessor(Class<T> beanType, String migrationId) {
        super(beanType);
        this.migrationId = migrationId;
    }



    @Override
    public void beanProcessed(T bean, ParsingContext context) {

        if (bean instanceof CsvRecord csvRecord) {

            csvRecord.setMigrationId(migrationId);
        }
        // Call the parent class method to retain existing behavior
        super.beanProcessed(bean, context);
    }

    @Override
    public void processEnded(ParsingContext context) {
        // Custom behavior when processing ends
        System.out.println("Processing ended. Number of beans: " + getBeans().size());

        // Call the parent class method to retain existing behavior
        super.processEnded(context);
    }
}