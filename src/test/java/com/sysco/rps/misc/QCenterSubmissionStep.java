package com.sysco.rps.misc;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 18:36
 */
public class QCenterSubmissionStep {
    TestResult result;

    QCenterSubmissionStep(TestResult result) {
        this.result = result;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }
}
