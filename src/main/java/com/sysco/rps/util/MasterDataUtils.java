package com.sysco.rps.util;

import com.sysco.rps.common.Constants;
import com.sysco.rps.config.PriceZoneMasterDataRecord;
import com.sysco.rps.config.PriceZoneTableConfig;
import com.sysco.rps.entity.masterdata.BusinessUnit;

import java.util.Map;
import java.util.Objects;

/**
 * @author Tharuka Jayalath
 * @copyright (C) 2021, Sysco Corporation
 * @end Created : 09/14/2021. Tue 13.37
 */
public class MasterDataUtils {

    private MasterDataUtils() {}

    public static PriceZoneTableConfig constructPriceZoneTableConfig(BusinessUnit businessUnit,
                                                              Map<String, PriceZoneMasterDataRecord> masterDataRecordMap) {
        Objects.requireNonNull(masterDataRecordMap);
        PriceZoneMasterDataRecord active = masterDataRecordMap.get(Constants.DBNames.PRICE_ZONE_TABLE_TYPE_ACTIVE);
        PriceZoneMasterDataRecord history = masterDataRecordMap.get(Constants.DBNames.PRICE_ZONE_TABLE_TYPE_HISTORY);
        String historyTableName = history != null ? history.getTableName() : active.getTableName();
        return new PriceZoneTableConfig(businessUnit.getBusinessUnitNumber(), active.getTableName(),historyTableName,
                active.getEffectiveDate());
    }
}
