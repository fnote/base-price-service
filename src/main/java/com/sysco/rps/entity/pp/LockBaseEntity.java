package com.sysco.rps.entity.pp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;

@MappedSuperclass
public abstract class LockBaseEntity extends BaseEntity {

  @Column(name = "is_locked", nullable = false)
  protected Boolean locked = false;

  private transient Boolean wasLocked = false;

  @PostLoad
  public void postUpdate() {
    wasLocked = locked;
  }

  public Boolean wasLocked() {
    return wasLocked;
  }

  public void setWasLocked() {
    wasLocked = true;
  }

  public void unlock() {
    this.locked = false;
  }
}
