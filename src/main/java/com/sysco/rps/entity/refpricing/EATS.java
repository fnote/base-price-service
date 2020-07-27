package com.sysco.rps.entity.refpricing;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 04. Jun 2020 11:46
 */

@Entity
@Table(name = "EATS_001",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"SUPC", "CUSTOMER_ID"})
)
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
@IdClass(EATSIdentifier.class)
public class EATS implements Serializable {

    @Id
    @Column(name = "SUPC", nullable = false, length = 9)
    private String supc;

    @Column(name = "PRICE_ZONE", nullable = false, length = 8)
    private String priceZone;

    @Id
    @Column(name = "CUSTOMER_ID", nullable = false, length = 14)
    private String customerId;

    @Column(name = "ADDED_TIME", nullable = false, length = 14)
    private BigInteger addedTime;

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

    public BigInteger getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(BigInteger addedTime) {
        this.addedTime = addedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof EATS)) return false;

        EATS eats = (EATS) o;

        return new EqualsBuilder()
                .append(supc, eats.supc)
                .append(priceZone, eats.priceZone)
                .append(customerId, eats.customerId)
                .append(addedTime, eats.addedTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(supc)
                .append(priceZone)
                .append(customerId)
                .append(addedTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("supc", supc)
                .append("priceZone", priceZone)
                .append("customerId", customerId)
                .append("addedTime", addedTime)
                .toString();
    }

}
