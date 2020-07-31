package com.sysco.rps.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    private String traceId;

    public ErrorDTO(String code) {
        this.code = code;
    }

    private ErrorDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private ErrorDTO(String code, String message, Object errorData) {
        this(code, message);
        this.errorData = errorData;
    }

    public ErrorDTO(String code, String message, Object errorData, String traceId) {
        this(code, message, errorData);
        this.traceId = traceId;
    }


    public ErrorDTO(String code, String message, String traceId) {
        this(code, message);
        this.traceId = traceId;
    }


    public ErrorDTO(String code, String message, Object errorData, Object originalData) {
        this(code, message, errorData);
        this.originalData = originalData;
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

    public void setOriginalData(Object originalData) {
        this.originalData = originalData;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ErrorDTO)) return false;

        ErrorDTO errorDTO = (ErrorDTO) o;

        return new EqualsBuilder()
              .append(code, errorDTO.code)
              .append(message, errorDTO.message)
              .append(errorData, errorDTO.errorData)
              .append(originalData, errorDTO.originalData)
              .append(traceId, errorDTO.traceId)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(code)
              .append(message)
              .append(errorData)
              .append(originalData)
              .append(traceId)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("code", code)
              .append("message", message)
              .append("errorData", errorData)
              .append("originalData", originalData)
              .append("traceId", traceId)
              .toString();
    }

}
