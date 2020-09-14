package com.sysco.rps.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 18:17
 */
public class QCenterSubmissionElement {
    private String name;
    private List<QCenterSubmissionStep> steps = new ArrayList<>();

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

    public QCenterSubmissionElement(String name, List<QCenterSubmissionStep> steps) {
        this.name = name;
        this.steps = steps;
    }
}
