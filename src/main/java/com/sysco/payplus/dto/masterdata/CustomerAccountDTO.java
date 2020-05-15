package com.sysco.payplus.dto.masterdata;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/8/20 Time: 12:54 PM
 */

public class CustomerAccountDTO extends BaseOpCoDTO {

  @NotBlank(message = "Account name can not be blank")
  @ApiModelProperty(example = "DON JULIO!")
  private String accountName;

  @NotBlank(message = "Account number can not be blank")
  @ApiModelProperty(example = "561530")
  private String accountNumber;

  //todo: enum, classification be updated via payplus
  @NotBlank(message = "Classification can not be blank")
  @ApiModelProperty(example = "Premium")
  private String classification;

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getClassification() {
    return classification;
  }

  public void setClassification(String classification) {
    this.classification = classification;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CustomerAccountDTO that = (CustomerAccountDTO) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(accountName, that.accountName)
        .append(accountNumber, that.accountNumber)
        .append(classification, that.classification)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(accountName)
        .append(accountNumber)
        .append(classification)
        .toHashCode();
  }
}
