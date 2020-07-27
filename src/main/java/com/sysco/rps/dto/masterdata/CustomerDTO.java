package com.sysco.rps.dto.masterdata;

import com.sysco.rps.entity.masterdata.Customer;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;

public class CustomerDTO extends BaseCustomerDTO {

    @NotBlank(message = "opCo number can not be blank")
    @ApiModelProperty(example = "US0075")
    private String opCoNumber;

    @NotBlank(message = "customer number can not be blank")
    @ApiModelProperty(example = "11111111")
    private String customerNumber;

    @NotBlank(message = "customer name can not be blank")
    @ApiModelProperty(example = "John Smith")
    private String customerName;

    public CustomerDTO(String opCoNumber, String customerNumber, String customerName){
        this.opCoNumber = opCoNumber;
        this.customerNumber = customerNumber;
        this.customerName = customerName;
    }

    public CustomerDTO(Customer customer) {
        //set driver props
        getModelMapper().map(customer, this);
    }

    public Customer merge(Customer customer) {
        getModelMapper().map(this, customer);
        return customer;
    }

    public String getOpCoNumber() {
        return opCoNumber;
    }

    public void setOpCoNumber(String opCoNumber) {
        this.opCoNumber = opCoNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("opCoNumber", opCoNumber)
                .append("customerNumber", customerNumber)
                .append("customerName", customerName)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CustomerDTO that = (CustomerDTO) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getOpCoNumber(), that.getOpCoNumber())
                .append(getCustomerNumber(), that.getCustomerNumber())
                .append(getCustomerName(), that.getCustomerName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getOpCoNumber())
                .append(getCustomerNumber())
                .append(getCustomerName())
                .toHashCode();
    }
}
