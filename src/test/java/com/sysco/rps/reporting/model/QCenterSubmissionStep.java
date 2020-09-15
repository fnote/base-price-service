package com.sysco.rps.reporting.model;

/**
 * Represents a QCenter Submission request's step object.
 * Object order: QCenterSubmission >> QCenterSubmissionElement >> QCenterSubmissionStep >> TestResult
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 18:36
 */
public class QCenterSubmissionStep {
    private TestResult result;

    QCenterSubmissionStep(TestResult result) {
        this.result = result;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "{"
              + "                        \"result\":" + result
              + "}";
    }
}
