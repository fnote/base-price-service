package com.sysco.rps.common;

/**
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

        private Messages() {
            // default constructor
        }

        public static final String MAPPING_NOT_FOUND = "Price not found for given SUPC/customer combination";
        public static final String MSG_CUSTOMER_INVALID = "Requested Customer is in invalid format";
        public static final String MSG_OPCO_NOT_FOUND = "Couldn't find a matching DB for the requested OpCo";
    }

    public static class Codes {

        private Codes() {
            // default constructor
        }

        public static final String UNEXPECTED_ERROR = "10100";

        public static final String OPCO_NOT_FOUND = "102010";
        public static final String CUSTOMER_INVALID = "102012";
        public static final String MAPPING_NOT_FOUND = "102020";
    }
}
