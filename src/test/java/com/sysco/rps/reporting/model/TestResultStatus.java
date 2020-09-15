package com.sysco.rps.reporting.model;

/**
 * Test Result statuses
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020,
 * @doc
 * @end Created : 14. Sep 2020 14:31
 */
public enum TestResultStatus {
    passed("Passed"),
    failed("Failed"),
    aborted("Aborted"),
    skipped("Skipped");

    private final String value;

    TestResultStatus(final String status) {
        this.value = status;
    }

    @Override
    public String toString() {
        return value;
    }
}
