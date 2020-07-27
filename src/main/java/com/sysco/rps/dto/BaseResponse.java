package com.sysco.rps.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/1/20 Time: 12:54 PM
 */

public class BaseResponse<T> {
    private final List<T> successfulItems;
    private final List<ErrorDTO> failedItems;


    BaseResponse(List<T> successfulItems, List<ErrorDTO> failedItems) {
        this.successfulItems = successfulItems;
        this.failedItems = failedItems;
    }

    public List<T> getSuccessfulItems() {
        return successfulItems;
    }

    public List<ErrorDTO> getFailedItems() {
        return failedItems;
    }

    public void addSuccessfulItem(T item) {
        this.successfulItems.add(item);
    }

    public void addFailedItem(ErrorDTO errorDTO) {
        this.failedItems.add(errorDTO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BaseResponse)) return false;

        BaseResponse<?> that = (BaseResponse<?>) o;

        return new EqualsBuilder()
              .append(successfulItems, that.successfulItems)
              .append(failedItems, that.failedItems)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(successfulItems)
              .append(failedItems)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("successfulItems", successfulItems)
              .append("failedItems", failedItems)
              .toString();
    }


}
