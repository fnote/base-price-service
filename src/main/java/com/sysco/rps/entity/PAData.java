package com.sysco.rps.entity;

/**
 * Entity bean for Price Advisor Data
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 20. Jul 2020 12:28
 */
public class PAData {

    private String supc;
    private Integer priceZone;
    private Double price;
    private String effectiveDate;
    private long exportedDate;
    private char splitIndicator;

    public PAData(String supc, Integer priceZone, Double price, String effectiveDate, long exportedDate, char splitIndicator) {
        this.supc = supc;
        this.priceZone = priceZone;
        this.price = price;
        this.effectiveDate = effectiveDate;
        this.exportedDate = exportedDate;
        this.splitIndicator = splitIndicator;
    }

    public String getSupc() {
        return supc;
    }

    public void setSupc(String supc) {
        this.supc = supc;
    }

    public Integer getPriceZone() {
        return priceZone;
    }

    public void setPriceZone(Integer priceZone) {
        this.priceZone = priceZone;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public long getExportedDate() {
        return exportedDate;
    }

    public void setExportedDate(long exportedDate) {
        this.exportedDate = exportedDate;
    }

    public char getSplitIndicator() {
        return splitIndicator;
    }

    public void setSplitIndicator(char splitIndicator) {
        this.splitIndicator = splitIndicator;
    }
}
