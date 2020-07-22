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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private static final String EXPORTED_DATE_CSV_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

    public void addPARecordsFromCsv(String fileName, Boolean formatExportedDate) {
        Optional<String> bulkValues = generatePARecords(readCsv(fileName), formatExportedDate)
              .stream()
              .map(r -> "('"+r.getSupc()+"',"+r.getPriceZone()+","+r.getPrice()+",'"+r.getEffectiveDate()+"',"+r.getExportedDate()+",'"+r.getSplitIndicator()+"')")
              .reduce((a, b) -> a.concat(",").concat(b));

        bulkValues.ifPresent(v -> {
            String query = "INSERT INTO `" + Constants.DBNames.PA + "` VALUES " + v + ";";
            executeQuery(query);
        });
    }

    public void addPriceZoneRecordsFromCsv(String fileName) {
        Optional<String> bulkValues = generatePriceZoneRecords(readCsv(fileName))
              .stream()
              .map(r -> "('"+r.getSupc()+"',"+r.getPriceZone()+",'"+r.getCustomerId()+"','"+r.getEffectiveDate()+"')")
              .reduce((a, b) -> a.concat(",").concat(b));

        bulkValues.ifPresent(v -> {
            String query = "INSERT INTO `" + Constants.DBNames.PRICE_ZONE_01 + "` VALUES " + v + ";";
            executeQuery(query);
        });
    }

    private boolean executeQuery(String query) {
        AtomicBoolean isSuccess = new AtomicBoolean();
        databaseClient.execute(query)
              .map((row, rowMetaData) -> {
                  LOGGER.debug(RESULT_ROW, row);
                  return row;
              })
              .all()
              .doOnError(r -> LOGGER.error("Failed to execute query: [{}]", query, r))
              .doOnComplete(() -> {
                  LOGGER.info("Data inserted successfully");
                  isSuccess.set(true);
              })
              .subscribe();
        return isSuccess.get();
    }

    private List<List<String>> readCsv(String fileName) {
        String filePath = "test-data" + "/" + fileName;
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource[] scripts = new Resource[]{resourceLoader.getResource("classpath:".concat(filePath))};

        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(scripts[0].getFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\'", "");
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (Exception e) {
            LOGGER.error("Error reading csv", e);
        }
        records.remove(0);
        return records;
    }

    /**
     * CSV data column order should be according to this
     * SUPC,PRICE_ZONE,PRICE,EFFECTIVE_DATE,EXPORTED_DATE,SPLIT_INDICATOR
     *
     * @param csvData
     * @return List<PAData>
     */
    private List<PAData> generatePARecords(List<List<String>> csvData, Boolean formatExportedDate) {
        //Sample : '2512527','1','1.00','2020-02-01',2020-01-14 00:05:00,'p'
        return csvData
              .stream()
              .map(r -> {

                        String dateField = r.get(4);
                        long exportedDate;

                        if (formatExportedDate != null && formatExportedDate) {
                            dateField = dateField.replace("/", "-");
                            LocalDateTime dateTime = LocalDateTime.parse(dateField, DateTimeFormatter.ofPattern(EXPORTED_DATE_CSV_FORMAT));
                            exportedDate = dateTime.toEpochSecond(ZoneOffset.UTC);
                        } else {
                            exportedDate = Long.parseLong(dateField);
                        }

                        char splitIndicator = (r.size() < 6 || r.get(5) == null) ? Constants.SplitIndicators.CASE : r.get(5).charAt(0);

                        return new PAData(r.get(0), Integer.parseInt(r.get(1)), Double.parseDouble(r.get(2)),
                              r.get(3), exportedDate, splitIndicator);
                    }
              ).collect(Collectors.toList());
    }

    /**
     * CSV data column order should be according to this
     * SUPC,PRICE_ZONE,CUSTOMER_ID,EFFECTIVE_DATE
     *
     * @param csvData
     * @return List<PriceZoneData>
     */
    private List<PriceZoneData> generatePriceZoneRecords(List<List<String>> csvData) {
        return csvData
              .stream()
              .map(r -> new PriceZoneData(r.get(0), Integer.parseInt(r.get(1)), r.get(2), r.get(3)))
              .collect(Collectors.toList());
    }

}
