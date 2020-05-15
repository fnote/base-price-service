package com.sysco.payplus.dto.masterdata;

import com.sysco.payplus.entity.masterdata.OpCo;
import com.sysco.payplus.entity.masterdata.enums.ActivationStatus;
import com.sysco.payplus.entity.masterdata.enums.Currency;
import com.sysco.payplus.entity.masterdata.enums.Market;
import com.sysco.payplus.entity.masterdata.enums.Timezone;
import com.sysco.payplus.entity.masterdata.enums.UnitOfLength;
import com.sysco.payplus.validators.annotations.ValidEnumFormat;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/8/20 Time: 12:54 PM
 */

public class OpCoDTO extends BaseOpCoDTO {

  @NotBlank(message = "Workday Name can not be blank")
  @Length(min = 1, max = 45, message = "Length of the Workday Entity ID should be between 1 to 45")
  @ApiModelProperty(example = "Sysco Philadelphia")
  private String workdayName;

  @NotBlank(message = "Country code can not be blank")
  @Length(min = 2, max = 3, message = "Length of the Country Code should be between 2 to 3")
  @ApiModelProperty(example = "US")
  private String countryCode;

  @NotNull(message = "Market can not be blank")
  @ValidEnumFormat(enumClass = Market.class)
  @ApiModelProperty(example = "MIDWEST, MOUNTAIN_CENTRAL, NORTHEAST, PACIFIC, SOUTH, SOUTHEAST")
  private String market;

  @NotNull(message = "SAP Entity ID can not be blank")
  @ApiModelProperty(example = "1310")
  private Integer sapEntityId;

  @NotNull(message = "SUS Entity ID can not be blank")
  @ApiModelProperty(example = "75")
  private Integer susEntityId;

  @NotBlank(message = "ADP Pay Group can not be blank")
  @Length(min = 1, max = 3, message = "Length of the ADP Pay Group should be between 1 to 3")
  @ApiModelProperty(example = "S1J")
  private String adpPayGroup;

  @NotNull(message = "ADP Location ID can not be blank")
  @ApiModelProperty(example = "113")
  private Integer adpLocationId;

  @NotNull(message = "Timezone can not be blank")
  @ValidEnumFormat(enumClass = Timezone.class)
  @ApiModelProperty(example = "CENTRAL, EASTERN, MOUNTAIN, ALASKA, ARIZONA, PACIFIC, HAWAII")
  private String timezone;

  @NotNull(message = "Target Pieces Per Trip can not be blank")
  @Positive(message = "Target pieces per trip should be greater than 0 ")
  @Max(value = 5000, message = "Target pieces per trip should not be greater than 5000")
  @ApiModelProperty(example = "850")
  private Integer targetPiecesPerTrip;

  @NotNull(message = "Active status can not be blank")
  @ValidEnumFormat(enumClass = ActivationStatus.class)
  @ApiModelProperty(example = "ACTIVE, INACTIVE")
  private String activeStatus;

  public OpCoDTO() {
  }

  public OpCoDTO(OpCo opCo) {
    getModelMapper().map(opCo, this);
  }

  public OpCo merge(OpCo opCo) {
    getModelMapper().map(this, opCo);
    return opCo;
  }

  public String getWorkdayName() {
    return workdayName;
  }

  public void setWorkdayName(String workdayName) {
    this.workdayName = workdayName;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getMarket() {
    return market;
  }

  public void setMarket(String market) {
    this.market = market;
  }

  public Integer getSapEntityId() {
    return sapEntityId;
  }

  public void setSapEntityId(Integer sapEntityId) {
    this.sapEntityId = sapEntityId;
  }

  public Integer getSusEntityId() {
    return susEntityId;
  }

  public void setSusEntityId(Integer susEntityId) {
    this.susEntityId = susEntityId;
  }

  public String getAdpPayGroup() {
    return adpPayGroup;
  }

  public void setAdpPayGroup(String adpPayGroup) {
    this.adpPayGroup = adpPayGroup;
  }

  public Integer getAdpLocationId() {
    return adpLocationId;
  }

  public void setAdpLocationId(Integer adpLocationId) {
    this.adpLocationId = adpLocationId;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public Integer getTargetPiecesPerTrip() {
    return targetPiecesPerTrip;
  }

  public void setTargetPiecesPerTrip(Integer targetPiecesPerTrip) {
    this.targetPiecesPerTrip = targetPiecesPerTrip;
  }

   public String getActiveStatus() {
    return activeStatus;
  }

  public void setActiveStatus(String activeStatus) {
    this.activeStatus = activeStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OpCoDTO opCoDTO = (OpCoDTO) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(workdayName, opCoDTO.workdayName)
        .append(countryCode, opCoDTO.countryCode)
        .append(market, opCoDTO.market)
        .append(sapEntityId, opCoDTO.sapEntityId)
        .append(susEntityId, opCoDTO.susEntityId)
        .append(adpPayGroup, opCoDTO.adpPayGroup)
        .append(adpLocationId, opCoDTO.adpLocationId)
        .append(timezone, opCoDTO.timezone)
        .append(targetPiecesPerTrip, opCoDTO.targetPiecesPerTrip)
        .append(activeStatus, opCoDTO.activeStatus)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(workdayName)
        .append(countryCode)
        .append(market)
        .append(sapEntityId)
        .append(susEntityId)
        .append(adpPayGroup)
        .append(adpLocationId)
        .append(timezone)
        .append(targetPiecesPerTrip)
        .append(activeStatus)
        .toHashCode();
  }
}
