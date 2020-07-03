package com.sysco.rps.repository.platform;

import com.sysco.rps.entity.masterdata.BusinessUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BusinessUnitRepository {

    @Value("${active.business.units}")
    private String businessUnitsStr;

    /**
     * Constructor
     */
    @Autowired
    public BusinessUnitRepository() {
        super();
    }

    public List<BusinessUnit> getBusinessUnitList() {
        // TODO: Retrieve business units from a different source (e.g. a DB table)

        List<BusinessUnit> businessUnits = new ArrayList<>();

        String[] businessUnitIds = businessUnitsStr.split(",");

        for (String businessUnitId : businessUnitIds) {
            BusinessUnit businessUnit = new BusinessUnit(businessUnitId);
            businessUnits.add(businessUnit);
        }

        return businessUnits;
    }

}
