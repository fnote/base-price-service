package com.sysco.rps.config;

import java.util.Map;

/**
 * Bean for holding Business Units, Cluster Mappings, and Cluster Info.
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/21. Thu 14:00
 */
public class ClusterInfo {

    private final Map<String, Cluster> clusterInfoMap;
    private final Map<String, BusinessUnitClusterMapping> businessUnitClusterMap;

    public ClusterInfo(Map<String, Cluster> clusterInfoMap, Map<String, BusinessUnitClusterMapping> businessUnitClusterMap) {
        this.clusterInfoMap = clusterInfoMap;
        this.businessUnitClusterMap = businessUnitClusterMap;
    }

    public Cluster getClusterByBusinessUnitId(String businessUnitNumber) {
        BusinessUnitClusterMapping mapping = businessUnitClusterMap.get(businessUnitNumber);
        if (mapping != null) {
            return clusterInfoMap.get(mapping.getClusterId());
        }
        return null;
    }
}
