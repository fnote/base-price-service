package com.sysco.rps.dto.refpricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 12. Jun 2020 16:23
 */
public class Product {

    @JsonProperty("supc")
    private String supc;

    @JsonProperty("price_zone_id")
    private String priceZoneId;

    @JsonProperty("reference_price")
    private Double referencePrice;

    @JsonProperty("effective_from_date")
    private String effectiveFromDate;

    @JsonProperty("price_export_date")
    private String priceExportDate;

    public Product(String supc, String priceZoneId, Double referencePrice, String effectiveFromDate,
                   String priceExportDate) {
        this.supc = supc;
        this.priceZoneId = priceZoneId;
        this.referencePrice = referencePrice;
        this.effectiveFromDate = effectiveFromDate;
        this.priceExportDate = priceExportDate;
    }

    public String getSupc() {
        return supc;
    }

    public void setSupc(String supc) {
        this.supc = supc;
    }

    public String getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(String priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public Double getReferencePrice() {
        return referencePrice;
    }

    public void setReferencePrice(Double referencePrice) {
        this.referencePrice = referencePrice;
    }

    public String getEffectiveFromDate() {
        return effectiveFromDate;
    }

    public void setEffectiveFromDate(String effectiveFromDate) {
        this.effectiveFromDate = effectiveFromDate;
    }

    public String getPriceExportDate() {
        return priceExportDate;
    }

    public void setPriceExportDate(String priceExportDate) {
        this.priceExportDate = priceExportDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        return new EqualsBuilder()
              .append(supc, product.supc)
              .append(priceZoneId, product.priceZoneId)
              .append(referencePrice, product.referencePrice)
              .append(effectiveFromDate, product.effectiveFromDate)
              .append(priceExportDate, product.priceExportDate)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(supc)
              .append(priceZoneId)
              .append(referencePrice)
              .append(effectiveFromDate)
              .append(priceExportDate)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("supc", supc)
              .append("priceZoneId", priceZoneId)
              .append("referencePrice", referencePrice)
              .append("effectiveFromDate", effectiveFromDate)
              .append("priceExportDate", priceExportDate)
              .toString();
    }

}
