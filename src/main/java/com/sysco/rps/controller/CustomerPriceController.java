package com.sysco.rps.controller;

import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.service.CustomerPriceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 03. Jul 2020 10:02
 */
@RestController
@RequestMapping("/ref-price/")
public class CustomerPriceController {


    @Autowired
    private CustomerPriceService customerPriceService;

    @PostMapping("/customer-prices")
    @ApiOperation(
          value = "Gets the reference pricing for the customer item",
          notes = "If effectiveDate is provided in the req body, that will be used as the max effective date." +
                "Else current day will be used. Uses NamedJDBCTemplate to fetch data",
          response = CustomerPriceResponse.class)
    @ApiResponses(value = {
          @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Data fetch successful"),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Request validation failed."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer/OpCo not found."),
    })
    public @ResponseBody
    Mono<CustomerPriceResponse> getCustomerPrices(@RequestBody CustomerPriceRequest request, @RequestParam(required = false) Integer supcsPerQuery) {
        return customerPriceService.pricesByOpCo(request, supcsPerQuery);
    }
}
