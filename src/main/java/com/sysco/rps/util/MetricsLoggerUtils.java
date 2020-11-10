package com.sysco.rps.util;

import com.sysco.rps.dto.MetricsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sysco.rps.common.Constants.LOGGER_NAME;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 10. Nov 2020 06:41
 */
public class MetricsLoggerUtils {
    private static final Logger logger = LoggerFactory.getLogger(LOGGER_NAME);

    private MetricsLoggerUtils() {
        // default constructor
    }

    public static void logInfo(MetricsEvent metricsEvent) {
        logger.info("{}", metricsEvent);
    }


    public static void logError(MetricsEvent metricsEvent) {
        logger.error("{}", metricsEvent);
    }
}
