package com.sysco.rps.entity.masterdata.enums;

public enum Market {
  MIDWEST("Midwest"),
  MOUNTAIN_CENTRAL("Mountain Central"),
  NORTHEAST("Northeast"),
  PACIFIC("Pacific"),
  SOUTH("South"),
  SOUTHEAST("South East");

  private String value;

  private Market(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
