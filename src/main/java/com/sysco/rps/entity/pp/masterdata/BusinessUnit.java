package com.sysco.rps.entity.pp.masterdata;

import com.sysco.rps.util.Conversions;
import com.sysco.rps.util.PricingUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 21. Jun 2020 09:16
 */
public class BusinessUnit {
    /**
     * The unique identifier for this business unit
     */
    private String businessUnitNumber = "";
    /**
     * The descriptive name for this business unit
     */
    private String businessName = "";

    /**
     * Constructor requires the business unit number
     */
    public BusinessUnit(String bunitId) {
        super();
        setBusinessUnitNumber(bunitId);
    }

    private void setBusinessUnitNumber(String businessUnitNumber) {
        businessUnitNumber = Conversions.convertOpCoIdToDBField(businessUnitNumber);
        this.businessUnitNumber = PricingUtils.trimSafely(businessUnitNumber, "000");
    }

    public String getBusinessUnitNumber() {
        return businessUnitNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BusinessUnit)) return false;

        BusinessUnit that = (BusinessUnit) o;

        return new EqualsBuilder()
              .append(businessUnitNumber, that.businessUnitNumber)
              .append(businessName, that.businessName)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(businessUnitNumber)
              .append(businessName)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("businessUnitNumber", businessUnitNumber)
              .append("businessName", businessName)
              .toString();
    }

}
