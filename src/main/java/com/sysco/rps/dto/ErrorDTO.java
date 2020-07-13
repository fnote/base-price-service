package com.sysco.rps.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
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

        return new EqualsBuilder()
              .appendSuper(super.equals(o))
              .append(code, errorDTO.code)
              .append(message, errorDTO.message)
              .append(errorData, errorDTO.errorData)
              .append(originalData, errorDTO.originalData)
              .isEquals();
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
}
