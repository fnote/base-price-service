package com.sysco.rps.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 12. Jun 2020 16:22
 */
public class CustomerPrice {

//    @JsonProperty("business_unit_number")
    private String businessUnitNumber;

//    @JsonProperty("customer_account")
    private String customerAccount;

//    @JsonProperty("price_request_date")
    private String priceRequestDate;

//    @JsonProperty("products")
    private List<Product> products;


    public CustomerPrice(String businessUnitNumber, String customerAccount, String priceRequestDate,
                         List<Product> products) {
        this.businessUnitNumber = businessUnitNumber;
        this.customerAccount = customerAccount;
        this.priceRequestDate = priceRequestDate;
        this.products = products;
    }

    public CustomerPrice(CustomerPriceRequest customerPriceRequest, List<Product> products) {
        this.businessUnitNumber = customerPriceRequest.getBusinessUnitNumber();
        this.customerAccount = customerPriceRequest.getCustomerAccount();
        this.priceRequestDate = customerPriceRequest.getPriceRequestDate();
        this.products = products;
    }

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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CustomerPrice)) return false;

        CustomerPrice that = (CustomerPrice) o;

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
