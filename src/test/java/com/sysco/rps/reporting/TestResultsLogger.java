package com.sysco.rps.reporting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.rps.reporting.model.QCenterSubmission;
import com.sysco.rps.reporting.model.TestResult;
import com.sysco.rps.reporting.model.TestResultStatus;
import com.sysco.rps.reporting.model.TestResults;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

import static com.sysco.rps.reporting.TestReportingConstants.AUTOMATION_ENDPOINT;
import static com.sysco.rps.reporting.TestReportingConstants.DESCRIPTION_IDENTIFIER;
import static com.sysco.rps.reporting.TestReportingConstants.DEVELOPER_UNIT_TEST_ID;
import static com.sysco.rps.reporting.TestReportingConstants.NODE;
import static com.sysco.rps.reporting.TestReportingConstants.SUBMISSION_NAME;
import static com.sysco.rps.reporting.TestReportingConstants.SYSCO_QCENTER_API_HOST;
import static com.sysco.rps.reporting.TestReportingConstants.TEST_ENV;
import static com.sysco.rps.reporting.TestReportingConstants.TEST_PROJECT;
import static com.sysco.rps.reporting.TestReportingConstants.TEST_RELEASE;
import static com.sysco.rps.reporting.TestReportingConstants.UPDATE_DASHBOARD;
import static com.sysco.rps.reporting.TestReportingConstants.WRITE_TO_FILE;

/**
 * Captures the unit test results and submit them to QCenter after all tests are run (per class).
 * Any submission failures would be logged but ignored and the next set of tests would be run.
 * Implements TestWatcher to get stats on the unit test life cycles.
 * <p>
 * Notes to developer:
 * Use the @Tag annotation to mention the ticket id in test methods. e.g.: @Tag("desc:PRCP-2080")
 * When no such tag is used, UNIT_DEV will be used as the ticket ID. So when a unit test is written without any reference to a JIRA ticket, there's
 * no need to add a Tag.
 * <p>
 * If you like to run the tests in a particular order, use the annotation @TestMethodOrder(MethodOrderer.OrderAnnotation.class) in the
 * class level and in the test method level add the required order with Order Annotation. e.g. @Order(1)
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 12:30
 */
public class TestResultsLogger implements TestWatcher, AfterAllCallback, BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

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
        String ticketId = getTicketID(context);
        testResultMap.put(id, new TestResult(id, ticketId, System.currentTimeMillis(), getTestName(context, ticketId)));
    }

    private String getTestName(ExtensionContext context, String ticketId) {
        String displayName = context.getDisplayName().replace("()", "");
        return StringUtils.isBlank(ticketId) ? displayName : displayName + " " + ticketId;
    }

    private String getTicketID(ExtensionContext context) {
        return context.getTags().stream()
              .filter(value -> value.contains(DESCRIPTION_IDENTIFIER))
              .findFirst()
              .map(val -> val.replace(DESCRIPTION_IDENTIFIER, ""))
              .orElse(DEVELOPER_UNIT_TEST_ID);
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
     * Invoked after a skipped test has been skipped.
     *
     * <p>The default implementation does nothing. Concrete implementations can
     * override this method as appropriate.
     *
     * @param context the current extension context; never {@code null}
     * @param reason  the reason the test is skipped; never {@code null} but
     */
    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        LOGGER.info("Test Disabled for test {}: with reason :- {}", context.getDisplayName(), reason.orElse("No reason"));
        TestResult testResult = testResultMap.get(context.getUniqueId());
        testResult.setStatus(TestResultStatus.failed);
        testResult.setErrorMessage(TestResultStatus.skipped.toString());
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
        testResultMap.get(context.getUniqueId()).setStatus(TestResultStatus.passed);
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
        TestResult testResult = testResultMap.get(context.getUniqueId());
        testResult.setStatus(TestResultStatus.failed);
        testResult.setErrorMessage(TestResultStatus.aborted.toString());
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
        TestResult testResult = testResultMap.get(context.getUniqueId());
        testResult.setStatus(TestResultStatus.failed);
        testResult.setErrorMessage(TestResultStatus.failed.toString());
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

        testResults.setEndTime(System.currentTimeMillis());
        testResults.setElapsedTime(testResults.getEndTime() - testResults.getStartTime());

        testResults.setName(SUBMISSION_NAME);
        context.getTestClass().ifPresent(value -> testResults.setTestClassName(value.toString()));

        testResultMap.values().forEach(testResult -> {
            System.out.println(testResult.toString());
            LOGGER.info(testResult.toString());
        });

        publishResults(testResults);
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

        if (WRITE_TO_FILE) {
            writeResultsToFile(testResults, submission);
        }

        LOGGER.info("QCenter Submission \n{}", submission);

        if (UPDATE_DASHBOARD) {
            sendAPIRequest(submission);
        }
    }

    private static QCenterSubmission generateQCenterSubmissionObject(TestResults testResults) {
        QCenterSubmission qCenterSubmission = new QCenterSubmission(testResults);
        qCenterSubmission.setKeyword("Suite");
        qCenterSubmission.setNode(NODE);
        qCenterSubmission.setEnv(TEST_ENV);
        qCenterSubmission.setRelease(TEST_RELEASE);
        qCenterSubmission.setProject(TEST_PROJECT);
        qCenterSubmission.setUri(testResults.getName()
              .replace("-", "")
              .replace("  ", " ")
              .replace(" ", "_"));
        qCenterSubmission.setTestClassName(testResults.getTestClassName());
        return qCenterSubmission;
    }

    private static void writeResultsToFile(TestResults testResults, String submission) {
        FileWriter file = null;

        try {
            file = new FileWriter(testResults.getName()
                  .replace("-", "")
                  .replace("  ", " ")
                  .replace(" ", "_") + ".json");
            file.write("[" + submission + "]");
        } catch (Exception e) {
            LOGGER.warn("Failed to write QCenter submission file. File {}", submission, e);
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException iox) {
                    iox.printStackTrace();
                }
            }
        }
    }

    private static void sendAPIRequest(String data) {
        String url = SYSCO_QCENTER_API_HOST + "/" + AUTOMATION_ENDPOINT;
        String response;
        try {
            response = RestUtils.sendPOST(url, data);
            LOGGER.info("QCenter submission Response :" + response);
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            LOGGER.error("Results submission to QCenter Failed", e);
        }
    }
}
