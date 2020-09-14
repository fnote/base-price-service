package com.sysco.rps.misc;

import com.google.common.base.CaseFormat;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 18:12
 */
public class QCenterSubmission {
    private List<QCenterSubmissionElement> elements;
    private String name;
    private String id;
    private String keyword;
    private String description;
    private Long duration;
    private String testClassName;
    private String node;
    private String env;
    private String release;
    private String project;
    private String uri;

    QCenterSubmission(TestResults testResults) {
        this.elements = testResults.getTestResultMap().values().stream().map(
              testResult -> {
                  QCenterSubmissionStep qCenterSubmissionStep = new QCenterSubmissionStep(testResult);
                  return new QCenterSubmissionElement(testResult.getName(), Collections.singletonList(qCenterSubmissionStep));
              }
        ).collect(Collectors.toList());


        this.duration = testResults.getElapsedTime();

        this.name = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, testResults.getName());
        this.id = UUID.randomUUID().toString().split("-")[0];

        this.description = testResults.getName();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QCenterSubmissionElement> getElements() {
        return elements;
    }

    public void setElements(List<QCenterSubmissionElement> elements) {
        this.elements = elements;
    }
}
