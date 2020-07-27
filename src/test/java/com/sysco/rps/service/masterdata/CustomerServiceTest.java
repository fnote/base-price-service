package com.sysco.rps.service.masterdata;

import com.sysco.rps.dto.masterdata.BaseCustomerDTO;
import com.sysco.rps.dto.masterdata.CustomerDTO;
import com.sysco.rps.dto.masterdata.OpCoDTO;
import com.sysco.rps.entity.masterdata.Customer;
import com.sysco.rps.entity.masterdata.enums.*;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.ErrorCode;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
class CustomerServiceTest {

  @Autowired
  CustomerService customerService;

  @Autowired
  OpCoService opCoService;

  private CustomerDTO createCustomerDTO(String opCo, String customerName, String customerNumber, StopClass stopClass, List<StopAttribute> stopAttributes) {
    Customer customer = new Customer();
    customer.setOpCoNumber(opCo);
    customer.setCustomerName(customerName);
    customer.setCustomerNumber(customerNumber);
    customer.setStopClass(stopClass);
    customer.setStopAttributes(stopAttributes);
    return new CustomerDTO(customer);
  }

  @Test
  @Tag("createCustomer")
  @Transactional
  void whenCreateCustomerDTO_thenSuccess() throws RecordNotFoundException, ValidationException {
    CustomerDTO expectedCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.REGULAR, null);
    customerService.createCustomer("US0075", expectedCustomerDTO);
    CustomerDTO actualCustomerDTO = customerService.findCustomer("US0075", "11111111");

    assertEquals(expectedCustomerDTO.getOpCoNumber(), actualCustomerDTO.getOpCoNumber());
    assertEquals(expectedCustomerDTO.getCustomerNumber(), actualCustomerDTO.getCustomerNumber());
    assertEquals(expectedCustomerDTO.getCustomerName(), actualCustomerDTO.getCustomerName());
    assertEquals(expectedCustomerDTO.getLocked(), actualCustomerDTO.getLocked());
  }

  @Test
  @Tag("createCustomer")
  @Transactional
  void whenCreateCustomerWithoutStopClass_thenSuccessWithDefaultStopClass()
          throws RecordNotFoundException, ValidationException {
    CustomerDTO expectedCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", null, null);
    customerService.createCustomer("US0075", expectedCustomerDTO);
    CustomerDTO actualCustomerDTO = customerService.findCustomer("US0075", "11111111");

    assertEquals(StopClass.REGULAR, actualCustomerDTO.getStopClass());
  }

  @Test
  @Tag("createCustomer")
  @Transactional
  void whenCreatePremiumCustomerWithStopAttributes_thenSuccess()
          throws RecordNotFoundException, ValidationException {
    List<StopAttribute> stopAttributes = new ArrayList<>();
    stopAttributes.add(StopAttribute.STAIRS);

    CustomerDTO expectedCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.PREMIUM, stopAttributes);
    customerService.createCustomer("US0075", expectedCustomerDTO);
    CustomerDTO actualCustomerDTO = customerService.findCustomer("US0075", "11111111");

    assertEquals(StopClass.PREMIUM, actualCustomerDTO.getStopClass());
    assertEquals(stopAttributes, actualCustomerDTO.getStopAttributes());
  }

  @Test
  @Tag("createCustomer")
  @Transactional
  void whenCreateCustomerDTOWithExistingCustomer_thenFail() throws ValidationException {
    CustomerDTO firstCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.PALLET_REGULAR, null);
    customerService.createCustomer("US0075", firstCustomerDTO);

    ValidationException thrownException = assertThrows(ValidationException.class, () -> {
      CustomerDTO duplicateCustomerDTO = createCustomerDTO("US0075", "Smith Johns", "11111111", StopClass.REGULAR, null);
      customerService.createCustomer("US0075", duplicateCustomerDTO);
    });

    assertEquals(ErrorCode.DUPLICATE_RECORD.getCode(), thrownException.getErrorDTO().getCode());
  }

  @Test
  @Tag("createCustomer")
  @Transactional
  void whenCreateCustomerDTOWithExistingCustomerDifferentOpCo_thenSuccess() throws ValidationException {
    CustomerDTO firstCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.PALLET_REGULAR, null);
    customerService.createCustomer("US0075", firstCustomerDTO);

    assertDoesNotThrow(() -> {
      CustomerDTO duplicateCustomerDTO = createCustomerDTO("US0001", "Smith Johns", "11111111", StopClass.REGULAR, null);
      customerService.createCustomer("US0001", duplicateCustomerDTO);
    });
  }

  @Test
  @Tag("createCustomer")
  @Transactional
  void whenCreateCustomerDTOWithConstraintViolations_thenFail() throws ValidationException {
    CustomerDTO firstCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.BACKHAUL, null);
    customerService.createCustomer("US0075", firstCustomerDTO);

    ValidationException thrownException = assertThrows(ValidationException.class, () -> {
      CustomerDTO duplicateCustomerDTO = createCustomerDTO("US0075", "Smith Johns", "11111111", StopClass.REGULAR, null);
      customerService.createCustomer("US0075", duplicateCustomerDTO);
    });

    assertEquals(ErrorCode.DUPLICATE_RECORD.getCode(), thrownException.getErrorDTO().getCode());
  }

  @Test
  @Tag("updateCustomer")
  @DisplayName("PAYP-423 Verify response retrieved by 'Edit' endpoint for valid request body")
  @Transactional
  void whenUpdateCustomerDTO_thenSuccess() throws RecordNotFoundException, ValidationException {
    CustomerDTO initialCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.REGULAR, null);
    customerService.createCustomer("US0075", initialCustomerDTO);

    BaseCustomerDTO updatedCustomerDTO = new BaseCustomerDTO();
    updatedCustomerDTO.setStopClass(StopClass.PALLET_REGULAR);
    customerService.updateCustomer("US0075", "11111111", updatedCustomerDTO);

    CustomerDTO actualCustomerDTO = customerService.findCustomer("US0075", "11111111");

    assertEquals(initialCustomerDTO.getOpCoNumber(), actualCustomerDTO.getOpCoNumber());
    assertEquals(initialCustomerDTO.getCustomerNumber(), actualCustomerDTO.getCustomerNumber());
    assertEquals(initialCustomerDTO.getCustomerName(), actualCustomerDTO.getCustomerName());
    assertEquals(initialCustomerDTO.getLocked(), actualCustomerDTO.getLocked());
    assertEquals(updatedCustomerDTO.getStopClass(), actualCustomerDTO.getStopClass());
  }

  @Test
  @Tag("updateCustomer")
  @DisplayName("Verify update the correct customer when same customer number exits in multiple opCos")
  @Transactional
  void whenUpdateCustomerDTOWithOpCo_thenSuccess() throws RecordNotFoundException, ValidationException {
    CustomerDTO opco75CustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.PALLET_REGULAR, null);
    CustomerDTO opco85CustomerDTO = createCustomerDTO("US0085", "Carlos", "11111111", StopClass.REGULAR, null);
    customerService.createCustomer("US0075", opco75CustomerDTO);
    customerService.createCustomer("US0085", opco85CustomerDTO);

    BaseCustomerDTO updated75CustomerDTO = new BaseCustomerDTO();
    updated75CustomerDTO.setStopClass(StopClass.REGULAR);
    customerService.updateCustomer("US0075", "11111111", updated75CustomerDTO);

    CustomerDTO actual75CustomerDTO = customerService.findCustomer("US0075", "11111111");
    CustomerDTO actual85CustomerDTO = customerService.findCustomer("US0085", "11111111");

    assertEquals(opco75CustomerDTO.getOpCoNumber(), actual75CustomerDTO.getOpCoNumber());
    assertEquals(opco75CustomerDTO.getCustomerNumber(), actual75CustomerDTO.getCustomerNumber());
    assertEquals(opco75CustomerDTO.getCustomerName(), actual75CustomerDTO.getCustomerName());
    assertEquals(updated75CustomerDTO.getStopClass(), actual75CustomerDTO.getStopClass());

    assertEquals(opco85CustomerDTO.getOpCoNumber(), actual85CustomerDTO.getOpCoNumber());
    assertEquals(opco85CustomerDTO.getCustomerNumber(), actual85CustomerDTO.getCustomerNumber());
    assertEquals(opco85CustomerDTO.getCustomerName(), actual85CustomerDTO.getCustomerName());
    assertEquals(opco85CustomerDTO.getStopClass(), actual85CustomerDTO.getStopClass());
  }

  @Test
  @Tag("updateCustomer")
  @DisplayName("Verify update the customer with stop attributes possible")
  @Transactional
  void whenUpdateCustomerDTOWithStopAttributes_thenSuccess() throws RecordNotFoundException, ValidationException {
    // initial customer creation with no stop attributes
    List<StopAttribute> stopAttributes = new ArrayList<>();
    stopAttributes.add(StopAttribute.STAIRS);
    stopAttributes.add(StopAttribute.ELEVATOR);
    CustomerDTO initialCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.PREMIUM_PLUS, stopAttributes);
    customerService.createCustomer("US0075", initialCustomerDTO);

    // updating only stop attributes
    stopAttributes.add(StopAttribute.SPECIAL_EVENT);

    BaseCustomerDTO customerUpdate1 = new BaseCustomerDTO();
    customerUpdate1.setStopAttributes(stopAttributes);
    customerService.updateCustomer("US0075", "11111111", customerUpdate1);

    CustomerDTO customerActual1= customerService.findCustomer("US0075", "11111111");
    assertEquals(initialCustomerDTO.getStopClass(), customerActual1.getStopClass()); // stop class shouldn't change
    assertEquals(customerUpdate1.getStopAttributes(), customerActual1.getStopAttributes());

    // update stop attributes as well as stop class
    BaseCustomerDTO customerUpdate2 = new BaseCustomerDTO();
    List<StopAttribute> stopAttributes2 = new ArrayList<>();
    stopAttributes2.add(StopAttribute.STAIRS);

    customerUpdate2.setStopClass(StopClass.PREMIUM);
    customerUpdate2.setStopAttributes(stopAttributes2);
    customerService.updateCustomer("US0075", "11111111", customerUpdate2);

    CustomerDTO customerActual2= customerService.findCustomer("US0075", "11111111");
    assertEquals(customerUpdate2.getStopClass(), customerActual2.getStopClass());
    assertEquals(customerUpdate2.getStopAttributes(), customerActual2.getStopAttributes());
  }

  @Test
  @Tag("updateCustomer")
  @DisplayName("Verify update the customer from premium level class to regular level class fails when stop attributes not set to empty")
  @Transactional
  void whenUpdateCustomerDTOFromPremiumToRegular_thenFail() throws ValidationException {
    // initial customer creation with no stop attributes
    List<StopAttribute> stopAttributes = new ArrayList<>();
    stopAttributes.add(StopAttribute.STAIRS);
    CustomerDTO initialCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.PREMIUM, stopAttributes);
    customerService.createCustomer("US0075", initialCustomerDTO);

    // should fail if only updating the class (i.e. you should update class with attributes to empty)
    BaseCustomerDTO customerUpdate1 = new BaseCustomerDTO();
    customerUpdate1.setStopClass(StopClass.REGULAR);
    assertThrows(ValidationException.class, () -> customerService.updateCustomer("US0075", "11111111", customerUpdate1));
  }

  @Test
  @Tag("updateCustomer")
  @DisplayName("Verify update the customer from premium level class to regular level class")
  @Transactional
  void whenUpdateCustomerDTOFromPremiumToRegular_thenSuccess() throws ValidationException {
    // initial customer creation with no stop attributes
    List<StopAttribute> stopAttributes = new ArrayList<>();
    stopAttributes.add(StopAttribute.STAIRS);
    CustomerDTO initialCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.PREMIUM, stopAttributes);
    customerService.createCustomer("US0075", initialCustomerDTO);

    // update stop class with stop attributes set to empty should update the customer
    BaseCustomerDTO customerUpdate1 = new BaseCustomerDTO();
    customerUpdate1.setStopClass(StopClass.REGULAR);
    customerUpdate1.setStopAttributes(new ArrayList<>());
    assertDoesNotThrow(() -> customerService.updateCustomer("US0075", "11111111", customerUpdate1));
  }

  @Test
  @Tag("updateCustomer")
  @DisplayName("PAYP-460 Verify response retrieved by 'Edit' endpoint for invalid OpCo")
  @Transactional
  void whenUpdateCustomerDTONotExisting_thenFail() throws ValidationException {
    CustomerDTO initialCustomerDTO = createCustomerDTO("US0075", "John Smith", "11111111", StopClass.REGULAR, null);
    customerService.createCustomer("US0075", initialCustomerDTO);

    // non existing customer number
    BaseCustomerDTO updatedCustomerDTO = new BaseCustomerDTO();
    updatedCustomerDTO.setStopClass(StopClass.PALLET_REGULAR);
    assertThrows(RecordNotFoundException.class, () -> {
      customerService.updateCustomer("US0075", "22222222", updatedCustomerDTO);
    });

    // customer number exist but different opCo should fail
    BaseCustomerDTO updatedCustomerDTO1 = new BaseCustomerDTO();
    updatedCustomerDTO1.setStopClass(StopClass.PALLET_REGULAR);
    assertThrows(RecordNotFoundException.class, () -> {
      customerService.updateCustomer("US0001", "11111111", updatedCustomerDTO);
    });
  }

  private void createOpCo(String opCoNumber, String workdayName, Integer sapEntityId, Integer susEntityId, String adpPayGroup, Integer adpLocationId)
          throws DuplicateRecordException, ValidationException {
    OpCoDTO opCoDTO = new OpCoDTO();
    opCoDTO.setOpCoNumber(opCoNumber);
    opCoDTO.setWorkdayName(workdayName);
    opCoDTO.setSapEntityId(sapEntityId);
    opCoDTO.setSusEntityId(susEntityId);
    opCoDTO.setAdpPayGroup(adpPayGroup);
    opCoDTO.setAdpLocationId(adpLocationId);
    opCoDTO.setTargetPiecesPerTrip(25);

    opCoDTO.setMarket(Market.MIDWEST.toString());
    opCoDTO.setTimezone(Timezone.ALASKA.toString());
    opCoDTO.setCountryCode("US");
    opCoDTO.setActiveStatus(ActivationStatus.ACTIVE.toString());
    opCoService.saveOpCo(opCoDTO);
  }

  private void createCustomers() {
    ArrayList<StopAttribute> stopAttributes = new ArrayList<>();
    stopAttributes.add(StopAttribute.STAIRS);
    try {
      createOpCo("US0010", "op10", 1, 10, "ABC", 1);
      createOpCo("US0020", "op20", 2, 20, "DEF", 2);
      CustomerDTO customer1 = createCustomerDTO("US0010", "John Smith", "11111111", StopClass.REGULAR, null);
      CustomerDTO customer2 = createCustomerDTO("US0010", "Smith Johns", "22222222", StopClass.REGULAR, new ArrayList<>());
      CustomerDTO customer3 = createCustomerDTO("US0010", "Carl Johnson", "33333333", StopClass.REGULAR, null);
      CustomerDTO customer4 = createCustomerDTO("US0020", "CJ", "44444444", StopClass.PREMIUM, stopAttributes);

      customerService.createCustomer("US0010", customer1);
      customerService.createCustomer("US0010", customer2);
      customerService.createCustomer("US0010", customer3);
      customerService.createCustomer("US0020", customer4);
    } catch (ValidationException | DuplicateRecordException e) {
      fail(e.getMessage(), e);
    }
  }

  @Test
  @Tag("findCustomers")
  @DisplayName("PAYP-466 Verify response retrieved by 'Get By OpCo' endpoint for a valid OpCo")
  @Transactional
  void whenFindAllCustomersWithValidOpCo() throws RecordNotFoundException {
    createCustomers();
    List<CustomerDTO> customersUS75 = customerService.findCustomers("US0010", PageRequest.of(0, 10)).getItems();
    assertEquals(3, customersUS75.size());
  }

  @Test
  @DisplayName("PAYP-469 Verify response retrieved by 'Get By OpCo' endpoint for invalid OpCo")
  @Tag("findCustomers")
  @Transactional
  void whenFindAllCustomersWithInValidOpCo() {
    createCustomers();
    assertThrows(RecordNotFoundException.class, () -> {
      customerService.findCustomers("US0012", PageRequest.of(0, 10));
    });
  }

  @Test
  @DisplayName("PAYP-462 Verify response retrieved by 'Get By Customer ID' endpoint for a valid customer Id")
  @Tag("findCustomer")
  @Transactional
  void whenFindCustomerWithValidOpCo() throws RecordNotFoundException {
    createCustomers();
    CustomerDTO customerDTO = customerService.findCustomer("US0010", "11111111");
    assertEquals("11111111", customerDTO.getCustomerNumber());
    assertEquals("US0010", customerDTO.getOpCoNumber());
  }

  @Test
  @DisplayName("Find customer with null/empty stop attribute")
  @Tag("findCustomer")
  @Transactional
  void whenFindCustomerWithNullEmptyStopAttributes() throws RecordNotFoundException {
    createCustomers();
    // stop attributes null
    CustomerDTO customerDTONullStopAttr = customerService.findCustomer("US0010", "11111111");
    assertTrue(customerDTONullStopAttr.getStopAttributes().isEmpty());

    // stop attributes empty
    CustomerDTO customerDTOEmptyStopAttr = customerService.findCustomer("US0010", "22222222");
    assertTrue(customerDTOEmptyStopAttr.getStopAttributes().isEmpty());
  }

  @Test
  @DisplayName("PAYP-465 Verify response retrieved by 'Get By Customer ID' endpoint for invalid Customer Id")
  @Tag("findCustomer")
  @Transactional
  void whenFindCustomerWithInvaliCustomer() {
    createCustomers();
    assertThrows(RecordNotFoundException.class, () -> {
      customerService.findCustomer("US0010", "9999999");
    });
  }

}
