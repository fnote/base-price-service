package com.sysco.rps.misc;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 18:00
 */
public class TestConstants {
    public static final String TEST_ENV = System.getProperty("test.env", "QA");
    public static final String TEST_RELEASE = System.getProperty("test.release", "INITIAL_BUILD");
    public static final String TEST_PROJECT = System.getProperty("test.project", "Reference Pricing");
    public static final boolean UPDATE_DASHBOARD = Boolean.parseBoolean(System.getProperty("update.dashboard", "false"));
    public static final String EMAIL_RECIPIENT = System.getProperty("email.recipient", "sanjaya.amarasinghe@syscolabs.com");
    public static final String QCENTER_FEATURE = "feature";
}
