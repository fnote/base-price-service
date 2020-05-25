package com.sysco.rps.entity.exception;

import com.sysco.rps.entity.RawBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

@Entity
@Table(name = "exception_table_map",
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"id"})
)
public class ExceptionTableMap extends RawBaseEntity {

  @Column(name = "table_name", nullable = false, length = 45)
  private String tableName;
  @Column(name = "entry_id", nullable = false)
  private Long entryId;
  @Column(name = "entry_version", nullable = true)
  private Long entryVersion;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "exception_id", nullable = false)
  private ExceptionEntity exception;

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public Long getEntryId() {
    return entryId;
  }

  public void setEntryId(Long entryId) {
    this.entryId = entryId;
  }

  public ExceptionEntity getException() {
    return exception;
  }

  public void setException(ExceptionEntity exception) {
    this.exception = exception;
  }

  public Long getEntryVersion() {
    return entryVersion;
  }

  public void setEntryVersion(Long entryVersion) {
    this.entryVersion = entryVersion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ExceptionTableMap that = (ExceptionTableMap) o;

    return new EqualsBuilder()
        .append(entryId, that.entryId)
        .append(tableName, that.tableName)
        .append(exception, that.exception)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(tableName)
        .append(entryId)
        .append(exception)
        .toHashCode();
  }
}
