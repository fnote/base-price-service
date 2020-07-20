package com.sysco.rps.repository;

import com.sysco.rps.common.Constants;
import com.sysco.rps.entity.PAData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicBoolean;

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

    @Autowired
    private DatabaseClient databaseClient;

    public boolean truncateTables() {
        return truncateTable(Constants.DBNames.PA) && truncateTable(Constants.DBNames.PRICE_ZONE_01);
    }

    private boolean truncateTable(String tableName) {
        String truncateTableQuery = "TRUNCATE TABLE " + tableName;
        AtomicBoolean isSuccess = new AtomicBoolean(false);

        databaseClient.execute(truncateTableQuery)
              .map((row, rowMetaData) -> {
                  LOGGER.debug("Result row {}", row.toString());
                  return row;
              })
              .all()
              .doOnError(r -> {
                  LOGGER.error("Failed to execute query", r);
                  isSuccess.set(false);
              })
              .subscribe();

        return isSuccess.get();
    }

    public boolean addPARecord(PAData paData) {
        String query = "INSERT INTO `PA` VALUES (:supc, :priceZone, :price, :effectiveFrom, :exportedDate, :splitIndicator);";

        AtomicBoolean isSuccess = new AtomicBoolean(false);

        databaseClient.execute(query)
              .bind("supc", paData.getSupc())
              .bind("priceZone", paData.getPriceZone())
              .bind("price", paData.getPrice())
              .bind("effectiveFrom", paData.getEffectiveDate())
              .bind("exportedDate", paData.getExportedDate())
              .bind("splitIndicator", String.valueOf(paData.getSplitIndicator()))
              .map((row, rowMetaData) -> {
                  LOGGER.debug("Result row {}", row.toString());
                  return row;
              })
              .all()
              .doOnError(r -> {
                  LOGGER.error("Failed to execute query", r);
                  isSuccess.set(false);
              })
              .subscribe();
        return isSuccess.get();
    }


}
