package com.sysco.payplus.entity;

public interface AuditableEntity {

  void setSavedBy(String username);

  void setSavedTime(Long timestamp);

}
