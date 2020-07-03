package com.sysco.rps.service.loader;


import com.sysco.rps.entity.masterdata.BusinessUnit;
import com.sysco.rps.repository.platform.BusinessUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * This class reads the list of business units from the business unit table.
 *
 * @author Rohana Kumara
 * @tag Copyright (C) 2018 SYSCO Corp. All Rights Reserved.
 */
@Component
public class BusinessUnitLoaderService {


    private final BusinessUnitRepository businessUnitRepository;

    /**
     * Constructor
     */
    @Autowired
    public BusinessUnitLoaderService(BusinessUnitRepository businessUnitRepository) {
        super();
        this.businessUnitRepository = businessUnitRepository;
    }

    /**
     * Build a list of business units
     * Then for each business unit, add the servers for it.
     * Only loads business units whose MCP flag is Y in the business unit table
     *
     * @return A list of business unit beans
     */
    public List<BusinessUnit> loadBusinessUnitList(){
        return businessUnitRepository.getBusinessUnitList();
    }

}
