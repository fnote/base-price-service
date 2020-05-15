package com.sysco.payplus.entity.masterdata.enums;

import io.swagger.annotations.ApiModel;

@ApiModel
public enum Currency {
  USD("USD");

  private String value;

  private Currency(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}