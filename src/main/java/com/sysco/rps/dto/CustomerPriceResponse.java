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
public class CustomerPriceResponse extends BaseResponse {

    private String businessUnitNumber;
    private String customerAccount;
    private String priceRequestDate;

    public CustomerPriceResponse(CustomerPriceRequest customerPriceRequest, List<Product> products, List<ErrorDTO> errors) {
        super(products, errors);
        this.businessUnitNumber = customerPriceRequest.getBusinessUnitNumber();
        this.customerAccount = customerPriceRequest.getCustomerAccount();
        this.priceRequestDate = customerPriceRequest.getPriceRequestDate();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CustomerPriceResponse)) return false;

        CustomerPriceResponse that = (CustomerPriceResponse) o;

        return new EqualsBuilder()
              .appendSuper(super.equals(o))
              .append(businessUnitNumber, that.businessUnitNumber)
              .append(customerAccount, that.customerAccount)
              .append(priceRequestDate, that.priceRequestDate)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .appendSuper(super.hashCode())
              .append(businessUnitNumber)
              .append(customerAccount)
              .append(priceRequestDate)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("businessUnitNumber", businessUnitNumber)
              .append("customerAccount", customerAccount)
              .append("priceRequestDate", priceRequestDate)
              .toString();
    }

}
