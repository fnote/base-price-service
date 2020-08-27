package com.sysco.rps.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The data transfer object for minor errors
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 29. Jul 2020 11:47
 */
public class MinorErrorDTO {
    private String supc;
    private String errorCode;
    private String message;

    public MinorErrorDTO(String supc, String errorCode, String message) {
        this.supc = supc;
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getSupc() {
        return supc;
    }

    public void setSupc(String supc) {
        this.supc = supc;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MinorErrorDTO)) return false;

        MinorErrorDTO that = (MinorErrorDTO) o;

        return new EqualsBuilder()
              .append(supc, that.supc)
              .append(errorCode, that.errorCode)
              .append(message, that.message)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(supc)
              .append(errorCode)
              .append(message)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("supc", supc)
              .append("errorCode", errorCode)
              .append("message", message)
              .toString();
    }
}
