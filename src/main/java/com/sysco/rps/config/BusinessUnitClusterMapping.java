package com.sysco.rps.config;

/**
 * Bean for holding Business Unit & Cluster Mapping information.
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/21. Thu 13:21
 */
public class BusinessUnitClusterMapping {

    private final String businessUnitNumber;
    private final String clusterId;

    public BusinessUnitClusterMapping(String businessUnitNumber, String clusterId) {
        this.businessUnitNumber = businessUnitNumber;
        this.clusterId = clusterId;
    }

    public String getBusinessUnitNumber() {
        return businessUnitNumber;
    }

    public String getClusterId() {
        return clusterId;
    }

}
