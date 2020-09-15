package com.sysco.rps.reporting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to store a single test result's info.
 * Used as a QCenter Submission request's result object as well.
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020,
 * @doc
 * @end Created : 14. Sep 2020 14:30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestResult {
    @JsonIgnore
    private String id;

    @JsonProperty("id")
    private String ticketId;

    @JsonIgnore
    private Long startTime;

    @JsonIgnore
    private Long endTime;

    @JsonProperty("duration")
    private Long elapsedTime;

    @JsonIgnore
    private String name;

    private TestResultStatus status;

    @JsonProperty("error_message")
    private String errorMessage;

    public TestResult(String id, String ticketId, Long startTime, String name) {
        this.id = id;
        this.ticketId = ticketId;
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

    public void setElapsedTime(Long elapsedTimeInMs) {
        // converting time as done in the qe-core library. Reason not known
        if (elapsedTimeInMs >= 0L) {
            this.elapsedTime = (elapsedTimeInMs + 5000L) * 1000L * 1000L;
        }
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestResultStatus getStatus() {
        return status;
    }

    public void setStatus(TestResultStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    public String toString() {
        return "{"
              + "                        \"id\":\"" + id + "\""
              + ",                         \"ticketId\":\"" + ticketId + "\""
              + ",                         \"startTime\":\"" + startTime + "\""
              + ",                         \"endTime\":\"" + endTime + "\""
              + ",                         \"elapsedTime\":\"" + elapsedTime + "\""
              + ",                         \"name\":\"" + name + "\""
              + ",                         \"status\":\"" + status + "\""
              + ",                         \"errorMessage\":\"" + errorMessage + "\""
              + "}";
    }
}
