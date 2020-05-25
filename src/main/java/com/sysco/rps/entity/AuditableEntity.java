package com.sysco.rps.entity;

public interface AuditableEntity {

  void setSavedBy(String username);

  void setSavedTime(Long timestamp);

}
