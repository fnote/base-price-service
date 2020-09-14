package com.sysco.rps.misc;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020,
 * @doc
 * @end Created : 14. Sep 2020 14:30
 */
public class TestResult {
    private String id;
    private Long startTime;
    private Long endTime;
    private Long elapsedTime;
    private String name;
    private TestResultStatus testResultStatus;

    public TestResult(){
        // default constructor
    }

    public TestResult(String id, Long startTime, String name) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestResultStatus getTestResultStatus() {
        return testResultStatus;
    }

    public void setTestResultStatus(TestResultStatus testResultStatus) {
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
