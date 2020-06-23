package com.sysco.rps.common;

/**
 * This class provides constants platform to all components of cloud pricing
 * <p>
 * Copyright (C) 2015 SYSCO Corp. All Rights Reserved.
 */
public class Constants {


    private Constants() {
    }


    public static final long PRICINGDB_MAXAGE_LOWER_LIMIT_DEFAULT = 180000;
    public static final long PRICINGDB_MAXAGE_UPPER_LIMIT_DEFAULT = 300000;

    public static class FieldsLength {
        public static final int SUPC_NUMBER = 7;
        public static final int SUPC = 9;
        public static final int CUSTOMER_NUMBER_OVERALL = 14;
        public static final int CUSTOMER_NUMBER = 6;
        public static final int OPCO_ID_OVERALL = 5;
        public static final int OPCO_NUMBER = 3;

        private FieldsLength() {

        }
    }

    /**
     * Values to match to the "service_environment" parameter
     */
    public static class EnvironmentValue {
        public static final String SERVER_ENVIRONMENT_VARIABLE = "SERVER_ENVIRONMENT_VARIABLE";
        public static final String DEVELOPMENT = "DEV";
        public static final String TUNING = "STG";
        public static final String EXE = "EXE";
        public static final String QUALITY = "QA";
        public static final String PRODUCTION = "PROD";

        private EnvironmentValue() {

        }
    }

    public static class JdbcProperties {
        public static final String JDBC_DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
        public static final String HIKARI_POOL_NAME_SUFFIX = "-hikari-pool";

        public static final String JDBC_MYSQL = "jdbc:mysql://";
        public static final String PORT = ":3306";
        public static final String PRICINGDB = "/REF_PRICE_";

        public static final String NETWORK_ADDRESS = "networkAddress";
        public static final String USER_ID = "userId";
        public static final String PASSWORD = "password";

        private JdbcProperties() {
        }
    }


    public static class DBNames {
        public static final String PA = "PA";
        public static final String EATS = "EATS";

        private DBNames() {
        }
    }

}
