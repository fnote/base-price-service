package com.sysco.rps.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 10. Jun 2020 16:35
 */
public class ResponseWrapper<T> {


    private Map<String, String> metadata;
    private T data;


    public ResponseWrapper() {
    }

    public ResponseWrapper(T data, Map<String, String> metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ResponseWrapper)) return false;

        ResponseWrapper<?> that = (ResponseWrapper<?>) o;

        return new EqualsBuilder()
              .append(data, that.data)
              .append(metadata, that.metadata)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(data)
              .append(metadata)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("metadata", metadata)
              .append("data", data)
              .toString();
    }


}
