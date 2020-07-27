package com.sysco.rps.dto.pp.masterdata;

import com.sysco.rps.dto.pp.Constant;
import com.sysco.rps.entity.pp.masterdata.FiscalCalendar;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/6/20 Time: 12:54 PM
 */

public class FiscalCalendarDTO extends BaseOpCoDTO implements Serializable {

  @NotNull(message = "Id can not be blank")
  @ApiModelProperty(example = "1")
  private Long id;

  @NotBlank(message = "Cal date can not be blank")
  @ApiModelProperty(example = "12-23-2020")
  private String calDate;

  @NotBlank(message = "Dow can not be blank")
  @ApiModelProperty(example = "Mon")
  private String dow;

  @NotNull(message = "Fiscal week number can not be blank")
  @ApiModelProperty(example = "50")
  private int fwNumber;

  @NotNull(message = "Fiscal year number can not be blank")
  @ApiModelProperty(example = "2020")
  private int fyNumber;


  @ApiModelProperty(example = "MM-dd-yyyy")
  private String dateFormat = Constant.DATE_FORMAT;

  public FiscalCalendarDTO(FiscalCalendar fiscalCalendar) {
    getModelMapper().map(fiscalCalendar, this);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public String getCalDate() {
    return calDate;
  }

  public void setCalDate(String calDate) {
    this.calDate = calDate;
  }

  public String getDow() {
    return dow;
  }

  public void setDow(String dow) {
    this.dow = dow;
  }

  public int getFwNumber() {
    return fwNumber;
  }

  public void setFwNumber(int fwNumber) {
    this.fwNumber = fwNumber;
  }

  public int getFyNumber() {
    return fyNumber;
  }

  public void setFyNumber(int fyNumber) {
    this.fyNumber = fyNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FiscalCalendarDTO that = (FiscalCalendarDTO) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(fwNumber, that.fwNumber)
        .append(fyNumber, that.fyNumber)
        .append(id, that.id)
        .append(calDate, that.calDate)
        .append(dow, that.dow)
        .append(dateFormat, that.dateFormat)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(id)
        .append(calDate)
        .append(dow)
        .append(fwNumber)
        .append(fyNumber)
        .append(dateFormat)
        .toHashCode();
  }
}
