package com.sysco.rps.entity.pp.masterdata;

import com.sysco.rps.entity.pp.VersionBaseEntity;
import com.sysco.rps.entity.pp.VersionIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.sysco.rps.entity.pp.masterdata.enums.StopAttribute;
import com.sysco.rps.entity.pp.masterdata.enums.StopClass;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.util.List;


@Entity
@Table(name = "stop_classification",
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"id", "version"})
)
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
@IdClass( VersionIdentifier.class )
public class Customer extends VersionBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stop_classification_sequence_generator")
  @SequenceGenerator(name="stop_classification_sequence_generator", sequenceName = "stop_classification_sequence", initialValue = 1, allocationSize = 1)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;
  @Column(name = "customer_number", nullable = false, length = 45)
  private String customerNumber;
  @Column(name = "customer_name", nullable = false, length = 150)
  private String customerName;
  @Column(name = "stop_class", nullable = false, length = 45)
  @Enumerated(EnumType.STRING)
  private StopClass stopClass;
  @Type(type = "jsonb")
  @Column(name = "stop_attributes", columnDefinition = "jsonb")
  private List<StopAttribute> stopAttributes;

  public String getCustomerNumber() {
    return customerNumber;
  }

  public void setCustomerNumber(String customerNumber) {
    this.customerNumber = customerNumber;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public StopClass getStopClass() {
    return stopClass;
  }

  public void setStopClass(StopClass stopClass) {
    this.stopClass = stopClass;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<StopAttribute> getStopAttributes() {
    return stopAttributes;
  }

  public void setStopAttributes(List<StopAttribute> stopAttributes) {
    this.stopAttributes = stopAttributes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Customer that = (Customer) o;

    return new EqualsBuilder()
        .append(customerNumber, that.customerNumber)
        .append(customerName, that.customerName)
        .append(stopClass, that.stopClass)
        .append(stopAttributes, that.stopAttributes)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(customerNumber)
        .append(customerName)
        .append(stopClass)
        .append(stopAttributes)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("id", id)
            .append("customerNumber", customerNumber)
            .append("customerName", customerName)
            .append("stopClass", stopClass)
            .append("stopAttributes", stopAttributes)
            .toString();
  }
}
