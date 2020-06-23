package com.sysco.rps.repository.refpricing;

import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.mysql.MySQLConnection;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.sysco.rps.dto.ResponseWrapper;
import com.sysco.rps.dto.refpricing.CustomerPriceReqDTO;
import com.sysco.rps.dto.refpricing.CustomerPriceSimplified;
import com.sysco.rps.dto.refpricing.ExecTime;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AsyncMySqlRepo {

    @Autowired
    @Qualifier("jAsyncDataSource")
    ConnectionPool<MySQLConnection> jaSqlDataSource;

    private static Logger LOGGER = LoggerFactory.getLogger(AsyncMySqlRepo.class);

    public String getPricesForRandomValues() {

        String summary = "";

        List<String> currentSUPCList = new ArrayList<>();
        Random r = new Random();
        String customerId = "" + r.nextInt(7700);

        String query1 = getQuery(currentSUPCList, customerId, 4, "");
        String query2 = getQuery(currentSUPCList, customerId, 4, "");
        String query3 = getQuery(currentSUPCList, customerId, 4, "");
        String query4 = getQuery(currentSUPCList, customerId, 4, "");
        String query5 = getQuery(currentSUPCList, customerId, 16, "");

        try {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            CompletableFuture<QueryResult> future5 = jaSqlDataSource.sendPreparedStatement(query5);
            future5.get();

            stopWatch.stop();

            long single = stopWatch.getLastTaskTimeMillis();
            LOGGER.info("[LATENCY] SINGLE query execution: [{}]", stopWatch.getLastTaskTimeMillis());


            stopWatch.start();

            CompletableFuture<QueryResult> future1 = jaSqlDataSource.sendPreparedStatement(query1);
            CompletableFuture<QueryResult> future2 = jaSqlDataSource.sendPreparedStatement(query2);
            CompletableFuture<QueryResult> future3 = jaSqlDataSource.sendPreparedStatement(query3);
            CompletableFuture<QueryResult> future4 = jaSqlDataSource.sendPreparedStatement(query4);

            CompletableFuture<Void> combinedFuture
                  = CompletableFuture.allOf(future1, future2, future3, future4);


            combinedFuture.get();
            stopWatch.stop();

            long multi = stopWatch.getLastTaskTimeMillis();
            LOGGER.info("[LATENCY] MULTI query execution: [{}]", stopWatch.getLastTaskTimeMillis());

            summary = "Multi : " + (multi);
            summary = summary + "\nSingle : " + (single);


        } catch (Exception e) {
            LOGGER.error("Failed to fetch data using queries with random values", e);
        }

        return summary;
    }

    public ResponseWrapper<List<CustomerPriceSimplified>> getRandomPricesCustom(Integer supcsCount, Integer supcsPerQuery) {
        supcsCount = (supcsCount == null || supcsCount == 0) ? 16 : supcsCount;
        supcsPerQuery = (supcsPerQuery == null || supcsPerQuery == 0) ? 4 : supcsPerQuery;

        List<String> currentSUPCList = new ArrayList<>();
        Random r = new Random();
        String customerId = "" + r.nextInt(7700);
        List<CustomerPriceSimplified> customerPriceList = null;
        ExecTime execTime = new ExecTime();
        StopWatch stopWatch = new StopWatch();

        try {

            if (supcsCount <= supcsPerQuery) {

                String query = getQuery(currentSUPCList, customerId, 16, "");
                customerPriceList = getCustomerPricesThroughSingleQuery(query, execTime, stopWatch);

            } else {

                List<String> supcList = getRandomValues(89000, currentSUPCList, supcsCount);
                customerPriceList = getCustomPricesThroughMultipleQueries(customerId, supcsPerQuery, supcList, execTime, stopWatch, "");
            }

        } catch (Exception e) {
            LOGGER.error("Failed to retrieve custom prices", e);
        }

        return formResponse(customerPriceList, execTime);
    }

    private ResponseWrapper<List<CustomerPriceSimplified>> formResponse(List<CustomerPriceSimplified> customerPrices, ExecTime execTime) {
        Map<String, String> metadataMap = new HashMap<>();

        if (execTime.getSingle() != null) {
            metadataMap.put("singleQueryExecTime", execTime.getSingle() + " ms");
        }

        if (execTime.getMulti() != null) {
            metadataMap.put("multiQueryExecTime", execTime.getMulti() + " ms");
        }

        return new ResponseWrapper<>(customerPrices, metadataMap);
    }

    public List<CustomerPriceSimplified> getPrices(CustomerPriceReqDTO customerPriceReqDTO, Integer supcsPerQuery) {
        ExecTime execTime = new ExecTime();

        List<String> supcs = customerPriceReqDTO.getSupcs();
        if (supcsPerQuery == null || supcsPerQuery == 0) {
            supcsPerQuery = supcs.size();
        }

        List<CustomerPriceSimplified> customerPriceList = null;
        StopWatch stopWatch = new StopWatch();

        try {

            if (supcs.size() <= supcsPerQuery) {
                String query = getQuery(customerPriceReqDTO.getCustomerId(), supcs, customerPriceReqDTO.getEffectiveDate());
                customerPriceList = getCustomerPricesThroughSingleQuery(query, execTime, stopWatch);

            } else {
                customerPriceList = getCustomPricesThroughMultipleQueries(customerPriceReqDTO.getCustomerId(), supcsPerQuery, supcs, execTime,
                      stopWatch, customerPriceReqDTO.getEffectiveDate());
            }


        } catch (Exception e) {
            LOGGER.error("Failed to fetch data using queries", e);
        }


        return customerPriceList;
    }

    private String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return dtf.format(now);
    }

    private List<CustomerPriceSimplified> getCustomPricesThroughMultipleQueries(String customerId, Integer supcsPerQuery,
                                                                                List<String> supcs, ExecTime execTime, StopWatch stopWatch,
                                                                                String effectiveDate) throws InterruptedException,
          java.util.concurrent.ExecutionException {

        List<CustomerPriceSimplified> customerPriceList;

        List<List<String>> partitionedLists = ListUtils.partition(supcs, supcsPerQuery);
        CompletableFuture<QueryResult>[] futureArray = new CompletableFuture[partitionedLists.size()];

        stopWatch.start();

        for (int i = 0; i < partitionedLists.size(); i++) {
            String query = getQuery(customerId, partitionedLists.get(i), effectiveDate);
            futureArray[i] = jaSqlDataSource.sendPreparedStatement(query);
        }

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futureArray);

        combinedFuture.get();

        stopWatch.stop();
        long executionTime = stopWatch.getLastTaskTimeMillis();
        execTime.setMulti(executionTime);

        LOGGER.info("[LATENCY] MULTI query execution: [{}]", stopWatch.getLastTaskTimeMillis());

        customerPriceList = Stream.of(futureArray)
              .map(CompletableFuture::join)
              .map(queryResult -> convertQueryResultsToCustomerPrice(queryResult.getRows()))
              .flatMap(List::stream)
              .collect(Collectors.toList());
        return customerPriceList;
    }

    private List<CustomerPriceSimplified> getCustomerPricesThroughSingleQuery(String query, ExecTime execTime, StopWatch stopWatch) throws InterruptedException,
          java.util.concurrent.ExecutionException {
        List<CustomerPriceSimplified> customerPriceList;
        stopWatch.start();

        CompletableFuture<QueryResult> future = jaSqlDataSource.sendPreparedStatement(query);
        QueryResult queryResult = future.get();

        stopWatch.stop();

        long executionTime = stopWatch.getLastTaskTimeMillis();
        execTime.setSingle(executionTime);

        LOGGER.info("[LATENCY] SINGLE query execution: [{}]", stopWatch.getLastTaskTimeMillis());

        ResultSet rows = queryResult.getRows();

        customerPriceList = rows.stream().map(row -> {
            String supc = row.getString("SUPC");
            String priceZone = row.getString("PRICE_ZONE");
            String customerId = row.getString("CUSTOMER_ID");
            Double price = row.getDouble("PRICE");
            Date date = localDateToDate(row.getDate("EFFECTIVE_DATE"));
            return new CustomerPriceSimplified(supc, priceZone, customerId, price, date);
        }).collect(Collectors.toList());
        return customerPriceList;
    }

    @NotNull
    private List<CustomerPriceSimplified> convertQueryResultsToCustomerPrice(ResultSet rows) {
        return rows.stream().map(row -> {
            String supc = row.getString("SUPC");
            String priceZone = row.getString("PRICE_ZONE");
            String customerId = row.getString("CUSTOMER_ID");
            Double price = row.getDouble("PRICE");
            LocalDateTime effectiveDate = row.getDate("EFFECTIVE_DATE");
            Date date = localDateToDate(effectiveDate);
            return new CustomerPriceSimplified(supc, priceZone, customerId, price, date);
        }).collect(Collectors.toList());
    }

    // from  LocalDate to java.sql.Date:
    private static java.sql.Date localDateToDate(LocalDateTime ld) {
        return new Date(ld.toDateTime().getMillis());
    }

    private String getQuery(List<String> currentSUPCList, String customerId, int countToGenerate, String effectiveDate) {
        List<String> supcList = getRandomValues(89000, currentSUPCList, countToGenerate);

        return getQuery(customerId, supcList, effectiveDate);
    }

    private String getQuery(String customerId, List<String> supcList, String effectiveDate) {

        String maxEffectiveDate = StringUtils.isEmpty(effectiveDate) ? getCurrentDate() : effectiveDate;

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
              "WHERE p.EFFECTIVE_DATE <= \"" + maxEffectiveDate + "\" " +
              "GROUP BY p.SUPC) " +
              "c " +
              "ON c.max_eff_date = p.EFFECTIVE_DATE AND c.SUPC = p.SUPC AND c.PRICE_ZONE = p.PRICE_ZONE " +
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
