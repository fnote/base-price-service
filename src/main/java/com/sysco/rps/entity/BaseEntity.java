package com.sysco.rps.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Super class having common fields for all entities . updatedby,aupdatedat Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/19/20
 * Time: 12:54 PM
 */

@MappedSuperclass
//this makes sure of nothing has change, nothing will be saved.
@DynamicUpdate
public abstract class BaseEntity implements AuditableEntity, Serializable {

    @Column(name = "opco_number", nullable = false, length = 45)
    protected String opCoNumber;
    @Column(name = "saved_by", nullable = false, length = 45)
    protected String savedBy;
    @Column(name = "saved_time", nullable = false)
    protected Long savedTime;
    //indicates if the record is locked for modifications

    public String getSavedBy() {
        return savedBy;
    }

    @Override
    public void setSavedBy(String username) {
        this.savedBy = username;
    }

    public Long getSavedTime() {
        return savedTime;
    }

    @Override
    public void setSavedTime(Long timestamp) {
        this.savedTime = timestamp;
    }

    public String getOpCoNumber() {
        return opCoNumber;
    }

    public void setOpCoNumber(String opCoNumber) {
        this.opCoNumber = opCoNumber;
    }
}
