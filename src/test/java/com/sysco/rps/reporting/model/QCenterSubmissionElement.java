package com.sysco.rps.reporting.model;

import java.util.List;

/**
 * Represents a QCenter Submission request's element object.
 * Object order: QCenterSubmission >> QCenterSubmissionElement >> QCenterSubmissionStep >> TestResult
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 18:17
 */
public class QCenterSubmissionElement {
    private String name;
    private List<QCenterSubmissionStep> steps;
    private String type = "scenario";
    private String keyword = "Test Case";

    QCenterSubmissionElement(String name, List<QCenterSubmissionStep> steps) {
        this.name = name;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QCenterSubmissionStep> getSteps() {
        return steps;
    }

    public void setSteps(List<QCenterSubmissionStep> steps) {
        this.steps = steps;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "{"
              + "                        \"name\":\"" + name + "\""
              + ",                         \"steps\":" + steps
              + ",                         \"type\":\"" + type + "\""
              + ",                         \"keyword\":\"" + keyword + "\""
              + "}";
    }
}
