package com.sysco.rps.misc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020,
 * @doc
 * @end Created : 14. Sep 2020 14:30
 */
public class TestResult {
    @JsonIgnore
    private String id;
    @JsonIgnore
    private Long startTime;
    @JsonIgnore
    private Long endTime;
    @JsonProperty("duration")
    private Long elapsedTime;
    @JsonIgnore
    private String name;
    private TestResultStatus testResultStatus;

    TestResult(String id, Long startTime, String name) {
        this.id = id;
        this.startTime = startTime;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    Long getEndTime() {
        return endTime;
    }

    void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestResultStatus getTestResultStatus() {
        return testResultStatus;
    }

    void setTestResultStatus(TestResultStatus testResultStatus) {
        this.testResultStatus = testResultStatus;
    }

    @Override
    public String toString() {
        return "{"
              + "                        \"id\":\"" + id + "\""
              + ",                         \"startTime\":\"" + startTime + "\""
              + ",                         \"endTime\":\"" + endTime + "\""
              + ",                         \"elapsedTime\":\"" + elapsedTime + "\""
              + ",                         \"name\":\"" + name + "\""
              + ",                         \"testResultStatus\":\"" + testResultStatus + "\""
              + "}";
    }
}
