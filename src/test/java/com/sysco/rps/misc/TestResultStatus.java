package com.sysco.rps.misc;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020,
 * @doc
 * @end Created : 14. Sep 2020 14:31
 */
public enum TestResultStatus {
    PASSED("passed"),
    FAILED("failed"),
    ABORTED("aborted"),
    DISABLED("disabled");

    private final String value;
    TestResultStatus(final String status) {
        this.value = status;
    }

    @Override
    public String toString() {
        return value;
    }
}
