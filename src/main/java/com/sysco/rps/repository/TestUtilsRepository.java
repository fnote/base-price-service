package com.sysco.rps.repository;

import com.sysco.rps.common.Constants;
import com.sysco.rps.entity.PAData;
import com.sysco.rps.entity.PriceZoneData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Contains some util methods that are only used during unit testing
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 20. Jul 2020 10:50
 */
@Repository
public class TestUtilsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtilsRepository.class);
    private static final String RESULT_ROW = "Result row {}";

    @Autowired
    private DatabaseClient databaseClient;

    public void truncateTables() {
        truncateTable(Constants.DBNames.PA);
        truncateTable(Constants.DBNames.PRICE_ZONE_01);
    }

    private void truncateTable(String tableName) {
        String truncateTableQuery = "TRUNCATE TABLE " + tableName;
        databaseClient.execute(truncateTableQuery)
              .map((row, rowMetaData) -> {
                  LOGGER.debug(RESULT_ROW, row);
                  return row;
              })
              .all()
              .doOnError(r -> {
                  LOGGER.error("Failed to execute query", r);
              })
              .subscribe();
    }

    public boolean addPARecord(PAData paData) {
        String query = "INSERT INTO `" + Constants.DBNames.PA + "` VALUES (:supc, :priceZone, :price, :effectiveDate, :exportedDate, " +
              ":splitIndicator);";

        AtomicBoolean isSuccess = new AtomicBoolean(false);

        databaseClient.execute(query)
              .bind("supc", paData.getSupc())
              .bind("priceZone", paData.getPriceZone())
              .bind("price", paData.getPrice())
              .bind("effectiveDate", paData.getEffectiveDate())
              .bind("exportedDate", paData.getExportedDate())
              .bind("splitIndicator", String.valueOf(paData.getSplitIndicator()))
              .map((row, rowMetaData) -> {
                  LOGGER.debug(RESULT_ROW, row);
                  return row;
              })
              .all()
              .doOnError(r -> {
                  LOGGER.error("Failed to execute query: [{}]", query, r);
                  isSuccess.set(false);
              })
              .subscribe();
        return isSuccess.get();
    }

    public boolean addPriceZoneRecord(PriceZoneData priceZoneData) {
        String query = "INSERT INTO `" + Constants.DBNames.PRICE_ZONE_01 + "` VALUES (:supc, :priceZone, :customerId, :effectiveDate);";

        AtomicBoolean isSuccess = new AtomicBoolean(false);

        databaseClient.execute(query)
              .bind("supc", priceZoneData.getSupc())
              .bind("priceZone", priceZoneData.getPriceZone())
              .bind("customerId", priceZoneData.getCustomerId())
              .bind("effectiveDate", priceZoneData.getEffectiveDate())
              .map((row, rowMetaData) -> {
                  LOGGER.debug(RESULT_ROW, row);
                  return row;
              })
              .all()
              .doOnError(r -> {
                  LOGGER.error("Failed to execute query: [{}]", query, r);
                  isSuccess.set(false);
              })
              .subscribe();
        return isSuccess.get();
    }

    public void addPARecordsFromCsv(String fileName) {
        generatePARecords(readCsv(fileName))
              .forEach(this::addPARecord);
    }

    public void addPriceZoneRecordsFromCsv(String fileName) {
        generatePriceZoneRecords(readCsv(fileName))
              .forEach(this::addPriceZoneRecord);
    }

    private List<List<String>> readCsv(String fileName) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource[] scripts = new Resource[]{resourceLoader.getResource("classpath:".concat(fileName))};

        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(scripts[0].getFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (Exception e) {
            LOGGER.error("Error reading csv", e);
        }
        records.remove(0);
        return records;
    }

    private List<PAData> generatePARecords(List<List<String>> csvData) {
        return csvData
              .stream()
              .map(r -> new PAData(r.get(0), Integer.parseInt(r.get(1)), Double.parseDouble(r.get(2)),
                    r.get(3), Long.parseLong(r.get(4)), r.get(5).charAt(0))
              ).collect(Collectors.toList());
    }

    private List<PriceZoneData> generatePriceZoneRecords(List<List<String>> csvData) {
        return csvData
              .stream()
              .map(r -> new PriceZoneData(r.get(0), Integer.parseInt(r.get(1)), r.get(2), r.get(3)))
              .collect(Collectors.toList());
    }

}
