package com.sysco.rps.dto.refpricing;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 10. Jun 2020 16:52
 */
public class ExecTime {

    private Long single;
    private Long multi;

    public ExecTime(){}

    public ExecTime(Long single, Long multi) {
        this.single = single;
        this.multi = multi;
    }

    public Long getSingle() {
        return single;
    }

    public void setSingle(Long single) {
        this.single = single;
    }

    public Long getMulti() {
        return multi;
    }

    public void setMulti(Long multi) {
        this.multi = multi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ExecTime)) return false;

        ExecTime execTime = (ExecTime) o;

        return new EqualsBuilder()
              .append(single, execTime.single)
              .append(multi, execTime.multi)
              .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
              .append(single)
              .append(multi)
              .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
              .append("SINGLE", single)
              .append("MULTI", multi)
              .toString();
    }

}
