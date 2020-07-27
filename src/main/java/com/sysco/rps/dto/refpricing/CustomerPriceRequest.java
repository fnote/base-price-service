package com.sysco.rps.dto.refpricing;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 12. Jun 2020 17:00
 */
public class CustomerPriceRequest {

    @ApiModelProperty(example = "001")
    @JsonProperty("business_unit_number")
    private String businessUnitNumber;

    @ApiModelProperty(example = "1")
    @JsonProperty("customer_account")
    private String customerAccount;

    @ApiModelProperty(example = "2020-05-31")
    @JsonProperty("price_request_date")
    private String priceRequestDate;

    @ApiModelProperty(example = "[ \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\"]")
    @JsonProperty("products")
    private List<String> products;

    public String getBusinessUnitNumber() {
        return businessUnitNumber;
    }

    public void setBusinessUnitNumber(String businessUnitNumber) {
        this.businessUnitNumber = businessUnitNumber;
    }

    public String getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    public String getPriceRequestDate() {
        return priceRequestDate;
    }

    public void setPriceRequestDate(String priceRequestDate) {
        this.priceRequestDate = priceRequestDate;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CustomerPriceRequest)) return false;

        CustomerPriceRequest that = (CustomerPriceRequest) o;

        return new EqualsBuilder()
              .append(businessUnitNumber, that.businessUnitNumber)
              .append(customerAccount, that.customerAccount)
              .append(priceRequestDate, that.priceRequestDate)
              .append(products, that.products)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(businessUnitNumber)
              .append(customerAccount)
              .append(priceRequestDate)
              .append(products)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("businessUnitNumber", businessUnitNumber)
              .append("customerAccount", customerAccount)
              .append("priceRequestDate", priceRequestDate)
              .append("products", products)
              .toString();
    }

}
