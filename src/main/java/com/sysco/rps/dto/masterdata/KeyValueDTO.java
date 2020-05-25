package com.sysco.rps.dto.masterdata;

import io.swagger.annotations.ApiModelProperty;

public class KeyValueDTO {

  @ApiModelProperty(example = "Name-key")
  private String key;
  @ApiModelProperty(example = "Display Value")
  private String displayString;

  public KeyValueDTO(String name, String displayString) {
    this.key = name;
    this.displayString = displayString;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDisplayString() {
    return displayString;
  }

  public void setDisplayString(String displayString) {
    this.displayString = displayString;
  }
}
