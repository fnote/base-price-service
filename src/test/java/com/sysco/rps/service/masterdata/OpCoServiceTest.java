package com.sysco.rps.service.masterdata;

import com.sysco.rps.dto.pp.ListResponse;
import com.sysco.rps.dto.pp.masterdata.OpCoDTO;
import com.sysco.rps.dto.pp.masterdata.OpCoDTOTestUtil;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.RecordLockedException;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.exception.ValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
class OpCoServiceTest extends OpCoDTOTestUtil {

  @Autowired
  OpCoService opCoService;

  @Test
  @Transactional
  public void testFindByOpCoNumber_thenSuccess() throws DuplicateRecordException, ValidationException, RecordNotFoundException {
    OpCoDTO opCoDTO = createOpCoDTO();
    opCoService.saveOpCo(opCoDTO);
    OpCoDTO foundOpCoDTO = opCoService.findByOpCoNumber("US0001");
    assertEquals(opCoDTO, foundOpCoDTO);
  }

  @Test
  @Transactional
  public void testFindAllOpCos_thenSuccess() throws DuplicateRecordException, ValidationException, RecordNotFoundException {
    List<OpCoDTO> opCoDTOList = createOpCoDTOList();
    for (OpCoDTO opCoDTO : opCoDTOList) {
      opCoService.saveOpCo(opCoDTO);
    }
    ListResponse<OpCoDTO> foundOpCos = opCoService.findAllOpCos("US", null);
    assertArrayEquals(opCoDTOList.toArray(), foundOpCos.getItems().toArray());
  }

  @Test
  @Transactional
  public void testSaveOpCo_thenSuccess() throws DuplicateRecordException, ValidationException {
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoDTO savedOpCoDTO = opCoService.saveOpCo(opCoDTO);
    assertEquals(opCoDTO, savedOpCoDTO);
  }

  @Test
  @Transactional
  public void testUpdateOpCo_thenSuccess() throws DuplicateRecordException, ValidationException, RecordNotFoundException, RecordLockedException {
    OpCoDTO opCoDTO = createOpCoDTO();
    opCoService.saveOpCo(opCoDTO);
    opCoDTO.setAdpLocationId(333);
    opCoDTO.setSapEntityId(444);
    OpCoDTO updatedOpCo = opCoService.updateOpCo("US0001", opCoDTO);
    assertEquals(opCoDTO, updatedOpCo);
  }

  @Test
  @Transactional
  public void testFindByOpCoNumber_thenRecordNotFoundException() {
    assertThrows(RecordNotFoundException.class, () -> opCoService.findByOpCoNumber("US0001"));
  }

  @Test
  @Transactional
  public void testFindAllOpCos_thenRecordNotFoundException() {
    assertThrows(RecordNotFoundException.class, () -> opCoService.findAllOpCos("US", null));
  }

  @Test
  @Transactional
  public void testSaveOpCoWithDuplicateFields_thenDuplicateRecordException() throws DuplicateRecordException, ValidationException {
    OpCoDTO opCoDTO = createOpCoDTO("US0001", "Test 1", 111, 1, "AAA", 111);
    opCoService.saveOpCo(opCoDTO);

    OpCoDTO opCoDTOWithDuplicateOpCoNumberAndSusEntityId = createOpCoDTO("US0001", "Test 2", 222, 1, "BBB", 222);
    OpCoDTO opCoDTOWithDuplicateWorkdayName = createOpCoDTO("US0002", "Test 1", 222, 2, "BBB", 222);
    OpCoDTO opCoDTOWithDuplicateSapEntityId = createOpCoDTO("US0002", "Test 2", 111, 2, "BBB", 222);
    OpCoDTO opCoDTOWithDuplicateAdpPayGroup = createOpCoDTO("US0002", "Test 2", 222, 2, "AAA", 222);
    OpCoDTO opCoDTOWithDuplicateAdpLocationId = createOpCoDTO("US0002", "Test 2", 222, 2, "BBB", 111);
    OpCoDTO opCoDTOWithDuplicateAll = createOpCoDTO("US0001", "Test 1", 111, 1, "AAA", 111);

    assertThrows(DuplicateRecordException.class, () -> opCoService.saveOpCo(opCoDTOWithDuplicateOpCoNumberAndSusEntityId));
    assertThrows(DuplicateRecordException.class, () -> opCoService.saveOpCo(opCoDTOWithDuplicateWorkdayName));
    assertThrows(DuplicateRecordException.class, () -> opCoService.saveOpCo(opCoDTOWithDuplicateSapEntityId));
    assertThrows(DuplicateRecordException.class, () -> opCoService.saveOpCo(opCoDTOWithDuplicateAdpPayGroup));
    assertThrows(DuplicateRecordException.class, () -> opCoService.saveOpCo(opCoDTOWithDuplicateAdpLocationId));
    assertThrows(DuplicateRecordException.class, () -> opCoService.saveOpCo(opCoDTOWithDuplicateAll));
  }

  @Test
  @Transactional
  public void testSaveOpCoWithInvalidOpCoNumber_thenValidationException() {
    OpCoDTO opCoDTOWithNullOpCoNumber = createOpCoDTO();
    opCoDTOWithNullOpCoNumber.setOpCoNumber(null);

    OpCoDTO opCoDTOWithInvalidOpCoNumberFormat = createOpCoDTO();
    opCoDTOWithInvalidOpCoNumberFormat.setOpCoNumber("TTTT66");

    OpCoDTO opCoDTOWithInvalidOpCoNumberLength = createOpCoDTO();
    opCoDTOWithInvalidOpCoNumberLength.setOpCoNumber("US000000");

    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullOpCoNumber));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithInvalidOpCoNumberFormat));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithInvalidOpCoNumberLength));
  }

  @Test
  @Transactional
  public void testSaveOpCoWithInvalidWorkdayName_thenValidationException() {
    OpCoDTO opCoDTOWithNullWorkdayName = createOpCoDTO();
    opCoDTOWithNullWorkdayName.setWorkdayName(null);

    OpCoDTO opCoDTOWithInvalidWorkdayNameLength = createOpCoDTO();
    opCoDTOWithInvalidWorkdayNameLength.setWorkdayName("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");

    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullWorkdayName));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithInvalidWorkdayNameLength));
  }

  @Test
  @Transactional
  public void testSaveOpCoWithInvalidCountryCode_thenValidationException() {
    OpCoDTO opCoDTOWithNullCountryCode = createOpCoDTO();
    opCoDTOWithNullCountryCode.setCountryCode(null);

    OpCoDTO opCoDTOWithInvalidCountryCodeLengthUpperBound = createOpCoDTO();
    opCoDTOWithInvalidCountryCodeLengthUpperBound.setCountryCode("AAAA");

    OpCoDTO opCoDTOWithInvalidCountryCodeLengthLowerBound = createOpCoDTO();
    opCoDTOWithInvalidCountryCodeLengthLowerBound.setCountryCode("A");

    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullCountryCode));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithInvalidCountryCodeLengthUpperBound));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithInvalidCountryCodeLengthLowerBound));
  }

  @Transactional
  public void testSaveOpCoWithInvalidSusEntityId_thenValidationException() {
    OpCoDTO opCoDTOWithNullSusEntityId = createOpCoDTO();
    opCoDTOWithNullSusEntityId.setSusEntityId(null);

    OpCoDTO opCoDTOWithInvalidSusEntityId = createOpCoDTO();
    opCoDTOWithInvalidSusEntityId.setOpCoNumber("US0001");
    opCoDTOWithInvalidSusEntityId.setSusEntityId(2);

    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullSusEntityId));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithInvalidSusEntityId));
  }

  @Test
  @Transactional
  public void testSaveOpCoWithInvalidAdpPayGroup_thenValidationException() {
    OpCoDTO opCoDTOWithNullAdpPayGroup = createOpCoDTO();
    opCoDTOWithNullAdpPayGroup.setAdpPayGroup(null);

    OpCoDTO opCoDTOWithInvalidAdpPayGroup = createOpCoDTO();
    opCoDTOWithInvalidAdpPayGroup.setAdpPayGroup("AAAA");

    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullAdpPayGroup));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithInvalidAdpPayGroup));
  }

  @Test
  @Transactional
  public void testSaveOpCoWithInvalidTargetPiecesPerTrip_thenValidationException() {
    OpCoDTO opCoDTOWithNullTargetPiecesPerTrip = createOpCoDTO();
    opCoDTOWithNullTargetPiecesPerTrip.setTargetPiecesPerTrip(null);

    OpCoDTO opCoDTOWithInvalidTargetPiecesPerTrip = createOpCoDTO();
    opCoDTOWithInvalidTargetPiecesPerTrip.setTargetPiecesPerTrip(5001);

    OpCoDTO opCoDTOWithNegativeTargetPiecesPerTrip = createOpCoDTO();
    opCoDTOWithNegativeTargetPiecesPerTrip.setTargetPiecesPerTrip(-10);

    OpCoDTO opCoDTOWithZeroTargetPiecesPerTrip = createOpCoDTO();
    opCoDTOWithZeroTargetPiecesPerTrip.setTargetPiecesPerTrip(0);

    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullTargetPiecesPerTrip));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithInvalidTargetPiecesPerTrip));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNegativeTargetPiecesPerTrip));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithZeroTargetPiecesPerTrip));
  }

  @Test
  @Transactional
  public void testSaveOpCoWithOtherInvalidFields_thenValidationException() {

    OpCoDTO opCoDTOWithNullMarket = createOpCoDTO();
    opCoDTOWithNullMarket.setMarket(null);

    OpCoDTO opCoDTOWithNullSapEntityId = createOpCoDTO();
    opCoDTOWithNullSapEntityId.setSapEntityId(null);

    OpCoDTO opCoDTOWithNullAdpLocationId = createOpCoDTO();
    opCoDTOWithNullAdpLocationId.setAdpLocationId(null);

    OpCoDTO opCoDTOWithNullCurrency = createOpCoDTO();

    OpCoDTO opCoDTOWithNullTimezone = createOpCoDTO();
    opCoDTOWithNullTimezone.setTimezone(null);

    OpCoDTO opCoDTOWithNullPayrollCSVFormat = createOpCoDTO();


    OpCoDTO opCoDTOWithNullActiveStatus = createOpCoDTO();
    opCoDTOWithNullActiveStatus.setActiveStatus(null);

    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullMarket));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullSapEntityId));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullAdpLocationId));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullTimezone));
    assertThrows(ValidationException.class, () -> opCoService.saveOpCo(opCoDTOWithNullActiveStatus));
  }

  @Test
  @Transactional
  public void testUpdateOpCo_thenRecordNotFoundException() {
    OpCoDTO opCoDTO = createOpCoDTO();
    assertThrows(RecordNotFoundException.class, () -> opCoService.updateOpCo("US0001", opCoDTO));
  }

  @Test
  @Transactional
  public void testUpdateOpCo_thenDuplicateException() throws DuplicateRecordException, ValidationException {
    OpCoDTO opCoDTO = createOpCoDTO("US0001", "Test 1", 111, 1, "AAA", 111);
    OpCoDTO opCoDTO2 = createOpCoDTO("US0002", "Test 2", 222, 2, "BBB", 222);
    opCoService.saveOpCo(opCoDTO);
    opCoService.saveOpCo(opCoDTO2);

    OpCoDTO opCoDTOWithDuplicateOpCoNumberAndSusEntityId = createOpCoDTO("US0001", "Test 2", 222, 1, "BBB", 222);
    OpCoDTO opCoDTOWithDuplicateWorkdayName = createOpCoDTO("US0002", "Test 1", 222, 2, "BBB", 222);
    OpCoDTO opCoDTOWithDuplicateSapEntityId = createOpCoDTO("US0002", "Test 2", 111, 2, "BBB", 222);
    OpCoDTO opCoDTOWithDuplicateAdpPayGroup = createOpCoDTO("US0002", "Test 2", 222, 2, "AAA", 222);
    OpCoDTO opCoDTOWithDuplicateAdpLocationId = createOpCoDTO("US0002", "Test 2", 222, 2, "BBB", 111);
    OpCoDTO opCoDTOWithDuplicateAll = createOpCoDTO("US0001", "Test 1", 111, 1, "AAA", 111);

    assertThrows(DuplicateRecordException.class, () -> opCoService.updateOpCo("US0002", opCoDTOWithDuplicateOpCoNumberAndSusEntityId));
    assertThrows(DuplicateRecordException.class, () -> opCoService.updateOpCo("US0002", opCoDTOWithDuplicateWorkdayName));
    assertThrows(DuplicateRecordException.class, () -> opCoService.updateOpCo("US0002", opCoDTOWithDuplicateSapEntityId));
    assertThrows(DuplicateRecordException.class, () -> opCoService.updateOpCo("US0002", opCoDTOWithDuplicateAdpPayGroup));
    assertThrows(DuplicateRecordException.class, () -> opCoService.updateOpCo("US0002", opCoDTOWithDuplicateAdpLocationId));
    assertThrows(DuplicateRecordException.class, () -> opCoService.updateOpCo("US0002", opCoDTOWithDuplicateAll));
  }

}
