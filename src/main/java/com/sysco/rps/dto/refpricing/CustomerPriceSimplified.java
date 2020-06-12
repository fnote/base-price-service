package com.sysco.rps.dto.refpricing;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.sql.Date;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 08. Jun 2020 06:04
 */
public class CustomerPriceSimplified {

    private String supc;

    private String priceZone;

    private String customerId;

    private Double price;

    private Date effectiveDate;

    public String getSupc() {
        return supc;
    }

    public void setSupc(String supc) {
        this.supc = supc;
    }

    public String getPriceZone() {
        return priceZone;
    }

    public void setPriceZone(String priceZone) {
        this.priceZone = priceZone;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }


    public CustomerPriceSimplified(String supc, String priceZone, String customerId, Double price, Date effectiveDate) {
        this.supc = supc;
        this.priceZone = priceZone;
        this.customerId = customerId;
        this.price = price;
        this.effectiveDate = effectiveDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CustomerPriceSimplified)) return false;

        CustomerPriceSimplified that = (CustomerPriceSimplified) o;

        return new EqualsBuilder()
              .append(supc, that.supc)
              .append(priceZone, that.priceZone)
              .append(customerId, that.customerId)
              .append(price, that.price)
              .append(effectiveDate, that.effectiveDate)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(supc)
              .append(priceZone)
              .append(customerId)
              .append(price)
              .append(effectiveDate)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("supc", supc)
              .append("priceZone", priceZone)
              .append("customerId", customerId)
              .append("price", price)
              .append("effectiveDate", effectiveDate)
              .toString();
    }

}
