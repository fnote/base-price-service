package com.sysco.rps.common;

/**
 * This class contains constants to the whole reference pricing API
 * <p>
 * Copyright (C) 2015 SYSCO Corp. All Rights Reserved.
 */
public class Constants {

    public static final String CORRELATION_ID_HEADER_KEY = "X-Syy-Correlation-Id";
    public static final String CLIENT_ID_HEADER_KEY = "client-id";
    public static final String CORRELATION_ID_KEY = "Correlation-Id";
    public static final String ROUTING_KEY = "businessUnitId";
    public static final String PRICE_REQUEST_DATE_PATTERN = "yyyyMMdd";
    public static final String IS_CATCH_WEIGHT = "Y";
    public static final String DEFAULT_PRICE_ZONE = "3";
    public static final String LOGGER_NAME = "LoggerForEvents";

    private Constants() {
        // default constructor
    }

    public static class FieldsLength {
        public static final int SUPC_NUMBER = 7;
        public static final int SUPC = 9;
        public static final int CUSTOMER_NUMBER_OVERALL = 14;
        public static final int CUSTOMER_NUMBER = 6;
        public static final int OPCO_ID_OVERALL = 5;
        public static final int OPCO_NUMBER = 3;

        private FieldsLength() {
            // default constructor
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
            // default constructor
        }
    }

    public static class JdbcProperties {
        public static final String PRICINGDB = "REF_PRICE_";

        private JdbcProperties() {
            // default constructor
        }
    }


    public static class DBNames {
        public static final String PA = "PRICE";
        public static final String PRICE_ZONE_01 = "PRICE_ZONE_01";

        private DBNames() {
            // default constructor
        }
    }

    public static class SplitIndicators {
        public static final Character CASE = 'c';
        public static final Character POUND = 'p';

        private SplitIndicators() {
            // default constructor
        }
    }

}
