package com.sysco.rps.controller.refpricing;

import com.sysco.rps.dto.refpricing.CustomerPrice;
import com.sysco.rps.dto.refpricing.CustomerPriceReqDTO;
import com.sysco.rps.dto.refpricing.CustomerPriceRequest;
import com.sysco.rps.dto.refpricing.CustomerPriceSimplified;
import com.sysco.rps.repository.refpricing.CustomerPriceJDBCTemplateRepository;
import com.sysco.rps.service.refpricing.CustomerPriceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 04. Jun 2020 12:05
 */
@RestController
@RequestMapping("/ref-price/")
public class CustomerPriceController {


    @Autowired
    private CustomerPriceService customerPriceService;

    @Autowired
    private CustomerPriceJDBCTemplateRepository customerPriceJDBCTemplateRepository;

    @PostMapping("/customer-prices")
    @ApiOperation(
          value = "Gets the reference pricing for the customer item",
          notes = "If effectiveDate is provided in the req body, that will be used as the max effective date." +
                "Else current day will be used. Uses NamedJDBCTemplate to fetch data",
          response = CustomerPrice.class)
    @ApiResponses(value = {
          @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Data fetch successful"),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Request validation failed."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer/OpCo not found."),
    })
    public @ResponseBody
    ResponseEntity<CustomerPrice> getCustomerPrice(@RequestBody @Valid CustomerPriceRequest customerPriceRequest) {
        CustomerPrice customerPrice = customerPriceService.getCustomerPrice(customerPriceRequest);
        return ResponseEntity.status(HttpStatus.OK)
              .body(customerPrice);
    }

    @PostMapping("/customer-price-with-date")
    @ApiOperation(
          value = "Gets the reference pricing for the customer item",
          notes = "If effectiveDate is provided in the req body, that will be used as the max effective date." +
                "Else current day will be used. Uses NamedJDBCTemplate to fetch data",
          response = CustomerPriceSimplified.class)
    @ApiResponses(value = {
          @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Data fetch successful"),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Request validation failed."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer/OpCo not found."),
    })
    public @ResponseBody
    ResponseEntity<List<CustomerPriceSimplified>> getCustomerPriceWithDate(@RequestBody @Valid CustomerPriceReqDTO customerPriceReqDTO) {
//        LOGGER.info("Entered the ref price handler:getCustomerPriceWithDate");
        List<CustomerPriceSimplified> customerPriceList = customerPriceJDBCTemplateRepository.getCustomerPrice2(customerPriceReqDTO);
        return ResponseEntity.status(HttpStatus.OK)
              .body(customerPriceList);
    }

}
