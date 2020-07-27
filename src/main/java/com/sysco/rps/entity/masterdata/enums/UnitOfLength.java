package com.sysco.rps.entity.masterdata.enums;

import io.swagger.annotations.ApiModel;

@ApiModel
public enum UnitOfLength {
  MILES("Miles"),
  KILOMETRES("Kilometres");

  private String value;

  private UnitOfLength(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
