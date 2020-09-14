package com.sysco.rps.misc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 12:30
 */
public class TestResultsLogger implements TestWatcher, AfterAllCallback, BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

    @Value("${test.project}")
    private String project;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestResultsLogger.class);
    private TestResults testResults = new TestResults();
    private Map<String, TestResult> testResultMap = testResults.getTestResultMap();

    /**
     * Callback that is invoked <em>before</em> each test is invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeEach(ExtensionContext context) {
        String id = context.getUniqueId();
        testResultMap.put(id, new TestResult(id, System.currentTimeMillis(), context.getDisplayName()));
    }

    /**
     * Callback that is invoked <em>after</em> each test has been invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void afterEach(ExtensionContext context) {
        TestResult testResult = testResultMap.get(context.getUniqueId());
        testResult.setEndTime(System.currentTimeMillis());
        testResult.setElapsedTime(testResult.getEndTime() - testResult.getStartTime());
    }

    /**
     * Invoked after a disabled test has been skipped.
     *
     * <p>The default implementation does nothing. Concrete implementations can
     * override this method as appropriate.
     *
     * @param context the current extension context; never {@code null}
     * @param reason  the reason the test is disabled; never {@code null} but
     */
    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        LOGGER.info("Test Disabled for test {}: with reason :- {}", context.getDisplayName(), reason.orElse("No reason"));
        testResultMap.get(context.getUniqueId()).setTestResultStatus(TestResultStatus.DISABLED);

    }

    /**
     * Invoked after a test has completed successfully.
     *
     * <p>The default implementation does nothing. Concrete implementations can
     * override this method as appropriate.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void testSuccessful(ExtensionContext context) {
        LOGGER.info("Test Successful for test {}: ", context.getDisplayName());
        testResultMap.get(context.getUniqueId()).setTestResultStatus(TestResultStatus.PASSED);
    }

    /**
     * Invoked after a test has been aborted.
     *
     * <p>The default implementation does nothing. Concrete implementations can
     * override this method as appropriate.
     *
     * @param context the current extension context; never {@code null}
     * @param cause   the throwable responsible for the test being aborted; may be {@code null}
     */
    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        LOGGER.info("Test Aborted for test {}: ", context.getDisplayName());
        testResultMap.get(context.getUniqueId()).setTestResultStatus(TestResultStatus.ABORTED);
    }

    /**
     * Invoked after a test has failed.
     *
     * <p>The default implementation does nothing. Concrete implementations can
     * override this method as appropriate.
     *
     * @param context the current extension context; never {@code null}
     * @param cause   the throwable that caused test failure; may be {@code null}
     */
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        LOGGER.info("Test Aborted for test {}: ", context.getDisplayName());
        testResultMap.get(context.getUniqueId()).setTestResultStatus(TestResultStatus.FAILED);
    }


    /**
     * Callback that is invoked once <em>before</em> all tests in the current
     * container.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeAll(ExtensionContext context) {
        testResults.setStartTime(System.currentTimeMillis());
    }

    /**
     * Callback that is invoked once <em>after</em> all tests in the current
     * container.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void afterAll(ExtensionContext context) {
        System.out.println(project);

        testResults.setEndTime(System.currentTimeMillis());
        testResults.setElapsedTime(testResults.getEndTime() - testResults.getStartTime());

        testResults.setName(context.getDisplayName());
        context.getTestClass().ifPresent(value -> testResults.setTestClassName(value.toString()));

        testResultMap.values().forEach(testResult -> {
            System.out.println(testResult.toString());
            LOGGER.info(testResult.toString());
        });

        publishResults(testResults);
    }

    private static QCenterSubmission generateQCenterSubmissionObject(TestResults testResults) {
        QCenterSubmission qCenterSubmission = new QCenterSubmission(testResults);
        qCenterSubmission.setKeyword("Suite");
        qCenterSubmission.setNode(System.getProperty("jenkins.node", "stag_cl2122"));
        qCenterSubmission.setEnv("DEV");
        qCenterSubmission.setRelease("release");
        qCenterSubmission.setProject("project");
        qCenterSubmission.setUri(testResults.getName().replace("-", "").replace("  ", " ").replace(" ", "_"));
        qCenterSubmission.setTestClassName(testResults.getTestClassName());
        return qCenterSubmission;
    }


    private static void publishResults(TestResults testResults) {

        QCenterSubmission qCenterSubmission = generateQCenterSubmissionObject(testResults);
        ObjectMapper mapper = new ObjectMapper();
        String submission = "";
        try {
            submission = mapper.writeValueAsString(qCenterSubmission);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse JSON", e);
        }

        FileWriter file = null;

        try {
            file = new FileWriter(testResults.getName().replace("-", "").replace("  ", " ").replace(" ", "_") + ".json");
            file.write("[" + submission + "]");
        } catch (Exception var17) {
            var17.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

//        try {
//            RestUtil.API_HOST = SyscoLabCoreConstants.SYSCO_QCENTER_API_HOST;
//            RestUtil.BASE_PATH = "";
//            System.out.println("\n\nREQUEST_URL\n" + RestAssured.baseURI + RestAssured.basePath + "\n*********\n\n");
//            Response response = RestUtil.send(Headers.getHeader(), jsonReport.toString(), "Automations", RequestMethods.POST.toString());
//            System.out.println(response.asString());
//        } catch (Exception var16) {
//            var16.printStackTrace();
//        }

        System.out.println(submission);

    }
}
