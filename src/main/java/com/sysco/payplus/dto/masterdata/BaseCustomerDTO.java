package com.sysco.payplus.dto.masterdata;

import com.sysco.payplus.entity.masterdata.enums.StopAttribute;
import com.sysco.payplus.entity.masterdata.enums.StopClass;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class BaseCustomerDTO extends BaseGlobalDTO {

    @ApiModelProperty(example = "REGULAR")
    private StopClass stopClass;

    @ApiModelProperty(example = "[\"STAIRS\", \"LONG_WALK\"]")
    private List<StopAttribute> stopAttributes;

    public StopClass getStopClass() {
        return stopClass;
    }

    public void setStopClass(StopClass stopClass) {
        this.stopClass = stopClass;
    }

    public List<StopAttribute> getStopAttributes() {
        return stopAttributes;
    }

    public void setStopAttributes(List<StopAttribute> stopAttributes) {
        this.stopAttributes = stopAttributes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("stopClass", stopClass)
                .append("stopAttributes", stopAttributes)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BaseCustomerDTO that = (BaseCustomerDTO) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getStopClass(), that.getStopClass())
                .append(getStopAttributes(), that.getStopAttributes())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getStopClass())
                .append(getStopAttributes())
                .toHashCode();
    }
}
