package com.sysco.rps.config;

import com.sysco.rps.entity.masterdata.BusinessUnit;
import com.sysco.rps.service.loader.BusinessUnitLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuration class for initializing ClusterInfo bean.
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/21. Thu 13:00
 */
@Profile("!test")
@Configuration
public class ClusterInfoConfigInitializer {

    private static final String OPCO_CLUSTER_MAPPINGS_QUERY = "SELECT * FROM OPCO_CLUSTER WHERE OPCO_ID IN (:businessUnits)";
    private static final String CLUSTER_INFO_QUERY = "SELECT * FROM CLUSTER_INFO";
    private static final String CLUSTER_ID_COLUMN_NAME = "CLUSTER_ID";
    private static final String OPCO_ID_COLUMN_NAME = "OPCO_ID";
    private static final String READER_ENDPOINT_COLUMN_NAME = "READER_ENDPOINT";
    private static final String WRITER_ENDPOINT_COLUMN_NAME = "WRITER_ENDPOINT";

    private final CommonDBClientConfig commonDBClientConfig;

    private final BusinessUnitLoaderService businessUnitLoaderService;

    @Autowired
    public ClusterInfoConfigInitializer(CommonDBClientConfig commonDBClientConfig,
                                        BusinessUnitLoaderService businessUnitLoaderService) {
        this.commonDBClientConfig = commonDBClientConfig;
        this.businessUnitLoaderService = businessUnitLoaderService;
    }

    @Bean
    public ClusterInfo initClusterInfo() {
        List<BusinessUnit> businessUnitList = businessUnitLoaderService.loadBusinessUnitList();
        List<String> businessUnits = businessUnitList
                .stream()
                .map(BusinessUnit::getBusinessUnitNumber)
                .collect(Collectors.toList());

        Map<String, BusinessUnitClusterMapping> mappingMap = commonDBClientConfig.getDatabaseClient()
                .execute(OPCO_CLUSTER_MAPPINGS_QUERY)
                .bind("businessUnits", businessUnits)
                .map(row -> new BusinessUnitClusterMapping(row.get(OPCO_ID_COLUMN_NAME, String.class),
                                row.get(CLUSTER_ID_COLUMN_NAME, String.class)
                        )
                )
                .all()
                .collectMap(BusinessUnitClusterMapping::getBusinessUnitNumber)
                .block();

        Map<String, Cluster> clusterMap = commonDBClientConfig.getDatabaseClient()
                .execute(CLUSTER_INFO_QUERY)
                .map(row -> new Cluster(row.get(CLUSTER_ID_COLUMN_NAME, String.class),
                                row.get(READER_ENDPOINT_COLUMN_NAME, String.class),
                                row.get(WRITER_ENDPOINT_COLUMN_NAME, String.class)
                        )
                )
                .all()
                .collectMap(Cluster::getId)
                .block();

        return new ClusterInfo(clusterMap, mappingMap);
    }
}
