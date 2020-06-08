package com.sysco.rps.entity.pp.masterdata.enums;

public enum StopAttribute {
  STAIRS("Stairs"),
  ELEVATOR("Elevator"),
  LONG_WALK("Long Walk"),
  SPECIAL_EVENT("Special Event");

  private String value;

  StopAttribute(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
