package com.sysco.rps.misc;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 14. Sep 2020 12:30
 */
public class TestResultsLogger implements TestWatcher, AfterAllCallback, BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestResultsLogger.class);
    private TestResultWrapper testResultWrapper = new TestResultWrapper();
    private Map<String, TestResult> testResultMap = testResultWrapper.getTestResultMap();

    /**
     * Callback that is invoked <em>before</em> each test is invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        String id = context.getUniqueId();
        testResultMap.put(id, new TestResult(id, System.currentTimeMillis(), context.getDisplayName()));
    }

    /**
     * Callback that is invoked <em>after</em> each test has been invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
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
        testResultMap.get(context.getUniqueId()).setTestResultStatus(TestResultStatus.SUCCESSFUL);
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
    public void beforeAll(ExtensionContext context) throws Exception {
        testResultWrapper.setStartTime(System.currentTimeMillis());

    }

    /**
     * Callback that is invoked once <em>after</em> all tests in the current
     * container.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {

        testResultWrapper.setEndTime(System.currentTimeMillis());
        testResultWrapper.setElapsedTime(testResultWrapper.getEndTime() - testResultWrapper.getStartTime());

        testResultMap.values().forEach(testResult ->  {
            System.out.println(testResult.toString());
            LOGGER.info(testResult.toString());
        });
    }
}
