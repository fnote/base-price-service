package com.sysco.rps.config;

import java.time.LocalDateTime;

/**
 * Bean for holding Price Zone Table Configuration
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/22. Thu 14:00
 */
public class PriceZoneTableConfig {

    private String businessUnitNumber;
    private String activeTable;
    private String historyTable;
    private String futureTable;
    private LocalDateTime activeTableEffectiveDate;

    public PriceZoneTableConfig(String businessUnitNumber, String activeTable, String historyTable,
                                String futureTable, LocalDateTime activeTableEffectiveDate) {
        this.businessUnitNumber = businessUnitNumber;
        this.activeTable = activeTable;
        this.historyTable = historyTable;
        this.futureTable = futureTable;
        this.activeTableEffectiveDate = activeTableEffectiveDate;
    }

    public String getBusinessUnitNumber() {
        return businessUnitNumber;
    }

    public void setBusinessUnitNumber(String businessUnitNumber) {
        this.businessUnitNumber = businessUnitNumber;
    }

    public String getActiveTable() {
        return activeTable;
    }

    public void setActiveTable(String activeTable) {
        this.activeTable = activeTable;
    }

    public String getHistoryTable() {
        return historyTable;
    }

    public void setHistoryTable(String historyTable) {
        this.historyTable = historyTable;
    }

    public String getFutureTable() { return futureTable; }

    public void setFutureTable(String futureTable) { this.futureTable = futureTable; }

    public LocalDateTime getActiveTableEffectiveDate() {
        return activeTableEffectiveDate;
    }

    public void setActiveTableEffectiveDate(LocalDateTime activeTableEffectiveDate) {
        this.activeTableEffectiveDate = activeTableEffectiveDate;
    }
}
