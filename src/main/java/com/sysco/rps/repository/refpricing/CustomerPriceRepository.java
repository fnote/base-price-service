package com.sysco.rps.repository.refpricing;

import com.sysco.rps.config.PriceZoneTableConfig;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.Product;
import com.sysco.rps.util.PricingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.sysco.rps.util.PricingUtils.formatDate;
import static com.sysco.rps.util.PricingUtils.getCatchWeightIndicator;
import static com.sysco.rps.util.PricingUtils.intToString;

/**
 * Repository that provides access to customer price data from PRICE (PA) and PRICE_ZONE tables
 *
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 6/14/20. Sun 2020 18:11
 */
@org.springframework.stereotype.Repository
public class CustomerPriceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPriceRepository.class);
    
    private static final String PRICE_ZONE_TABLE_NAME_PLACEHOLDER = ":PZ_TABLE";

    private final DatabaseClient databaseClient;

    private final Map<String, PriceZoneTableConfig> priceZoneTableConfigMap;

    private String query = "SELECT" +
            "   paOuter.SUPC," +
            "   paOuter.PRICE_ZONE," +
            "   paOuter.PRICE," +
            "   paOuter.EFFECTIVE_DATE," +
            "   paOuter.EXPORTED_DATE," +
            "   paOuter.CATCH_WEIGHT_INDICATOR " +
            "FROM" +
            "   PRICE paOuter force index (`PRIMARY`) " +
            "   INNER JOIN" +
            "      (" +
            "         SELECT" +
            "            Max(paInner.EFFECTIVE_DATE) max_eff_date," +
            "            paInner.SUPC," +
            "            paInner.PRICE_ZONE " +
            "         FROM" +
            "            (" +
            "               SELECT" +
            "                  priceZoneOuter.SUPC," +
            "                  priceZoneOuter.PRICE_ZONE," +
            "                  priceZoneOuter.CUSTOMER_ID," +
            "                  priceZoneOuter.EFFECTIVE_DATE " +
            "               FROM" +
            "                  :PZ_TABLE priceZoneOuter " +
            "                  INNER JOIN" +
            "                     (" +
            "                        SELECT" +
            "                           priceZoneInner.SUPC," +
            "                           priceZoneInner.CUSTOMER_ID," +
            "                           MAX(priceZoneInner.EFFECTIVE_DATE) AS MAX_EFFECTIVE_DATE " +
            "                        FROM" +
            "                           :PZ_TABLE priceZoneInner force index (`PRIMARY`) " +
            "                        WHERE" +
            "                           priceZoneInner.CUSTOMER_ID = :customerId " +
            "                           AND priceZoneInner.EFFECTIVE_DATE <= :effectiveDate" +
            "                           AND SUPC IN (:supcs)" +
            "                        GROUP BY" +
            "                           priceZoneInner.SUPC" +
            "                     )" +
            "                     t " +
            "                     ON t.SUPC = priceZoneOuter.SUPC " +
            "                     and t.MAX_EFFECTIVE_DATE = priceZoneOuter.EFFECTIVE_DATE " +
            "                     and t.CUSTOMER_ID = priceZoneOuter.CUSTOMER_ID" +
            "            )" +
            "            pz " +
            "            INNER JOIN" +
            "               PRICE paInner force index (`PRIMARY`) " +
            "               ON pz.SUPC = paInner.SUPC " +
            "               AND pz.PRICE_ZONE = paInner.PRICE_ZONE " +
            "               AND paInner.EFFECTIVE_DATE <= :effectiveDate" +
            "         GROUP BY" +
            "            paInner.SUPC," +
            "            paInner.PRICE_ZONE" +
            "      )" +
            "      c " +
            "      ON c.SUPC = paOuter.SUPC " +
            "      AND c.PRICE_ZONE = paOuter.PRICE_ZONE " +
            "      AND c.MAX_EFF_DATE = paOuter.EFFECTIVE_DATE";

    private static final String QUERY_TO_FETCH_PRICE_FOR_PRICE_ZONE =
          "SELECT " +
                "   P_OUTER.SUPC, " +
                "   P_OUTER.PRICE_ZONE, " +
                "   P_OUTER.PRICE, " +
                "   P_OUTER.EFFECTIVE_DATE, " +
                "   P_OUTER.EXPORTED_DATE, " +
                "   P_OUTER.CATCH_WEIGHT_INDICATOR  " +
                "FROM " +
                "   PRICE P_OUTER  " +
                "   INNER JOIN " +
                "      ( " +
                "         SELECT " +
                "            SUPC, " +
                "            MAX(EFFECTIVE_DATE) MAX_EFF_DATE  " +
                "         FROM " +
                "            PRICE PRICE  " +
                "         WHERE " +
                "            SUPC IN ( :supcs ) " +
                "            AND PRICE.PRICE_ZONE = :priceZone  " +
                "            AND PRICE.EFFECTIVE_DATE <= :effectiveDate  " +
                "         GROUP BY " +
                "            SUPC  " +
                "      ) " +
                "      P_INNER  " +
                "      ON P_OUTER.SUPC = P_INNER.SUPC  " +
                "      AND P_OUTER.EFFECTIVE_DATE = P_INNER.MAX_EFF_DATE  " +
                "WHERE " +
                "   P_OUTER.PRICE_ZONE = :priceZone";

    @Autowired
    public CustomerPriceRepository(DatabaseClient databaseClient,
                                   Map<String, PriceZoneTableConfig> priceZoneTableConfigMap,
                                   @Value("${query.get.price:}") String queryToOverride) {
        this.databaseClient = databaseClient;
        this.priceZoneTableConfigMap = priceZoneTableConfigMap;

        if (!StringUtils.isEmpty(queryToOverride)) {
            this.query = queryToOverride;
        }
    }

    /**
     * Decides the price zone table name to be queried based on the price request date and
     * the effective date of the active price zone table for a given business unit
     * @param businessUnitNumber Business unit number in the price request
     * @param requestDate Price request date in the price request
     * @return either one of these PRICE_ZONE_01, PRICE_ZONE_02, PRICE_ZONE_03
     */
    private String getPriceZoneTableName(String businessUnitNumber, LocalDateTime requestDate) {
        PriceZoneTableConfig priceZoneTableConfig = priceZoneTableConfigMap.get(businessUnitNumber);
        if (requestDate.isBefore(priceZoneTableConfig.getActiveTableEffectiveDate())) {
            return priceZoneTableConfig.getHistoryTable();
        }
        return priceZoneTableConfig.getActiveTable();
    }

    private String prepareQuery(String priceZoneTableName) {
        return query.replace(PRICE_ZONE_TABLE_NAME_PLACEHOLDER, priceZoneTableName);
    }

    /**
     * Queries the DB for ref price for given supcs for a given date
     * The requested effectiveDate is in yyyMMdd format, since MySql accepts that as a valid format, we will be doing no format changes
     * Ref: https://dev.mysql.com/doc/refman/5.7/en/date-and-time-literals.html
     */
    public Flux<Product> getPricesByOpCo(CustomerPriceRequest customerPriceRequest, List<String> supcsPartition,
                                         String businessUnitNumber) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String priceZoneTableName = getPriceZoneTableName(businessUnitNumber,
                PricingUtils.convertToDate(customerPriceRequest.getPriceRequestDate()));
        String query = prepareQuery(priceZoneTableName);

        return databaseClient.execute(query)
              .bind("customerId", customerPriceRequest.getCustomerAccount())
              .bind("supcs", supcsPartition)
              .bind("effectiveDate", customerPriceRequest.getPriceRequestDate())
              .map((row, rowMetaData) -> {

                        if (stopWatch.isRunning()) {
                            stopWatch.stop();
                            LOGGER.debug("QUERY-LATENCY : [{}]", stopWatch.getLastTaskTimeMillis());
                        }

                        return new Product(row.get("SUPC", String.class),
                              intToString(row.get("PRICE_ZONE", Integer.class)),
                              row.get("PRICE", Double.class),
                              formatDate(row.get("EFFECTIVE_DATE", LocalDateTime.class)),
                              row.get("EXPORTED_DATE", Long.class),
                              getCatchWeightIndicator(row.get("CATCH_WEIGHT_INDICATOR", String.class))
                        );

                    }

              ).all();
    }


    /**
     * Queries the DB for price of a given price zone used as default price for the given list of SUPCs
     * The requested effectiveDate is in yyyMMdd format, since MySql accepts that as a valid format, we will be doing no format changes
     * Ref: https://dev.mysql.com/doc/refman/5.7/en/date-and-time-literals.html
     */
    public Flux<Product> getPricesForSpecificPriceZone(List<String> supcsPartition, String effectiveDate, String priceZone, Boolean isDefaultPrice) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        return databaseClient.execute(QUERY_TO_FETCH_PRICE_FOR_PRICE_ZONE)
              .bind("supcs", supcsPartition)
              .bind("effectiveDate", effectiveDate)
              .bind("priceZone", priceZone)
              .map((row, rowMetaData) -> {

                        if (stopWatch.isRunning()) {
                            stopWatch.stop();
                            LOGGER.debug("DEFAULT-PRICE QUERY-LATENCY : [{}]", stopWatch.getLastTaskTimeMillis());
                        }

                        return new Product(row.get("SUPC", String.class),
                              intToString(row.get("PRICE_ZONE", Integer.class)),
                              row.get("PRICE", Double.class),
                              formatDate(row.get("EFFECTIVE_DATE", LocalDateTime.class)),
                              row.get("EXPORTED_DATE", Long.class),
                              getCatchWeightIndicator(row.get("CATCH_WEIGHT_INDICATOR", String.class)),
                              isDefaultPrice
                        );

                    }

              ).all();
    }
}
