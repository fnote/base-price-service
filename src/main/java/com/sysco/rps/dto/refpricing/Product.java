package com.sysco.rps.dto.refpricing;

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

    private String supc;
    private String priceZoneId;
    private Double referencePrice;
    private String effectiveFromDate;
    private String priceExportDate;

    public Product(String supc, String priceZoneId, Double referencePrice, String effectiveFromDate) {
        this.supc = supc;
        this.priceZoneId = priceZoneId;
        this.referencePrice = referencePrice;
        this.effectiveFromDate = effectiveFromDate;
        //TODO add price export date when DBs are ready
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
