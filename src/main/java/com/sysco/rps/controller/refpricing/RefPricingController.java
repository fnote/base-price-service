package com.sysco.rps.controller.refpricing;

import com.sysco.rps.dto.pp.masterdata.CustomerDTO;
import com.sysco.rps.dto.refpricing.CustomerPrice;
import com.sysco.rps.dto.refpricing.CustomerPriceReqDTO;
import com.sysco.rps.repository.refpricing.AsyncMySqlRepo;
import com.sysco.rps.repository.refpricing.CustomerPriceJDBCTemplateRepository;
import com.sysco.rps.repository.refpricing.CustomerPriceRepository;
import com.sysco.rps.repository.refpricing.EATSRepository;
import com.sysco.rps.repository.refpricing.PARepository;
import com.sysco.rps.service.exception.ValidationException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private CustomerPriceJDBCTemplateRepository customerPriceJDBCTemplateRepository;

    @Autowired
    private AsyncMySqlRepo asyncMySqlRepo;

//
//    @GetMapping("/prices")
//    @ApiOperation(value = "Returns the customer record identified by the parameters", notes = "Returns the customer record identified by the parameters", response = CustomerDTO.class)
//    @ApiResponses(value = {
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Customer found"),
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
//    })
//    public @ResponseBody
//    ResponseEntity<PA> findPA()
//          throws RecordNotFoundException {
//        LOGGER.info("Entered the ref price handler:findPA");
//        return ResponseEntity.status(HttpStatus.OK)
//              .body(paRepository.findById(1).orElse(new PA()));
//    }
//
//    @GetMapping("/eats")
//    @ApiOperation(value = "Returns the customer record identified by the parameters", notes = "Returns the customer record identified by the parameters", response = CustomerDTO.class)
//    @ApiResponses(value = {
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Customer found"),
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
//          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
//    })
//    public @ResponseBody
//    ResponseEntity<EATS> findEATS()
//          throws RecordNotFoundException {
//        LOGGER.info("Entered the ref price handler:findEATS");
//        return ResponseEntity.status(HttpStatus.OK)
//              .body(eatsRepository.findById(new EATSIdentifier("1", "1")).orElse(new EATS()));
//    }

    @PostMapping("/customer-price")
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
        LOGGER.info("Entered the ref price handler:getCustomerPrice");
        List<CustomerPrice> customerPriceList = customerPriceRepository.getCustomerPrice(customerPriceReqDTO);
        return ResponseEntity.status(HttpStatus.OK)
              .body(customerPriceList);
    }

    @PostMapping("/customer-price-with-date")
    @ApiOperation(
          value = "Gets the reference pricing for the customer item",
          notes = "If effectiveDate is provided in the req body, that will be used as the max effective date." +
                "Else current day will be used.",
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
    ResponseEntity<List<CustomerPrice>> getCustomerPriceWithDate (@RequestBody @Valid CustomerPriceReqDTO customerPriceReqDTO)
          throws ValidationException {
        LOGGER.info("Entered the ref price handler:getCustomerPrice");
        List<CustomerPrice> customerPriceList = customerPriceJDBCTemplateRepository.getCustomerPrice2(customerPriceReqDTO);
        return ResponseEntity.status(HttpStatus.OK)
              .body(customerPriceList);
    }


    @PostMapping("/customer-price-with-date-async-random")
    @ApiOperation(
          value = "Gets the reference pricing using completable futures",
          notes = "Customer and SUPCs are randomly selected",
          response = CustomerDTO.class)
    @ApiResponses(value = {
          @ApiResponse(code = org.apache.http.HttpStatus.SC_CREATED, message = "Customer created."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_CONFLICT, message = "Customer already exists."),
    })
    public @ResponseBody
    ResponseEntity<String> getCustomerPriceWithDateAsyncRandom(@RequestBody @Valid CustomerPriceReqDTO customerPriceReqDTO)
          throws ValidationException {
        LOGGER.info("Entered the ref price handler:getCustomerPrice");
        String customerPriceList = asyncMySqlRepo.getPricesForRandomValues();
        return ResponseEntity.status(HttpStatus.OK)
              .body(customerPriceList);
    }

    @PostMapping("/customer-price-with-date-async")
    @ApiOperation(
          value = "Gets the reference pricing for the customer item",
          notes = "If effectiveDate is provided in the req body, that will be used as the max effective date." +
                "Else current day will be used.",
          response = CustomerDTO.class)
    @ApiResponses(value = {
          @ApiResponse(code = org.apache.http.HttpStatus.SC_CREATED, message = "Customer created."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_CONFLICT, message = "Customer already exists."),
    })
    public @ResponseBody
    ResponseEntity<String> getCustomerPriceWithDateAsync(@RequestBody @Valid CustomerPriceReqDTO customerPriceReqDTO,
                                                         @RequestParam(required = false) Integer supcsPerQuery)
          throws ValidationException {
        LOGGER.info("Entered the ref price handler:getCustomerPrice");
        String customerPriceList = asyncMySqlRepo.getPrices(customerPriceReqDTO, supcsPerQuery);
        return ResponseEntity.status(HttpStatus.OK)
              .body(customerPriceList);
    }

}
