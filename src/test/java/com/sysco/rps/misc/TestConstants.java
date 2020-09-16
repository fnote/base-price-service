package com.sysco.rps.misc;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 16. Sep 2020 15:46
 */
public class TestConstants {
    private TestConstants() {
        // default constructor
    }

    public static final String TEST_ENV = System.getProperty("test.env", "EXE");
    public static final String TEST_RELEASE = System.getProperty("test.release", "INITIAL_BUILD");
    public static final String TEST_PROJECT = System.getProperty("test.project", "Reference Pricing");
    public static final boolean UPDATE_DASHBOARD = Boolean.parseBoolean(System.getProperty("update.dashboard", "false"));
    public static final boolean WRITE_TO_FILE = Boolean.parseBoolean(System.getProperty("write.to.file", "false"));
    public static final String FILE_PATH = System.getProperty("file.path", "");
    public static final String NODE_NAME = System.getProperty("jenkins.node", "stag_cl2122");

    public static final String FEATURE_NAME = "UNIT_TESTING - APIUnitTests";
}
