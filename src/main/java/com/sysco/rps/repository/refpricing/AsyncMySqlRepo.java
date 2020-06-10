package com.sysco.rps.repository.refpricing;

import com.github.jasync.sql.db.Configuration;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.ConnectionPoolConfiguration;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.sysco.rps.dto.refpricing.CustomerPriceReqDTO;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class AsyncMySqlRepo {

    private static Logger LOGGER = LoggerFactory.getLogger(AsyncMySqlRepo.class);

    private static String host = "reference-db-mysql-cluster-instance-1-us-east-1b.c6xai0tt38eb.us-east-1.rds.amazonaws.com";
    private static int port = 3306;
    private static String db = "reference_pricing";
    private static String username = "admin";
    private static String password = "gvt12345";

    public String getPricesForRandomValues() {

        String summary = "";

        ConnectionPoolConfiguration poolConfiguration = new ConnectionPoolConfiguration(
              host,
              port,
              db,
              username,
              password,
              100,
              TimeUnit.MINUTES.toMillis(15)
        );

        MySQLConnectionFactory mySQLConnectionFactory = new MySQLConnectionFactory(new Configuration(
              username,
              host,
              port,
              password,
              db
        ));

        List<String> currentSUPCList = new ArrayList<>();
        Random r = new Random();
        String customerId = "" + r.nextInt(7700);


        String query1 = getQuery(currentSUPCList, customerId, 4);
        String query2 = getQuery(currentSUPCList, customerId, 4);
        String query3 = getQuery(currentSUPCList, customerId, 4);
        String query4 = getQuery(currentSUPCList, customerId, 4);
        String query5 = getQuery(currentSUPCList, customerId, 16);
        String query6 = getQuery(currentSUPCList, customerId, 16);


        Connection connection = new ConnectionPool<>(mySQLConnectionFactory, poolConfiguration);

        try {
            connection.connect().get();

            CompletableFuture<QueryResult> future6 = connection.sendPreparedStatement(query6);
            future6.get();

            long singleStart = System.currentTimeMillis();

            CompletableFuture<QueryResult> future5 = connection.sendPreparedStatement(query5);
            future5.get();

            long singleEnd = System.currentTimeMillis();

            long multiStart = System.currentTimeMillis();

            CompletableFuture<QueryResult> future1 = connection.sendPreparedStatement(query1);
            CompletableFuture<QueryResult> future2 = connection.sendPreparedStatement(query2);
            CompletableFuture<QueryResult> future3 = connection.sendPreparedStatement(query3);
            CompletableFuture<QueryResult> future4 = connection.sendPreparedStatement(query4);

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


    public String getPrices(CustomerPriceReqDTO customerPriceReqDTO, Integer supcsPerQuery) {

        List<String> supcs = customerPriceReqDTO.getSupcs();
        if (supcsPerQuery == null || supcsPerQuery == 0) {
            supcsPerQuery = supcs.size();
        }

        String summary = "";

        ConnectionPoolConfiguration poolConfiguration = new ConnectionPoolConfiguration(
              host,
              port,
              db,
              username,
              password,
              100,
              TimeUnit.MINUTES.toMillis(15)
        );

        MySQLConnectionFactory mySQLConnectionFactory = new MySQLConnectionFactory(new Configuration(
              username,
              host,
              port,
              password,
              db
        ));


        Connection connection = new ConnectionPool<>(mySQLConnectionFactory, poolConfiguration);

        try {

            connection.connect().get();

            if (supcs.size() <= supcsPerQuery) {
                long singleStart = System.currentTimeMillis();

                String query = getQuery(customerPriceReqDTO.getCustomerId(), supcs);
                CompletableFuture<QueryResult> future = connection.sendPreparedStatement(query);
                future.get();

                long singleEnd = System.currentTimeMillis();

                summary = summary + "Single : " + (singleEnd - singleStart);
            } else {
                List<List<String>> partitionedLists = ListUtils.partition(supcs, supcsPerQuery);
                CompletableFuture<QueryResult>[] futureArray = new CompletableFuture[partitionedLists.size()];

                long multiStart = System.currentTimeMillis();

                for (int i = 0; i < partitionedLists.size(); i++) {

                    String query = getQuery(customerPriceReqDTO.getCustomerId(), partitionedLists.get(i));
                    futureArray[i] = connection.sendPreparedStatement(query);
                }


                CompletableFuture<Void> combinedFuture
                      = CompletableFuture.allOf(futureArray);

                combinedFuture.get();

                long multiEnd = System.currentTimeMillis();

                summary = "Multi : " + (multiEnd - multiStart);

            }


        } catch (Exception e) {
            LOGGER.error("Failed to fetch data using queries", e);
        }


        return summary;
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
