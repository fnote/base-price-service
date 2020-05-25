package com.sysco.rps.entity.security;

import java.util.Collection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/22/20 Time: 12:54 PM
 */

public class UsernameOpCoNumberAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String opCoNumber;

    public UsernameOpCoNumberAuthenticationToken(Object principal, String opCoNumber) {
        super(principal, null);
        this.opCoNumber = opCoNumber;
    }

    public UsernameOpCoNumberAuthenticationToken(Object principal, String opCoNumber, Collection<? extends GrantedAuthority> authorities) {
        super(principal, null, authorities);
        this.opCoNumber = opCoNumber;
    }

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

        if (!(o instanceof UsernameOpCoNumberAuthenticationToken)) {
            return false;
        }

        UsernameOpCoNumberAuthenticationToken that = (UsernameOpCoNumberAuthenticationToken) o;

        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(opCoNumber, that.opCoNumber)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .appendSuper(super.hashCode())
            .append(opCoNumber)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("opCoNumber", opCoNumber)
            .toString();
    }
}
