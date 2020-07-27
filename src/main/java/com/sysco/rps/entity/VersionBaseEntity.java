package com.sysco.rps.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class VersionBaseEntity extends LockBaseEntity{

  @Column(name = "is_current")
  protected Boolean isCurrent = true;

  @Column(name = "version", insertable = false)
  protected Long version = 1L;

  public void setIsCurrent(Boolean isCurrent) {
    this.isCurrent = isCurrent;
  }
}
