package com.sysco.rps.entity.exception;

import com.sysco.rps.entity.RawBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "exception_type",
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"id"})
)
public class ExceptionType extends RawBaseEntity {

  @Column(name = "exception_type", nullable = false, length = 45)
  private String exceptionTypeValue;

  public String getExceptionTypeValue() {
    return exceptionTypeValue;
  }

  public void setExceptionTypeValue(String exceptionType) {
    this.exceptionTypeValue = exceptionType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ExceptionType that = (ExceptionType) o;

    return new EqualsBuilder()
        .append(exceptionTypeValue, that.exceptionTypeValue)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(exceptionTypeValue)
        .toHashCode();
  }
}
