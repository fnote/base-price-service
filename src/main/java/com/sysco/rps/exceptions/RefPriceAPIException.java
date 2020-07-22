package com.sysco.rps.exceptions;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpStatus;

import java.util.Objects;

/**
 * The class to be used during reference price exceptions
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 13. Jul 2020 08:19
 */
public class RefPriceAPIException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatusCode;
    private final transient Object additionalInfo;

    public RefPriceAPIException(HttpStatus httpStatusCode, String errorCode, String message) {
        this(httpStatusCode, errorCode, message, null);
    }

    private RefPriceAPIException(HttpStatus httpStatusCode, String errorCode, String message, Throwable throwable) {
        this(httpStatusCode, errorCode, message, null, throwable);
    }

    private RefPriceAPIException(HttpStatus httpStatusCode, String errorCode, String message, Object additionalInfo, Throwable throwable) {
        super(message, throwable);
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.additionalInfo = additionalInfo == null ? message : additionalInfo;
    }

    public HttpStatus getHttpStatusCode() {
        return Objects.requireNonNullElse(httpStatusCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .appendSuper(super.toString())
              .append("errorCode", errorCode)
              .append("httpStatusCode", httpStatusCode)
              .append("additionalInfo", additionalInfo)
              .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RefPriceAPIException)) return false;

        RefPriceAPIException exception = (RefPriceAPIException) o;
        return Objects.equals(errorCode, exception.errorCode) &&
              Objects.equals(httpStatusCode, exception.httpStatusCode) &&
              Objects.equals(additionalInfo, exception.additionalInfo);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(errorCode)
              .append(httpStatusCode)
              .append(additionalInfo)
              .toHashCode();
    }
}
