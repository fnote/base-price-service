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

        String q2 = "SELECT pr.EXPORTED_DATE, pr.SUPC, pr.PRICE, pr.PRICE_ZONE, pr.EFFECTIVE_DATE" +
              "                     FROM PA pr force index (pa_ix_supc_eff_date)" +
              "                              INNER JOIN (SELECT max(pp.EFFECTIVE_DATE) max_eff, pp.SUPC, pp.PRICE_ZONE" +
              "                                          FROM PA pp force index (pa_ix_supc_eff_date)" +
              "                                                   INNER JOIN (SELECT ea.SUPC, ea.CUSTOMER_ID, ea.PRICE_ZONE, ea.EFFECTIVE_DATE" +
              "                                                               FROM EATS ea force index (PRIMARY)" +
              "                                                                        INNER JOIN (SELECT MAX(e.EFFECTIVE_DATE) eat_max_eff, e.SUPC, e.CUSTOMER_ID" +
              "                                                                                    FROM EATS e force index (PRIMARY)" +
              "                                                                                    WHERE e.CUSTOMER_ID = \""+customerId+"\" " +
              "                                                                                      AND e.SUPC IN" +
              "                                                                                          ("+supcs+")" +
              "                                                                                      AND e.EFFECTIVE_DATE <= \""+effectiveDate+"\" " +
              "                                                                                    GROUP BY e.SUPC" +
              "                                                                                    ORDER BY NULL) aa" +
              "                                                               WHERE ea.CUSTOMER_ID = aa.CUSTOMER_ID" +
              "                                                                 AND ea.SUPC = aa.SUPC" +
              "                                                                 AND ea.EFFECTIVE_DATE = aa.eat_max_eff) bb" +
              "                                                              ON pp.SUPC = bb.SUPC" +
              "                                                                  AND bb.PRICE_ZONE = pp.PRICE_ZONE" +
              "                                                                  AND pp.EFFECTIVE_DATE <= \""+effectiveDate+"\" " +
              "                                          GROUP BY pp.SUPC" +
              "                                          ORDER BY NULL) cc" +
              "                     WHERE pr.SUPC = cc.SUPC" +
              "                       AND pr.PRICE_ZONE = cc.PRICE_ZONE" +
              "                       AND pr.EFFECTIVE_DATE = cc.max_eff";

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


//    private String getSUPCs(PricingRequestBody body) {
//        return body.getSupcs()
//              .stream()
//              .map(s -> "\"" + s + "\"")
//              .collect(Collectors.joining(","));
//    }

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
