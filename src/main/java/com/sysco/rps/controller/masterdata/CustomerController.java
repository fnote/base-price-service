package com.sysco.rps.controller.masterdata;

import com.sysco.rps.dto.pp.ListResponse;
import com.sysco.rps.dto.pp.masterdata.BaseCustomerDTO;
import com.sysco.rps.dto.pp.masterdata.CustomerDTO;
import com.sysco.rps.service.masterdata.CustomerService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/ref-price/v1/master-data")
@Validated
public class CustomerController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private static final class CustomerDTOListResponse extends ListResponse<CustomerDTO>{}

    @Autowired
    private CustomerService customerService;

    @GetMapping("/opcos/{opco-number}/customers/{customer-number}")
    @ApiOperation(value = "Returns the customer record identified by the parameters", notes = "Returns the customer record identified by the parameters", response = CustomerDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Customer found"),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
    })
    public @ResponseBody
    ResponseEntity<CustomerDTO> findCustomer(
            @ApiParam(value = "OpCo number", required = true)
            @ValidOpCoNumberFormat(message = "OpCo number should have 2 letters followed by 4 digits") @PathVariable(name = "opco-number") String opCoNumber,
            @ApiParam(value = "Customer number", required = true) @PathVariable(name = "customer-number") String customerNumber)
            throws RecordNotFoundException {
        logger.info("Entered the customer handler:findCustomer");
        CustomerDTO customerDTO = customerService.findCustomer(opCoNumber, customerNumber);
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerDTO);
    }

    @GetMapping("/opcos/{opco-number}/customers")
    @ApiOperation(value = "Returns the driver record identified by the parameters", notes = "Returns the customer records identified by the parameters", response = CustomerDTOListResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Customers found"),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customers validation failed."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customers not found."),
    })
    public @ResponseBody
    ResponseEntity<ListResponse<CustomerDTO>> findCustomers(
            @ApiParam(value = "OpCo number", required = true)
            @ValidOpCoNumberFormat(message = "OpCo number should have 2 letters followed by 4 digits") @PathVariable(name = "opco-number") String opCoNumber,
            @ApiParam(value = "Page number to fetch", required = false) @RequestParam(name = "page", required = false) Integer page,
            @ApiParam(value = "Number of records per page", required = false) @RequestParam(name = "page_size", required = false) Integer pageSize
    ) throws RecordNotFoundException {
        logger.info("Entered the customer handler:findCustomers");
        ListResponse<CustomerDTO> customerDTOList = customerService.findCustomers(opCoNumber, getPageable(page, pageSize));
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerDTOList);
    }


    @PostMapping("/opcos/{opco-number}/customer")
    @ApiOperation(value = "Creates a new driver record or update an existing driver record", notes = "Returns the newly created Driver Master Data with its auto assigned Id", response = CustomerDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = org.apache.http.HttpStatus.SC_CREATED, message = "Customer created."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_CONFLICT, message = "Customer already exists."),
    })
    public @ResponseBody
    ResponseEntity<CustomerDTO> createCustomer(
            @ApiParam(value = "OpCo number", required = true)
            @ValidOpCoNumberFormat(message = "OpCo number should have 2 letters followed by 4 digits") @PathVariable(name = "opco-number") String opCoNumber,
            @ApiParam(value = "Driver work day details", required = true) @RequestBody @Valid CustomerDTO customerDTO)
            throws ValidationException {
        logger.info("Entered the customer handler:createCustomer");
        CustomerDTO createdCustomerDTO = customerService.createCustomer(opCoNumber, customerDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(createdCustomerDTO);
    }

    @PatchMapping("/opcos/{opco-number}/customers/{customer-number}")
    @ApiOperation(value = "Update an existing customer record", notes = "Only return the 204 status on success", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = org.apache.http.HttpStatus.SC_NO_CONTENT, message = "Customer updated."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "Customer validation failed."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "Customer not found."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_CONFLICT, message = "Customer locked."),
    })
    public @ResponseBody
    ResponseEntity<Void> updateCustomer(
            @ApiParam(value = "OpCo number", required = true)
            @ValidOpCoNumberFormat(message = "OpCo number should have 2 letters followed by 4 digits") @PathVariable(name = "opco-number") String opCoNumber,
            @ApiParam(value = "Customer number", required = true) @PathVariable(name = "customer-number") String customerNumber,
            @ApiParam(value = "Customer related details", required = true) @RequestBody @Valid BaseCustomerDTO baseCustomerDTO)
            throws ValidationException, RecordNotFoundException {
        logger.info("Entered the customer handler:saveCustomer");
        customerService.updateCustomer(opCoNumber, customerNumber, baseCustomerDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}

