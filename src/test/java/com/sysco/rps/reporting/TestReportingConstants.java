package com.sysco.rps.reporting;

/**
 * Constants for Unit Test Reporting
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 18:00
 */
class TestReportingConstants {
    static final String TEST_ENV = System.getProperty("test.env", "EXE");
    static final String TEST_RELEASE = System.getProperty("test.release", "INITIAL_BUILD");
    static final String TEST_PROJECT = System.getProperty("test.project", "Reference Pricing");
    static final String NODE = System.getProperty("jenkins.node", "stag_cl2122");

    static final boolean UPDATE_DASHBOARD = Boolean.parseBoolean(System.getProperty("update.dashboard", "false"));
    static final boolean WRITE_TO_FILE = false;

    static final String SYSCO_QCENTER_API_HOST = System.getProperty("qcenter.url", "https://syscoqcenter.sysco.com:3000");
    static final String AUTOMATION_ENDPOINT = "automations";

    static final String DESCRIPTION_IDENTIFIER = "desc:";
    static final String DEVELOPER_UNIT_TEST_ID = "UNIT_DEV";
    static final String SUBMISSION_NAME = "UNIT_TESTING - APIUnitTests";
}
