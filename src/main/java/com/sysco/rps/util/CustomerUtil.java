package com.sysco.rps.util;

import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.dto.masterdata.BaseCustomerDTO;
import com.sysco.rps.dto.masterdata.CustomerDTO;
import com.sysco.rps.entity.masterdata.Customer;
import com.sysco.rps.entity.masterdata.enums.StopAttribute;
import com.sysco.rps.entity.masterdata.enums.StopClass;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sysco.rps.service.exception.ErrorCode.DUPLICATE_RECORD;
import static com.sysco.rps.service.exception.ErrorCode.VALIDATION_FAILURE;

public class CustomerUtil {
    private static final Logger logger = LoggerFactory.getLogger(CustomerUtil.class);
    private static final String VALIDATION_FAILURE_MESSAGE = "CustomerDTO validation failure";
    private static final int MIN_PREMIUM_STOP_ATTRIBUTES = 1;
    private static final int MAX_PREMIUM_STOP_ATTRIBUTES = 1;
    private static final int MIN_PREMIUM_PLUS_STOP_ATTRIBUTES = 2;

    private CustomerUtil(){}

    public static void validateDuplicateCustomer(boolean isExistingCustomer, CustomerDTO customerDTO)
            throws ValidationException {
        if (isExistingCustomer) {
            logger.error("customer already exists with customer number={}, opCo number={}", customerDTO.getCustomerNumber(), customerDTO.getOpCoNumber());
            ErrorDTO errorDTO = new ErrorDTO(DUPLICATE_RECORD.getCode(), MessageFormat.format("Customer with customer number {0} already exist", customerDTO.getCustomerNumber()), customerDTO, null);
            throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
        }
    }

    public static void validateStopAttributes(StopClass stopClass, List<StopAttribute> stopAttributes)
            throws ValidationException {
        ErrorDTO errorDTO = new ErrorDTO(VALIDATION_FAILURE.getCode());
        switch (stopClass) {
            case REGULAR:
            case PALLET_REGULAR:
            case BACKHAUL:
                if (!stopAttributes.isEmpty()) {
                    errorDTO.setMessage(MessageFormat.format(
                            "{0} stop class cannot contain stop attributes",
                            stopClass
                    ));
                    errorDTO.setErrorData(stopAttributes);
                    throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
                }
                break;
            case PREMIUM:
            case PALLET_PREMIUM:
                if (stopAttributes.size() < MIN_PREMIUM_STOP_ATTRIBUTES
                        || stopAttributes.size() > MAX_PREMIUM_STOP_ATTRIBUTES) {
                    errorDTO.setMessage(MessageFormat.format(
                            "{0} stop class should contain {1} stop attribute",
                            stopClass,
                            MIN_PREMIUM_STOP_ATTRIBUTES
                    ));
                    errorDTO.setErrorData(stopAttributes);
                    throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
                }
                break;
            case PREMIUM_PLUS:
                if (stopAttributes.size() < MIN_PREMIUM_PLUS_STOP_ATTRIBUTES) {
                    errorDTO.setMessage(MessageFormat.format(
                            "{0} stop class should contain a minimum of {1} stop attributes",
                            stopClass,
                            MIN_PREMIUM_PLUS_STOP_ATTRIBUTES
                    ));
                    errorDTO.setErrorData(stopAttributes);
                    throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
                }
                break;
            default:
                break;
        }
    }

    public static void validateCustomerNotFound(Customer existingCustomer, String customerNumber, String opCoNumber)
            throws RecordNotFoundException {
        if (Objects.isNull(existingCustomer)) {
            throw new RecordNotFoundException(MessageFormat.format("Customer with customer number {0} not found in the opCo {1}", customerNumber, opCoNumber));
        }
    }

    private static void setCustomerDTODefaultValues(CustomerDTO customerDTO) {
        if (Objects.isNull(customerDTO.getStopClass())) {
            logger.info("using default stop class since the customer stop class is not found");
            customerDTO.setStopClass(StopClass.REGULAR);
        }

        if (Objects.isNull(customerDTO.getStopAttributes())) {
            logger.info("using empty list of stop attributes since the customer stop attributes are not found");
            customerDTO.setStopAttributes(new ArrayList<>());
        }
    }

    public static Customer transformCustomerCreateDTO(CustomerDTO customerDTO) {
        setCustomerDTODefaultValues(customerDTO);
        return customerDTO.merge(new Customer());
    }

    public static Customer transformCustomerUpdateDTO(BaseCustomerDTO updateBaseCustomerDTO, Customer existingCustomer) {
        CustomerDTO customerDTO = new CustomerDTO(existingCustomer);
        if (Objects.nonNull(updateBaseCustomerDTO.getStopClass())) {
            logger.info("new stop class is set to {}", updateBaseCustomerDTO.getStopClass());
            customerDTO.setStopClass(updateBaseCustomerDTO.getStopClass());
        }

        if (Objects.nonNull(updateBaseCustomerDTO.getStopAttributes())) {
            logger.info("new stop attributes is set to {}", updateBaseCustomerDTO.getStopAttributes());
            existingCustomer.setStopAttributes(new ArrayList<>()); // otherwise, merge function would merge existing values and new values
            customerDTO.setStopAttributes(updateBaseCustomerDTO.getStopAttributes());
        }

        return customerDTO.merge(existingCustomer);
    }

    public static CustomerDTO transformCustomerResponse(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO(customer);
        setCustomerDTODefaultValues(customerDTO);
        return customerDTO;
    }
}
