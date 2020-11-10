package com.sysco.rps.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
    private String priceZoneId;
    private Double referencePrice;
    private String effectiveFromDate;
    private Long priceExportTimestamp;
    private Boolean catchWeightIndicator;

    @JsonIgnore
    private Boolean isDefaultPriced = false;

    public Product(String supc, String priceZoneId, Double referencePrice, String effectiveFromDate, Long priceExportTimestamp, Boolean catchWeightIndicator) {
        this.supc = supc;
        this.priceZoneId = priceZoneId;
        this.referencePrice = referencePrice;
        this.effectiveFromDate = effectiveFromDate;
        this.priceExportTimestamp = priceExportTimestamp;
        this.catchWeightIndicator = catchWeightIndicator;
    }

    public Product(String supc, String priceZoneId, Double referencePrice, String effectiveFromDate, Long priceExportTimestamp,
                   Boolean catchWeightIndicator, Boolean isDefaultPriced) {
        this(supc, priceZoneId, referencePrice, effectiveFromDate, priceExportTimestamp, catchWeightIndicator);
        this.isDefaultPriced = isDefaultPriced;
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

    public Long getPriceExportTimestamp() {
        return priceExportTimestamp;
    }

    public void setPriceExportTimestamp(Long priceExportTimestamp) {
        this.priceExportTimestamp = priceExportTimestamp;
    }

    public Boolean getCatchWeightIndicator() {
        return catchWeightIndicator;
    }

    public void setCatchWeightIndicator(Boolean catchWeightIndicator) {
        this.catchWeightIndicator = catchWeightIndicator;
    }

    Boolean getDefaultPriced() {
        return isDefaultPriced;
    }

    public void setDefaultPriced(Boolean defaultPriced) {
        isDefaultPriced = defaultPriced;
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
              .append(priceExportTimestamp, product.priceExportTimestamp)
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
              .append(priceExportTimestamp)
              .append(catchWeightIndicator)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
              .append("supc", supc)
              .append("priceZoneId", priceZoneId)
              .append("referencePrice", referencePrice)
              .append("effectiveFromDate", effectiveFromDate)
              .append("priceExportTimestamp", priceExportTimestamp)
              .append("catchWeightIndicator", catchWeightIndicator)
              .toString();
    }

}
