package com.sysco.rps.entity;

import org.springframework.util.StringUtils;

/**
 * Bean to hold Price Advisor Data (supc-priceZone-price mapping)
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
        this.exportedDate = exportedDate;
        this.splitIndicator = splitIndicator;
        setEffectiveDate(effectiveDate);
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
        if(StringUtils.isEmpty(effectiveDate)) {
            this.effectiveDate = effectiveDate;
        } else {
            this.effectiveDate = effectiveDate.replace("/", "-");
        }
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
