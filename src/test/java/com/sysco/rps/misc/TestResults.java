package com.sysco.rps.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 15:52
 */
public class TestResults {
    private String name;
    private Long startTime;
    private Long endTime;
    private Long elapsedTime;
    private Map<String, TestResult> testResultMap = new HashMap<>();
    private String testClassName;

    TestResults() {
        // default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Map<String, TestResult> getTestResultMap() {
        return testResultMap;
    }

    public void setTestResultMap(Map<String, TestResult> testResultMap) {
        this.testResultMap = testResultMap;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    @Override
    public String toString() {
        return "{"
              + "                        \"name\":\"" + name + "\""
              + ",                         \"startTime\":\"" + startTime + "\""
              + ",                         \"endTime\":\"" + endTime + "\""
              + ",                         \"elapsedTime\":\"" + elapsedTime + "\""
              + ",                         \"testResultMap\":" + testResultMap
              + "}";
    }
}
