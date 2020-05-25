package com.sysco.rps.entity.masterdata.enums;

public enum StopClass {
  REGULAR("Regular"),
  PREMIUM("Premium"),
  PALLET_REGULAR("Pallet+Regular"),
  BACKHAUL("Backhaul"),
  PALLET_PREMIUM("Pallet+Premium"),
  PREMIUM_PLUS("Premium Plus");

  private String value;

  StopClass(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
