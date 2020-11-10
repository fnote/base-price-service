package com.sysco.rps.util;

import com.sysco.rps.dto.MetricsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 10. Nov 2020 06:41
 */
public class MetricsLoggerUtils {
    private static final Logger logger = LoggerFactory.getLogger("metricsLogger");

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
