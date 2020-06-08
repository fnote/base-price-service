package com.sysco.rps.entity.pp;

public interface AuditableEntity {

  void setSavedBy(String username);

  void setSavedTime(Long timestamp);

}
