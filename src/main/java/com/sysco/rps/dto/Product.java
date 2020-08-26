package com.sysco.rps.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Product bean that contains information about customer supc mapping with price info
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 12. Jun 2020 16:23
 */
public class Product {

    private String supc;
    private Integer priceZoneId;
    private Double referencePrice;
    private String effectiveFromDate;
    private Long priceExportDate;
    private Boolean catchWeightIndicator;

    public Product(String supc, Integer priceZoneId, Double referencePrice, String effectiveFromDate, Long priceExportDate, Boolean catchWeightIndicator) {
        this.supc = supc;
        this.priceZoneId = priceZoneId;
        this.referencePrice = referencePrice;
        this.effectiveFromDate = effectiveFromDate;
        this.priceExportDate = priceExportDate;
        this.catchWeightIndicator = catchWeightIndicator;
    }

    public String getSupc() {
        return supc;
    }

    public void setSupc(String supc) {
        this.supc = supc;
    }

    public Integer getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Integer priceZoneId) {
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

    public Long getPriceExportDate() {
        return priceExportDate;
    }

    public void setPriceExportDate(Long priceExportDate) {
        this.priceExportDate = priceExportDate;
    }

    public Boolean getCatchWeightIndicator() {
        return catchWeightIndicator;
    }

    public void setCatchWeightIndicator(Boolean catchWeightIndicator) {
        this.catchWeightIndicator = catchWeightIndicator;
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
              .append(catchWeightIndicator, product.catchWeightIndicator)
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
              .append(catchWeightIndicator)
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
              .append("catchWeightIndicator", catchWeightIndicator)
              .toString();
    }

}
