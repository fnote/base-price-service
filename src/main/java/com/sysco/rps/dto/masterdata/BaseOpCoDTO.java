package com.sysco.rps.dto.masterdata;

import com.sysco.rps.validators.annotations.ValidOpCoNumberFormat;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.NotBlank;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/26/20 Time: 12:54 PM
 */

public class BaseOpCoDTO extends BaseGlobalDTO {

  @NotBlank(message = "OpCo Number can not be blank")
  @ValidOpCoNumberFormat(message = "OpCo number should have 2 letters followed by 4 digits")
  @ApiModelProperty(example = "US0075")
  protected String opCoNumber;

  public String getOpCoNumber() {
    return opCoNumber;
  }

  public void setOpCoNumber(String opCoNumber) {
    this.opCoNumber = opCoNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseOpCoDTO that = (BaseOpCoDTO) o;
    return opCoNumber.equals(that.opCoNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(opCoNumber);
  }
}
