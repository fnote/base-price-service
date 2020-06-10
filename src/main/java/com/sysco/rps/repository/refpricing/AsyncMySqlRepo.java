package com.sysco.rps.repository.refpricing;

import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.mysql.MySQLConnection;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.sysco.rps.dto.refpricing.CustomerPrice;
import com.sysco.rps.dto.refpricing.CustomerPriceReqDTO;
import org.apache.commons.collections4.ListUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AsyncMySqlRepo {

    @Autowired
    @Qualifier("jaSqlDataSource")
    ConnectionPool<MySQLConnection> jaSqlDataSource;

    private static Logger LOGGER = LoggerFactory.getLogger(AsyncMySqlRepo.class);

    public String getPricesForRandomValues() {

        String summary = "";

        List<String> currentSUPCList = new ArrayList<>();
        Random r = new Random();
        String customerId = "" + r.nextInt(7700);


        String query1 = getQuery(currentSUPCList, customerId, 4);
        String query2 = getQuery(currentSUPCList, customerId, 4);
        String query3 = getQuery(currentSUPCList, customerId, 4);
        String query4 = getQuery(currentSUPCList, customerId, 4);
        String query5 = getQuery(currentSUPCList, customerId, 16);

        try {

            long singleStart = System.currentTimeMillis();

            CompletableFuture<QueryResult> future5 = jaSqlDataSource.sendPreparedStatement(query5);
            future5.get();

            long singleEnd = System.currentTimeMillis();

            long multiStart = System.currentTimeMillis();

            CompletableFuture<QueryResult> future1 = jaSqlDataSource.sendPreparedStatement(query1);
            CompletableFuture<QueryResult> future2 = jaSqlDataSource.sendPreparedStatement(query2);
            CompletableFuture<QueryResult> future3 = jaSqlDataSource.sendPreparedStatement(query3);
            CompletableFuture<QueryResult> future4 = jaSqlDataSource.sendPreparedStatement(query4);

            CompletableFuture<Void> combinedFuture
                  = CompletableFuture.allOf(future1, future2, future3, future4);


            combinedFuture.get();

            long multiEnd = System.currentTimeMillis();


            summary = "Multi : " + (multiEnd - multiStart);
            summary = summary + "\nSingle : " + (singleEnd - singleStart);


        } catch (Exception e) {
            LOGGER.error("Failed to fetch data using queries with random values", e);
        }

        return summary;
    }

    public List<CustomerPrice> getPrices(CustomerPriceReqDTO customerPriceReqDTO, Integer supcsPerQuery) {

        List<String> supcs = customerPriceReqDTO.getSupcs();
        if (supcsPerQuery == null || supcsPerQuery == 0) {
            supcsPerQuery = supcs.size();
        }

        List<CustomerPrice> customerPriceList = null;

        try {

            if (supcs.size() <= supcsPerQuery) {
                long singleStart = System.currentTimeMillis();

                String query = getQuery(customerPriceReqDTO.getCustomerId(), supcs);
                CompletableFuture<QueryResult> future = jaSqlDataSource.sendPreparedStatement(query);
                QueryResult queryResult = future.get();

                long singleEnd = System.currentTimeMillis();

                LOGGER.info("[LATENCY] SINGLE query execution: [{}]", (singleEnd - singleStart));

                ResultSet rows = queryResult.getRows();

                customerPriceList = rows.stream().map(row -> {
                    String supc = row.getString("SUPC");
                    String priceZone = row.getString("PRICE_ZONE");
                    String customerId = row.getString("CUSTOMER_ID");
                    Double price = row.getDouble("PRICE");
                    Date date = localDateToDate(row.getDate("EFFECTIVE_DATE"));
                    return new CustomerPrice(supc, priceZone, customerId, price, date);
                }).collect(Collectors.toList());

            } else {
                List<List<String>> partitionedLists = ListUtils.partition(supcs, supcsPerQuery);
                CompletableFuture<QueryResult>[] futureArray = new CompletableFuture[partitionedLists.size()];

                long multiStart = System.currentTimeMillis();

                for (int i = 0; i < partitionedLists.size(); i++) {

                    String query = getQuery(customerPriceReqDTO.getCustomerId(), partitionedLists.get(i));
                    futureArray[i] = jaSqlDataSource.sendPreparedStatement(query);
                }


                CompletableFuture<Void> combinedFuture
                      = CompletableFuture.allOf(futureArray);

                combinedFuture.get();

                long multiEnd = System.currentTimeMillis();

                LOGGER.info("[LATENCY] MULTI query execution: [{}]", (multiEnd - multiStart));

                customerPriceList = Stream.of(futureArray)
                      .map(CompletableFuture::join)
                      .map(queryResult -> convertQueryResultsToCustomerPrice(queryResult.getRows()))
                      .flatMap(List::stream)
                      .collect(Collectors.toList());

            }


        } catch (Exception e) {
            LOGGER.error("Failed to fetch data using queries", e);
        }


        return customerPriceList;
    }

    @NotNull
    private List<CustomerPrice> convertQueryResultsToCustomerPrice(ResultSet rows) {
        return rows.stream().map(row -> {
            String supc = row.getString("SUPC");
            String priceZone = row.getString("PRICE_ZONE");
            String customerId = row.getString("CUSTOMER_ID");
            Double price = row.getDouble("PRICE");
            LocalDateTime effectiveDate = row.getDate("EFFECTIVE_DATE");
            Date date = localDateToDate(effectiveDate);
            return new CustomerPrice(supc, priceZone, customerId, price, date);
        }).collect(Collectors.toList());
    }

    // from  LocalDate to java.sql.Date:
    private static java.sql.Date localDateToDate(LocalDateTime ld) {
        return new Date(ld.toDateTime().getMillis());
    }

    private String getQuery(List<String> currentSUPCList, String customerId, int countToGenerate) {
        List<String> supcList = getRandomValues(89000, currentSUPCList, countToGenerate);

        return getQuery(customerId, supcList);
    }

    private String getQuery(String customerId, List<String> supcList) {
        String supcs = supcList.stream().map(str -> "\"" + str + "\"").collect(Collectors.joining(","));

        return "SELECT c.CUSTOMER_ID, p.SUPC, p.PRICE_ZONE, p.PRICE, p.EFFECTIVE_DATE " +
              "FROM " +
              "PA_HIS p INNER JOIN " +
              "(SELECT MAX(p.EFFECTIVE_DATE) max_eff_date, p.SUPC, p.PRICE_ZONE, b.CUSTOMER_ID " +
              "FROM " +
              "(SELECT e.SUPC, e.PRICE_ZONE, e.CUSTOMER_ID from EATS_001 e where e.CUSTOMER_ID= \"" + customerId + "\" and SUPC in (" + supcs + ") " +
              ") b " +
              "INNER JOIN " +
              "PA_HIS p ON b.SUPC = p.SUPC AND b.PRICE_ZONE = p.PRICE_ZONE " +
              "WHERE p.EFFECTIVE_DATE <= \"2021-12-12\" " +
              "GROUP BY p.SUPC) " +
              "c " +
              "ON c.max_eff_date = p.EFFECTIVE_DATE AND c.SUPC = p.SUPC AND c.PRICE_ZONE = p.PRICE_ZONE " +
              "ORDER BY c.CUSTOMER_ID, SUPC " +
              ";";
    }

    private List<String> getRandomValues(int max, List<String> currentList, int countToGenerate) {
        List<String> newList = new ArrayList<>();
        Random r = new Random();

        while (newList.size() < countToGenerate) {
            int generatedVal = r.nextInt(max);
            String strGeneratedVal = "" + generatedVal;
            if (!currentList.contains(strGeneratedVal)) {
                currentList.add(strGeneratedVal);
                newList.add(strGeneratedVal);
            }
        }

        return newList;
    }
}
