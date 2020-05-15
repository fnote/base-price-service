package com.sysco.payplus.entity.masterdata.enums;

public enum Timezone {
  CENTRAL("Central"),
  EASTERN("Eastern"),
  MOUNTAIN("Mountain"),
  ALASKA("Alaska"),
  ARIZONA("Arizona"),
  PACIFIC("Pacific"),
  HAWAII("Hawaii");

  private String value;

  private Timezone(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
