package com.sysco.rps.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Super class that provides saved_by and saved_time fields
 */

@MappedSuperclass
//this makes sure of nothing has change, nothing will be saved.
@DynamicUpdate
public abstract class EnumBaseEntity implements AuditableEntity, Serializable {

  @Column(name = "saved_by", nullable = false, length = 45)
  protected String savedBy;
  @Column(name = "saved_time", nullable = false)
  protected Long savedTime;

  public String getSavedBy() {
    return savedBy;
  }

  @Override
  public void setSavedBy(String savedBy) {
    this.savedBy = savedBy;
  }

  public Long getSavedTime() {
    return savedTime;
  }

  @Override
  public void setSavedTime(Long savedTime) {
    this.savedTime = savedTime;
  }
}
