package com.sysco.rps.repository.refpricing;

import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tharuka Jayalath
 * (C) 2019, Sysco Labs
 * Created: 6/14/20. Sun 2020 18:11
 */
@org.springframework.stereotype.Repository
public class CustomerPriceRepository {

    @Autowired
    private DatabaseClient databaseClient;

    private Logger LOGGER = LoggerFactory.getLogger(CustomerPriceRepository.class);

    private String getQuery(String customerId, String effectiveDate, String supcs) {

        String q2 = "SELECT paOuter.SUPC," +
              "       paOuter.PRICE_ZONE," +
              "       paOuter.PRICE," +
              "       paOuter.EFFECTIVE_DATE," +
              "       paOuter.EXPORTED_DATE" +
              " FROM PA paOuter force index (`PRIMARY`)" +
              "         INNER JOIN (SELECT Max(paInner.EFFECTIVE_DATE) max_eff_date," +
              "                            paInner.SUPC," +
              "                            paInner.PRICE_ZONE" +
              "                     FROM (SELECT e.SUPC," +
              "                                  e.PRICE_ZONE," +
              "                                  e.CUSTOMER_ID" +
              "                           FROM PRICE_ZONE_01 e force index (`PRIMARY`)" +
              "                           WHERE e.CUSTOMER_ID = \"" + customerId + "\"" +
              "                             AND SUPC IN (" + supcs + ")) pz" +
              "                              INNER JOIN PA paInner force index (`PRIMARY`)" +
              "                                         ON pz.SUPC = paInner.SUPC" +
              "                                             AND pz.PRICE_ZONE = paInner.PRICE_ZONE" +
              "                                             AND paInner.EFFECTIVE_DATE <= \"" + effectiveDate + "\"" +
              "                     GROUP BY paInner.SUPC, paInner.PRICE_ZONE) c" +
              "                    ON c.SUPC = paOuter.SUPC AND c.PRICE_ZONE = paOuter.PRICE_ZONE AND" +
              "                       c.MAX_EFF_DATE = paOuter.EFFECTIVE_DATE";

//        LOGGER.debug(q2);
        return q2;
    }

    public Flux<Product> getPricesByOpCo(CustomerPriceRequest customerPriceRequest, List<String> supcsPartition) {
        String supcs = getSUPCs(supcsPartition);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        return databaseClient.execute(getQuery(customerPriceRequest.getCustomerAccount(), customerPriceRequest.getPriceRequestDate(), supcs))
              .map((row, rowMetaData) -> {

                        if (stopWatch.isRunning()) {
                            stopWatch.stop();
                            LOGGER.debug("QUERY-LATENCY : [{}]", stopWatch.getLastTaskTimeMillis());
                        }

                        return new Product(row.get("SUPC", String.class),
                              row.get("PRICE_ZONE", Integer.class),
                              row.get("PRICE", Double.class),
                              getDate(row.get("EFFECTIVE_DATE", LocalDateTime.class)),
                                    row.get("EXPORTED_DATE", Long.class)
                        );

                    }

              ).all();
    }

    private String getSUPCs(List<String> supcs) {
        return supcs
              .stream()
              .map(s -> "\"" + s + "\"")
              .collect(Collectors.joining(","));
    }

    private String getDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
}
