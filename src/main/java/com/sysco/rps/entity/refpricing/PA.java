package com.sysco.rps.entity.refpricing;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigInteger;
import java.sql.Date;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 04. Jun 2020 11:46
 */

@Entity
@Table(name = "PA",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"ID"})
)
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
public class PA {
    @Id
    @Column(name = "ID", updatable = false, nullable = false, length = 10)
    private Integer id;

    @Column(name = "SUPC", nullable = false, length = 9)
    private String supc;

    @Column(name = "PRICE_ZONE", nullable = false, length = 8)
    private String priceZone;

    @Column(name = "PRICE", nullable = false)
    private Double price;

    @Column(name = "EFFECTIVE_DATE", nullable = false)
    private Date effectiveDate;

    @Column(name = "ADDED_TIME", nullable = false, length = 14)
    private BigInteger addedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public BigInteger getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(BigInteger addedTime) {
        this.addedTime = addedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PA)) return false;

        PA pa = (PA) o;

        return new EqualsBuilder()
                .append(id, pa.id)
                .append(supc, pa.supc)
                .append(priceZone, pa.priceZone)
                .append(price, pa.price)
                .append(effectiveDate, pa.effectiveDate)
                .append(addedTime, pa.addedTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(supc)
                .append(priceZone)
                .append(price)
                .append(effectiveDate)
                .append(addedTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("supc", supc)
                .append("priceZone", priceZone)
                .append("price", price)
                .append("effectiveDate", effectiveDate)
                .append("addedTime", addedTime)
                .toString();
    }
}
