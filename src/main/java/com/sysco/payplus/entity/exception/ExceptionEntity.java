package com.sysco.payplus.entity.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.sysco.payplus.entity.RawBaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "exception",
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"id"})
)
public class ExceptionEntity extends RawBaseEntity {

  @Type(type = "jsonb")
  @Column(name = "exception_data", columnDefinition = "jsonb", nullable = false)
  private JsonNode exceptionData;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "exception_type", nullable = false)
  private ExceptionType exceptionType;

  public JsonNode getExceptionData() {
    return exceptionData;
  }

  public void setExceptionData(JsonNode exceptionData) {
    this.exceptionData = exceptionData;
  }

  public ExceptionType getExceptionType() {
    return exceptionType;
  }

  public void setExceptionType(ExceptionType exceptionType) {
    this.exceptionType = exceptionType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ExceptionEntity exception = (ExceptionEntity) o;

    return new EqualsBuilder()
        .append(exceptionData, exception.exceptionData)
        .append(exceptionType, exception.exceptionType)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(exceptionData)
        .append(exceptionType)
        .toHashCode();
  }
}
