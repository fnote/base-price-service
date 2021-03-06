package com.sysco.rps.common;

/**
 * This class contains error messages and error codes used for responses
 * Refer errors.properties to see error texts matching the error codes. Always update that file when you add a new error code
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 10. Jul 2020 15:12
 */
public class Errors {

    private Errors() {
        // default constructor
    }

    public static class Messages {

        public static final String MAPPING_NOT_FOUND = "Price not found for given SUPC/customer combination. No default price found as well";
        public static final String MSG_CUSTOMER_INVALID = "Requested Customer is in invalid format";
        public static final String MSG_OPCO_NOT_FOUND = "Couldn't find a matching DB for the requested OpCo";
        public static final String CUSTOMER_NULL_OR_EMPTY = "Customer ID should not be null/empty";
        public static final String REQUESTED_OPCO_NULL_OR_EMPTY = "OpCo ID should not be null/empty";
        public static final String MSG_CUSTOMER_NOT_FOUND_ON_REQUEST = "Customer not found in the request";
        public static final String MSG_PRODUCTS_NOT_FOUND_IN_REQUEST = "Products not found in the request";
        public static final String MSG_INVALID_PRICE_REQUEST_DATE_IN_REQUEST = "Price request date is either null, empty or not in the valid format" +
              "(yyyyMMdd)";

        private Messages() {
            // default constructor
        }
    }

    public static class Codes {

        public static final String UNEXPECTED_ERROR = "101000";
        public static final String OPCO_NOT_FOUND = "102010";
        public static final String CUSTOMER_INVALID = "102012";
        public static final String MAPPING_NOT_FOUND = "102020";
        public static final String CUSTOMER_NULL_OR_EMPTY = "102030";
        public static final String REQUESTED_OPCO_NULL_OR_EMPTY = "102040";
        public static final String CUSTOMER_NOT_FOUND_ON_REQUEST = "102030";
        public static final String PRODUCTS_NOT_FOUND_IN_REQUEST = "102050";
        public static final String INVALID_PRICE_REQUEST_DATE_IN_REQUEST = "102060";

        private Codes() {
            // default constructor
        }
    }
}
