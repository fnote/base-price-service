package com.sysco.rps.service.refpricing;

import com.sysco.rps.dto.refpricing.CustomerPrice;
import com.sysco.rps.dto.refpricing.CustomerPriceRequest;
import com.sysco.rps.repository.common.DBConnectionContextHolder;
import com.sysco.rps.repository.refpricing.CustomerPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 18. Jun 2020 16:00
 */

@Service
public class CustomerPriceService {

    @Autowired
    private CustomerPriceRepository customerPriceRepository;

    public CustomerPrice getCustomerPrice(CustomerPriceRequest customerPriceRequest) {

        DBConnectionContextHolder.setBusinessUnitDatabaseInstance(customerPriceRequest.getBusinessUnitNumber());

        return customerPriceRepository.getCustomerPrice(customerPriceRequest);
    }
}
