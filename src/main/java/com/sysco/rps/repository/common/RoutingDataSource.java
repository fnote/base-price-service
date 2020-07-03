//package com.sysco.rps.repository.common;
//
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
///**
// * This class wraps a db connection to opco specific database
// *
// * @author Rohana Kumara
// * @tag Copyright (C) 2019 SYSCO Corp. All Rights Reserved.
// */
//
//public class RoutingDataSource extends AbstractRoutingDataSource {
//
//    /**
//     * This method tell which key to use to look up the database connection
//     *
//     * @return Database name
//     */
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return DBConnectionContextHolder.getBusinessUnitDatabase();
//    }
//}
