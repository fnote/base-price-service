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
    private LocalDateTime activeTableEffectiveDate;

    public PriceZoneTableConfig(String businessUnitNumber, String activeTable, String historyTable,
                                LocalDateTime activeTableEffectiveDate) {
        this.businessUnitNumber = businessUnitNumber;
        this.activeTable = activeTable;
        this.historyTable = historyTable;
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

    public LocalDateTime getActiveTableEffectiveDate() {
        return activeTableEffectiveDate;
    }

    public void setActiveTableEffectiveDate(LocalDateTime activeTableEffectiveDate) {
        this.activeTableEffectiveDate = activeTableEffectiveDate;
    }
}
