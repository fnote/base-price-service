package com.sysco.rps.controller.refpricing;

import com.sysco.rps.dto.pp.masterdata.CustomerDTO;
import com.sysco.rps.dto.refpricing.CustomerPrice;
import com.sysco.rps.dto.refpricing.CustomerPriceReqDTO;
import com.sysco.rps.entity.refpricing.EATS;
import com.sysco.rps.entity.refpricing.EATSIdentifier;
import com.sysco.rps.entity.refpricing.PA;
import com.sysco.rps.repository.refpricing.EATSRepository;
import com.sysco.rps.repository.refpricing.CustomerPriceRepository;
import com.sysco.rps.repository.refpricing.PARepository;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.exception.ValidationException;
import com.sysco.rps.validators.annotations.ValidOpCoNumberFormat;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class RefPricingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefPricingController.class);


    @Autowired
    private PARepository paRepository;

    @Autowired
    private EATSRepository eatsRepository;

    @Autowired
    private CustomerPriceRepository customerPriceRepository;


    @GetMapping("/prices")
    @ApiOperation(value = "Returns the customer record identified by the parameters", notes = "Returns the customer record identified by the parameters", response = CustomerDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Customer found"),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
    })
    public @ResponseBody
    ResponseEntity<PA> findPA()
            throws RecordNotFoundException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paRepository.findById(1).orElse(new PA()));
    }

    @GetMapping("/eats")
    @ApiOperation(value = "Returns the customer record identified by the parameters", notes = "Returns the customer record identified by the parameters", response = CustomerDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Customer found"),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
    })
    public @ResponseBody
    ResponseEntity<EATS> findEATS()
            throws RecordNotFoundException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eatsRepository.findById(new EATSIdentifier("1", "1")).orElse(new EATS()));
    }

    @PostMapping("/customerprice")
    @ApiOperation(
          value = "Gets the reference pricing for the customer item",
          notes = "Gets the reference pricing for the customer item",
          response = CustomerDTO.class)
    @ApiResponses(value = {
          @ApiResponse(code = org.apache.http.HttpStatus.SC_CREATED, message = "Customer created."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_CONFLICT, message = "Customer already exists."),
    })
    public @ResponseBody
    ResponseEntity<List<CustomerPrice>> getCustomerPrice(@RequestBody @Valid CustomerPriceReqDTO customerPriceReqDTO)
          throws ValidationException {
        LOGGER.info("Entered the customer handler:createCustomer");
        List<CustomerPrice> customerPriceList = customerPriceRepository.getCustomerPrice(customerPriceReqDTO);
        return ResponseEntity.status(HttpStatus.OK)
              .body(customerPriceList);
    }

}
