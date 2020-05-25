package com.sysco.payplus.service.masterdata;

import com.sysco.payplus.dto.ListResponse;
import com.sysco.payplus.dto.masterdata.BaseCustomerDTO;
import com.sysco.payplus.dto.masterdata.CustomerDTO;
import com.sysco.payplus.entity.masterdata.Customer;
import com.sysco.payplus.repository.masterdata.CustomerRepository;
import com.sysco.payplus.service.exception.RecordNotFoundException;
import com.sysco.payplus.service.exception.ValidationException;
import com.sysco.payplus.util.CustomerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private static final String ROLES = "hasAnyRole( T(com.sysco.payplus.entity.security.Authority).ADMIN)";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    OpCoService opCoService;

    @Autowired
    CustomerValidationService customerValidationService;

    @Override
    @PreAuthorize(ROLES)
    public CustomerDTO createCustomer(String opCoNumber, CustomerDTO customerDTO) throws ValidationException {
        logger.info("creating customer in opCo {} - {}", opCoNumber, customerDTO);
        // validating request
        customerValidationService.customerDTOValidator.validate(customerDTO);
        boolean isExistingCustomer = customerRepository.existsByOpCoNumberAndCustomerNumberAndIsCurrentTrue(opCoNumber, customerDTO.getCustomerNumber());
        CustomerUtil.validateDuplicateCustomer(isExistingCustomer, customerDTO);

        // creating customer
        Customer customer = CustomerUtil.transformCustomerCreateDTO(customerDTO);

        // validate the customer stop attributes are matching
        CustomerUtil.validateStopAttributes(customerDTO.getStopClass(), customerDTO.getStopAttributes());

        customerRepository.save(customer);
        logger.info("customer created {}", customer);
        return customerDTO;
    }

    @Override
    @PreAuthorize(ROLES)
    public void updateCustomer(String opCoNumber, String customerNumber, BaseCustomerDTO baseCustomerDTO)
            throws ValidationException, RecordNotFoundException {
        logger.info("updating customer {} in opCo {} - {}", customerNumber, opCoNumber, baseCustomerDTO);

        // validating request
        customerValidationService.customerDTOValidator.validate(baseCustomerDTO);

        Customer existingCustomer = customerRepository
                .findByOpCoNumberAndCustomerNumberAndIsCurrentTrue(opCoNumber, customerNumber)
                .orElse(null);
        CustomerUtil.validateCustomerNotFound(existingCustomer, customerNumber, opCoNumber);

        // updating customer
        Customer updatedCustomer = CustomerUtil
                .transformCustomerUpdateDTO(baseCustomerDTO, existingCustomer);

        // validate the customer stop attributes are matching
        CustomerUtil.validateStopAttributes(updatedCustomer.getStopClass(), updatedCustomer.getStopAttributes());

        customerRepository.save(updatedCustomer);
        logger.info("customer {} updated", customerNumber);
    }

    @Override
    @PreAuthorize(ROLES)
    public CustomerDTO findCustomer(String opCoNumber, String customerNumber) throws RecordNotFoundException {
        logger.info("getting customer {} in opCo {}", customerNumber, opCoNumber);
        Customer customer = customerRepository.findByOpCoNumberAndCustomerNumberAndIsCurrentTrue(opCoNumber, customerNumber)
                .orElse(null);
        CustomerUtil.validateCustomerNotFound(customer, customerNumber, opCoNumber);
        logger.info("customer {} found in opCo {}", customerNumber, opCoNumber);
        return CustomerUtil.transformCustomerResponse(customer);
    }

    @Override
    @PreAuthorize(ROLES)
    public ListResponse<CustomerDTO> findCustomers(String opCoNumber, Pageable pageable) throws RecordNotFoundException {
        logger.info("getting customers in opCo {}", opCoNumber);
        // validate opCo
        opCoService.findByOpCoNumber(opCoNumber);

        // get customers
        Page<Customer> customerPage = customerRepository.findAllByOpCoNumberAndIsCurrentTrue(opCoNumber, pageable);
        List<CustomerDTO> customerDTOList = customerPage.get()
                .map(CustomerUtil::transformCustomerResponse)
                .collect(Collectors.toList());
        logger.info("customer fetching completed for opCo {}", opCoNumber);
        return new ListResponse<>(customerPage.getTotalElements(), customerPage.getTotalPages(), customerDTOList);
    }
}
