package com.sysco.payplus.entity;

public interface AuditableEntity
{
    public void setUpdatedBy(String username);
    public void setUpdatedAt(Long timestamp);

}
