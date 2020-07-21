package com.sysco.rps.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * Bean that can be used to include error data for responses
 *
 * @author rohana.kumara@sysco.com
 * @copyright (C) 2020, Sysco Corporation
 * @end Created : 3/13/20 Time: 12:54 PM
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {

    private String code;
    private String message;
    private Object errorData;
    private Object originalData;

    public ErrorDTO(String code) {
        this.code = code;
    }

    public ErrorDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorDTO(String code, String message, Object errorData) {
        this.code = code;
        this.message = message;
        this.errorData = errorData;
    }

    public ErrorDTO(String code, String message, Object originalData, Object errorData) {
        this.code = code;
        this.message = message;
        this.originalData = originalData;
        this.errorData = errorData;
    }

    public Object getOriginalData() {
        return originalData;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getErrorData() {
        return errorData;
    }

    public void setErrorData(Object errorData) {
        this.errorData = errorData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ErrorDTO errorDTO = (ErrorDTO) o;

        return Objects.equals(code, errorDTO.code) &&
              Objects.equals(message, errorDTO.message) &&
              Objects.equals(errorData, errorDTO.errorData) &&
              Objects.equals(originalData, errorDTO.originalData);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .appendSuper(super.hashCode())
              .append(code)
              .append(message)
              .append(errorData)
              .append(originalData)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("code", code)
              .append("message", message)
              .append("errorData", errorData)
              .append("originalData", originalData)
              .toString();
    }

}
