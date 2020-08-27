package com.sysco.rps.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * The Base Response class that can be extended by other sub classes related to responses
 *
 * @author rohana.kumara@sysco.com
 * @copyright (C) 2020, Sysco Corporation
 * @end Created : 4/1/20 Time: 12:54 PM
 */

public class BaseResponse<T> {
    private final List<T> products;

    // contains info about requested objects that resulted in minor errors
    private final List<MinorErrorDTO> failedProducts;


    BaseResponse(List<T> products, List<MinorErrorDTO> failedProducts) {
        this.products = products;
        this.failedProducts = failedProducts;
    }

    public List<T> getProducts() {
        return products;
    }

    public List<MinorErrorDTO> getFailedProducts() {
        return failedProducts;
    }

    public void addSuccessfulProduct(T product) {
        this.products.add(product);
    }

    public void addFailedProduct(MinorErrorDTO errorDTO) {
        this.failedProducts.add(errorDTO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BaseResponse)) return false;

        BaseResponse<?> that = (BaseResponse<?>) o;

        return new EqualsBuilder()
              .append(products, that.products)
              .append(failedProducts, that.failedProducts)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(products)
              .append(failedProducts)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("products", (products == null) ? null : Arrays.toString(products.toArray()))
              .append("failedProducts", (failedProducts == null) ? null : Arrays.toString(failedProducts.toArray()))
              .toString();
    }

}
