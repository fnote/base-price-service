package com.sysco.rps.dto.refpricing;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 08. Jun 2020 06:13
 */
public class CustomerPriceReqDTO {

    @ApiModelProperty(example = "[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\"]")
    private List<String> supcs;

    @ApiModelProperty(example = "\"1\"")
    private String customerId;

    @ApiModelProperty(example = "\"2022-12-12\"")
    private String effectiveDate;

    public List<String> getSupcs() {
        return supcs;
    }

    public void setSupcs(List<String> supcs) {
        this.supcs = supcs;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CustomerPriceReqDTO)) return false;

        CustomerPriceReqDTO that = (CustomerPriceReqDTO) o;

        return new EqualsBuilder()
              .append(supcs, that.supcs)
              .append(customerId, that.customerId)
              .append(effectiveDate, that.effectiveDate)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(supcs)
              .append(customerId)
              .append(effectiveDate)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("supcs", supcs)
              .append("customerId", customerId)
              .append("effectiveDate", effectiveDate)
              .toString();
    }

}
