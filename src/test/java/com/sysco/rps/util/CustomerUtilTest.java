package com.sysco.rps.util;

import com.sysco.rps.dto.masterdata.BaseCustomerDTO;
import com.sysco.rps.dto.masterdata.CustomerDTO;
import com.sysco.rps.entity.masterdata.Customer;
import com.sysco.rps.entity.masterdata.enums.StopAttribute;
import com.sysco.rps.entity.masterdata.enums.StopClass;
import com.sysco.rps.service.exception.ErrorCode;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerUtilTest {

  @Test
  @Tag("validateDuplicateCustomer")
  @DisplayName("Verify exception is thrown with customer exist flag")
  void verifyDuplicateCustomerFlagTrue() {
    CustomerDTO customerDTO = new CustomerDTO("US0030", "11111111", "John Smith");

    ValidationException validationException = assertThrows(ValidationException.class,
            () -> CustomerUtil.validateDuplicateCustomer(true, customerDTO)
    );
    assertEquals(validationException.getErrorDTO().getCode(), ErrorCode.DUPLICATE_RECORD.getCode());
  }

  @Test
  @Tag("validateDuplicateCustomer")
  @DisplayName("Verify exception is not thrown when customer exist flag is false")
  void verifyDuplicateCustomerFlagFalse() {
    CustomerDTO customerDTO = new CustomerDTO("US0030", "11111111", "John Smith");

    assertDoesNotThrow(() -> CustomerUtil.validateDuplicateCustomer(false, customerDTO));
  }

  @Test
  @Tag("validateCustomerNotFound")
  @DisplayName("Verify exception is thrown for null customer")
  void verifyCustomerNotFoundWithNullCustomer() {
    assertThrows(RecordNotFoundException.class,
            () -> CustomerUtil.validateCustomerNotFound(null, "1234", "US0030")
    );
  }

  @Test
  @Tag("validateCustomerNotFound")
  @DisplayName("Verify exception is not thrown for not null customer")
  void verifyCustomerNotFoundWithCustomer() {
    assertDoesNotThrow(() -> CustomerUtil.validateCustomerNotFound(new Customer(), "1234", "US0030"));
  }

  @Test
  @Tag("transformCustomerCreateDTO")
  @DisplayName("Verify transform customer create optional attributes are set to default when not provided")
  void verifyTransformCustomerCreateWithoutOptionalAttributes() {
    CustomerDTO customerDTO = new CustomerDTO("US0030", "11111111", "John Smith");
    Customer customer = CustomerUtil.transformCustomerCreateDTO(customerDTO);
    assertEquals(StopClass.REGULAR, customer.getStopClass());
    assertNotNull(customer.getStopAttributes());
    assertTrue(customer.getStopAttributes().isEmpty());
  }

  @Test
  @Tag("transformCustomerUpdateDTO")
  @DisplayName("Verify transform customer update only set the given attributes: stop class")
  void verifyTransformCustomerUpdateSetStopClass() {
    Customer customer = new Customer();
    customer.setOpCoNumber("US0030");
    customer.setCustomerNumber("11111111");
    customer.setCustomerName("John Smith");
    customer.setStopClass(StopClass.REGULAR);
    customer.setStopAttributes(new ArrayList<>());

    // update stop class
    BaseCustomerDTO baseCustomerDTO = new BaseCustomerDTO();
    baseCustomerDTO.setStopClass(StopClass.PREMIUM);
    Customer updatedCustomer = CustomerUtil.transformCustomerUpdateDTO(baseCustomerDTO, customer);
    assertEquals(StopClass.PREMIUM, updatedCustomer.getStopClass());
    assertTrue(updatedCustomer.getStopAttributes().isEmpty());
  }

  @Test
  @Tag("transformCustomerUpdateDTO")
  @DisplayName("Verify transform customer update only set the given attributes: stop class and stop attributes")
  void verifyTransformCustomerUpdateSetGivenAttributes() {
    Customer customer = new Customer();
    customer.setOpCoNumber("US0030");
    customer.setCustomerNumber("11111111");
    customer.setCustomerName("John Smith");
    customer.setStopClass(StopClass.REGULAR);
    customer.setStopAttributes(new ArrayList<>());

    List<StopAttribute> stopAttributeList = new ArrayList<>();
    stopAttributeList.add(StopAttribute.LONG_WALK);
    stopAttributeList.add(StopAttribute.STAIRS);

    BaseCustomerDTO baseCustomerDTO = new BaseCustomerDTO();
    baseCustomerDTO.setStopClass(StopClass.PREMIUM);
    baseCustomerDTO.setStopAttributes(stopAttributeList);

    Customer updatedCustomer = CustomerUtil.transformCustomerUpdateDTO(baseCustomerDTO, customer);
    assertEquals(StopClass.PREMIUM, updatedCustomer.getStopClass());
    assertFalse(updatedCustomer.getStopAttributes().isEmpty());
    assertEquals(2, updatedCustomer.getStopAttributes().size());
  }

  @Test
  @Tag("transformCustomerResponse")
  @DisplayName("Verify transform customer response sets default values")
  void verifyTransformCustomerResponseSetDefaults() {
    Customer customer = new Customer();
    customer.setOpCoNumber("US0030");
    customer.setCustomerNumber("11111111");
    customer.setCustomerName("John Smith");

    CustomerDTO customerDTO = CustomerUtil.transformCustomerResponse(customer);

    assertEquals(StopClass.REGULAR, customerDTO.getStopClass());
    assertNotNull(customerDTO.getStopAttributes());
    assertTrue(customerDTO.getStopAttributes().isEmpty());
  }

  @Test
  @Tag("validateStopAttributes")
  @DisplayName("Verify valid regular level")
  void verifyValidateStopAttributesWithRegularLevelClass() {
    // empty should not throw
    assertDoesNotThrow(() -> CustomerUtil.validateStopAttributes(StopClass.REGULAR, new ArrayList<>()));
    assertDoesNotThrow(() -> CustomerUtil.validateStopAttributes(StopClass.PALLET_REGULAR, new ArrayList<>()));
    assertDoesNotThrow(() -> CustomerUtil.validateStopAttributes(StopClass.BACKHAUL, new ArrayList<>()));

    // stop attributes for regular should throw validation error
    List<StopAttribute> stopAttributes = new ArrayList<>();
    stopAttributes.add(StopAttribute.STAIRS);
    stopAttributes.add(StopAttribute.LONG_WALK);
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.REGULAR, stopAttributes));
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.PALLET_REGULAR, stopAttributes));
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.BACKHAUL, stopAttributes));
  }

  @Test
  @Tag("validateStopAttributes")
  @DisplayName("Verify valid premium level")
  void verifyValidateStopAttributesWithPremiumLevelClass() {
    // empty or not meeting required threshold should throw exceptions
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.PREMIUM, new ArrayList<>()));
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.PALLET_PREMIUM, new ArrayList<>()));
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.PREMIUM_PLUS, new ArrayList<>()));

    List<StopAttribute> stopAttributes = new ArrayList<>();
    stopAttributes.add(StopAttribute.STAIRS);
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.PREMIUM_PLUS, stopAttributes));
    // 1 is enough for premium and pallet premium
    assertDoesNotThrow(() -> CustomerUtil.validateStopAttributes(StopClass.PREMIUM, stopAttributes));
    assertDoesNotThrow(() -> CustomerUtil.validateStopAttributes(StopClass.PALLET_PREMIUM, stopAttributes));

    // stop attributes with valid threshold
    List<StopAttribute> validStopAttributes = new ArrayList<>();
    validStopAttributes.add(StopAttribute.STAIRS);
    validStopAttributes.add(StopAttribute.LONG_WALK);
    assertDoesNotThrow(() -> CustomerUtil.validateStopAttributes(StopClass.PREMIUM_PLUS, validStopAttributes));
    // exceed the maximum for premium and pallet premium
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.PREMIUM, validStopAttributes));
    assertThrows(ValidationException.class, () -> CustomerUtil.validateStopAttributes(StopClass.PALLET_PREMIUM, validStopAttributes));
  }
}
