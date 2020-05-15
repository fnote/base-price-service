package com.sysco.payplus.service.exception;

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum ErrorCode {

  INTERNAL_SERVER_ERROR("9999", "Internal Service Failure"),
  BAD_REQUEST("1000", "Bad Request"),
  VALIDATION_FAILURE("1010", "Validation Failure"),
  RANGE_VALIDATION_FAILURE("1020", "Range Validation Failure"),
  RECORD_LOCKED("1500", "Record Locked"),
  RECORD_NOT_FOUND("1510", "Record Not Found"),
  DUPLICATE_RECORD("1520", "Duplicate Record"),
  ACCESS_DENIED("2000", "Access Denied"),
  NO_HANDLER_FOUND("3000", "HTTP Handler Not Found");

  private final String code;
  private final String description;

  ErrorCode(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("code", code)
        .append("description", description)
        .toString();
  }
}
