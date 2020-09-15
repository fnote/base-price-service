package com.sysco.rps.reporting.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * Used to store test result info of all tests of a class.
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
    private Map<String, TestResult> testResultMap = new LinkedHashMap<>();
    private String testClassName;

    public TestResults() {
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

    Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTimeInMs) {
        // converting time as done in the qe-core library. Reason not known
        if (elapsedTimeInMs >= 0L) {
            this.elapsedTime = (elapsedTimeInMs + 5000L) * 1000L * 1000L;
        }
    }

    public Map<String, TestResult> getTestResultMap() {
        return testResultMap;
    }

    public void setTestResultMap(SortedMap<String, TestResult> testResultMap) {
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
              + ",                         \"testClassName\":\"" + testClassName + "\""
              + "}";
    }
}
