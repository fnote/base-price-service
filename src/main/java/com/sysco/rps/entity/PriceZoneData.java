package com.sysco.rps.entity;

/**
 * Entity bean for Price Zone data
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 20. Jul 2020 12:28
 */
public class PriceZoneData {

    private String supc;
    private Integer priceZone;
    private String customerId;
    private String effectiveDate;

    public PriceZoneData(String supc, Integer priceZone, String customerId, String effectiveDate) {
        this.supc = supc;
        this.priceZone = priceZone;
        this.customerId = customerId;
        this.effectiveDate = effectiveDate;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}

