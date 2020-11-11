package com.sysco.rps.util;

import com.sysco.rps.dto.MetricsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sysco.rps.common.Constants.METRICS_LOGGER;

/**
 * Util class that contains methods for logging metrics logs
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 10. Nov 2020 06:41
 */
public class MetricsLoggerUtils {
    private static final Logger logger = LoggerFactory.getLogger(METRICS_LOGGER);

    private MetricsLoggerUtils() {
        // default constructor
    }

    /**
     * Log the metrics with info log level
     * */
    public static void logInfo(MetricsEvent metricsEvent) {
        logger.info("{}", metricsEvent);
    }

    /**
     * Log the metrics with error log level
     * */
    public static void logError(MetricsEvent metricsEvent) {
        logger.error("{}", metricsEvent);
    }
}
