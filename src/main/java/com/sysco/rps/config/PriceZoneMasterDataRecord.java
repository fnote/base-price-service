package com.sysco.rps.config;

import java.time.LocalDateTime;

/**
 * Bean for holding Price Zone Master Data Record
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/22. Thu 14:00
 */
public class PriceZoneMasterDataRecord {

    private final String tableType;
    private final String tableName;
    private final LocalDateTime effectiveDate;

    public PriceZoneMasterDataRecord(String tableType, String tableName, LocalDateTime effectiveDate) {
        this.tableType = tableType;
        this.tableName = tableName;
        this.effectiveDate = effectiveDate;
    }

    public String getTableType() {
        return tableType;
    }

    public String getTableName() {
        return tableName;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }
}
