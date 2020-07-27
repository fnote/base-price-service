package com.sysco.rps.entity.refpricing;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 05. Jun 2020 09:09
 */
public class EATSIdentifier implements Serializable {

    private String supc;
    private String customerId;

    public EATSIdentifier(){

    }

    public EATSIdentifier(String supc, String customerId) {
        this.supc = supc;
        this.customerId = customerId;
    }

    public String getSupc() {
        return supc;
    }

    public void setSupc(String supc) {
        this.supc = supc;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof EATSIdentifier)) return false;

        EATSIdentifier eatsId = (EATSIdentifier) o;

        return new EqualsBuilder()
                .append(supc, eatsId.supc)
                .append(customerId, eatsId.customerId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(supc)
                .append(customerId)
                .toHashCode();
    }
}
