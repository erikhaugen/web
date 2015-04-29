package com.livescribe.framework.lsconfiguration;


import org.hibernate.cfg.DefaultNamingStrategy;

public class DefaultQuotedNamingStrategy extends DefaultNamingStrategy {

    private static final long serialVersionUID = -8680426075313478040L;

    @Override
    public String classToTableName(String className) {
        return addQuotes(super.classToTableName(className));
    }

    @Override
    public String tableName(String tableName) {
        return addQuotes(super.tableName(tableName));
    }

    @Override
    public String columnName(String columnName) {
        return addQuotes(super.columnName(columnName));
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return addQuotes(super.propertyToColumnName(propertyName));
    }

    /**
     * Adds backticks before and after the name.
     * 
     * @param input
     *            the input to quote
     * @return the quoted input
     */
    private static String addQuotes(String input) {
        return new StringBuffer().append('`').append(input).append('`')
                .toString();
    }
}
