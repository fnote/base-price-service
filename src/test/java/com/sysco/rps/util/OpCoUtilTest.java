package com.sysco.rps.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sysco.rps.dto.pp.masterdata.OpCoDTO;
import com.sysco.rps.dto.pp.masterdata.OpCoDTOTestUtil;
import com.sysco.rps.entity.pp.masterdata.OpCo;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.ErrorCode;
import com.sysco.rps.service.exception.RecordNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class OpCoUtilTest extends OpCoDTOTestUtil {

  @Test
  void whenOpCoListEmpty_thenThrowException() {
    List<OpCo> opCoList = new ArrayList<>();
    PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE);
    Page<OpCo> page = new PageImpl<>(opCoList, pageable, opCoList.size());
    assertThrows(RecordNotFoundException.class, () -> OpCoUtil.checkOpCoListEmpty(page, "US"));
  }

  @Test
  void whenOpCoListNotEmpty_thenDoesNotThrowException() {
    List<OpCo> opCoList = new ArrayList<>();
    opCoList.add(createOpCoDTO().merge(new OpCo()));
    PageRequest pageable = PageRequest.of(0, Integer.MAX_VALUE);
    Page<OpCo> page = new PageImpl<>(opCoList, pageable, opCoList.size());
    assertDoesNotThrow(() -> OpCoUtil.checkOpCoListEmpty(page, "US"));
  }

  @Test
  void whenDuplicateOpCoNumberOnInsert_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateOpCoNumberOnInsert(true, opCoDTO, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.OPCO_NUMBER_FIELD_NAME));
    assertEquals(opCoDTO.getOpCoNumber(), duplicates.get(OpCoUtil.OPCO_NUMBER_FIELD_NAME));
  }

  @Test
  void whenNotDuplicateOpCoNumberOnInsert_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateOpCoNumberOnInsert(false, opCoDTO, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.OPCO_NUMBER_FIELD_NAME));
  }

  @Test
  void whenDuplicateWorkdayNameOnInsert_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateWorkdayNameOnInsert(true, opCoDTO, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.WORKDAY_NAME_FIELD_NAME));
    assertEquals(opCoDTO.getWorkdayName(), duplicates.get(OpCoUtil.WORKDAY_NAME_FIELD_NAME));
  }

  @Test
  void whenNotDuplicateWorkdayNameOnInsert_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateWorkdayNameOnInsert(false, opCoDTO, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.WORKDAY_NAME_FIELD_NAME));
  }

  @Test
  void whenDuplicateSapEntityIdOnInsert_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateSapEntityIdOnInsert(true, opCoDTO, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.SAP_ENTITY_ID_FIELD_NAME));
    assertEquals(String.valueOf(opCoDTO.getSapEntityId()), duplicates.get(OpCoUtil.SAP_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenNotDuplicateSapEntityIdOnInsert_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateSapEntityIdOnInsert(false, opCoDTO, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.SAP_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenDuplicateSusEntityIdOnInsert_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateSusEntityIdOnInsert(true, opCoDTO, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.SUS_ENTITY_ID_FIELD_NAME));
    assertEquals(String.valueOf(opCoDTO.getSusEntityId()), duplicates.get(OpCoUtil.SUS_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenNotDuplicateSusEntityIdOnInsert_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateSusEntityIdOnInsert(false, opCoDTO, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.SUS_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenDuplicateAdpPayGroupOnInsert_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateAdpPayGroupOnInsert(true, opCoDTO, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.ADP_PAY_GROUP_FIELD_NAME));
    assertEquals(opCoDTO.getAdpPayGroup(), duplicates.get(OpCoUtil.ADP_PAY_GROUP_FIELD_NAME));
  }

  @Test
  void whenNotDuplicateAdpPayGroupOnInsert_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateAdpPayGroupOnInsert(false, opCoDTO, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.ADP_PAY_GROUP_FIELD_NAME));
  }

  @Test
  void whenDuplicateAdpLocationIdOnInsert_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateAdpLocationIdOnInsert(true, opCoDTO, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME));
    assertEquals(String.valueOf(opCoDTO.getAdpLocationId()), duplicates.get(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME));
  }

  @Test
  void whenNotDuplicateAdpLocationIdOnInsert_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCoUtil.checkDuplicateAdpLocationIdOnInsert(false, opCoDTO, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME));
  }

  @Test
  void whenOpCoNumberEqualsInOpCoAndOpCoDTOAndDuplicateOpCoNumberOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateOpCoNumberOnEdit(true, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.OPCO_NUMBER_FIELD_NAME));
  }

  @Test
  void whenOpCoNumberEqualsInOpCoAndOpCoDTOAndNotDuplicateOpCoNumberOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateOpCoNumberOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.OPCO_NUMBER_FIELD_NAME));
  }

  @Test
  void whenOpCoNumberNotEqualsInOpCoAndOpCoDTOAndDuplicateOpCoNumberOnEdit_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = new OpCo();
    opCo.setOpCoNumber("US0002");
    OpCoUtil.checkDuplicateOpCoNumberOnEdit(true, opCoDTO, opCo, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.OPCO_NUMBER_FIELD_NAME));
    assertEquals(opCoDTO.getOpCoNumber(), duplicates.get(OpCoUtil.OPCO_NUMBER_FIELD_NAME));
  }

  @Test
  void whenOpCoNumberNotEqualsInOpCoAndOpCoDTOAndNotDuplicateOpCoNumberOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    opCo.setOpCoNumber("US0002");
    OpCoUtil.checkDuplicateOpCoNumberOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.OPCO_NUMBER_FIELD_NAME));
  }

  @Test
  void whenWorkdayNameEqualsInOpCoAndOpCoDTOAndDuplicateWorkdayNameOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateWorkdayNameOnEdit(true, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.WORKDAY_NAME_FIELD_NAME));
  }

  @Test
  void whenWorkdayNameEqualsInOpCoAndOpCoDTOAndNotDuplicateWorkdayNameOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateWorkdayNameOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.WORKDAY_NAME_FIELD_NAME));
  }

  @Test
  void whenWorkdayNameNotEqualsInOpCoAndOpCoDTOAndDuplicateWorkdayNameOnEdit_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = new OpCo();
    opCo.setWorkdayName("Sysco Test 2");
    OpCoUtil.checkDuplicateWorkdayNameOnEdit(true, opCoDTO, opCo, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.WORKDAY_NAME_FIELD_NAME));
    assertEquals(opCoDTO.getWorkdayName(), duplicates.get(OpCoUtil.WORKDAY_NAME_FIELD_NAME));
  }

  @Test
  void whenWorkdayNameNotEqualsInOpCoAndOpCoDTOAndNotDuplicateWorkdayNameOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    opCo.setWorkdayName("Sysco Test 2");
    OpCoUtil.checkDuplicateWorkdayNameOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.WORKDAY_NAME_FIELD_NAME));
  }

  @Test
  void whenSapEntityIdEqualsInOpCoAndOpCoDTOAndDuplicateSapEntityIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateSapEntityIdOnEdit(true, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.SAP_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenSapEntityIdEqualsInOpCoAndOpCoDTOAndNotDuplicateSapEntityIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateSapEntityIdOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.SAP_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenSapEntityIdNotEqualsInOpCoAndOpCoDTOAndDuplicateSapEntityIdOnEdit_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = new OpCo();
    opCo.setSapEntityId(2);
    OpCoUtil.checkDuplicateSapEntityIdOnEdit(true, opCoDTO, opCo, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.SAP_ENTITY_ID_FIELD_NAME));
    assertEquals(String.valueOf(opCoDTO.getSapEntityId()), duplicates.get(OpCoUtil.SAP_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenSapEntityIdNotEqualsInOpCoAndOpCoDTOAndNotDuplicateSapEntityIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    opCo.setSapEntityId(2);
    OpCoUtil.checkDuplicateSapEntityIdOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.SAP_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenSusEntityIdEqualsInOpCoAndOpCoDTOAndDuplicateSusEntityIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateSusEntityIdOnEdit(true, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.SUS_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenSusEntityIdEqualsInOpCoAndOpCoDTOAndNotDuplicateSusEntityIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateSusEntityIdOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.SUS_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenSusEntityIdNotEqualsInOpCoAndOpCoDTOAndDuplicateSusEntityIdOnEdit_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = new OpCo();
    opCo.setSusEntityId(2);
    OpCoUtil.checkDuplicateSusEntityIdOnEdit(true, opCoDTO, opCo, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.SUS_ENTITY_ID_FIELD_NAME));
    assertEquals(String.valueOf(opCoDTO.getSusEntityId()), duplicates.get(OpCoUtil.SUS_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenSusEntityIdNotEqualsInOpCoAndOpCoDTOAndNotDuplicateSusEntityIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    opCo.setSusEntityId(2);
    OpCoUtil.checkDuplicateSusEntityIdOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.SUS_ENTITY_ID_FIELD_NAME));
  }

  @Test
  void whenAdpPayGroupEqualsInOpCoAndOpCoDTOAndDuplicateAdpPayGroupOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateAdpPayGroupOnEdit(true, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.ADP_PAY_GROUP_FIELD_NAME));
  }

  @Test
  void whenAdpPayGroupEqualsInOpCoAndOpCoDTOAndNotDuplicateAdpPayGroupOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateAdpPayGroupOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.ADP_PAY_GROUP_FIELD_NAME));
  }

  @Test
  void whenAdpPayGroupNotEqualsInOpCoAndOpCoDTOAndDuplicateAdpPayGroupOnEdit_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = new OpCo();
    opCo.setAdpPayGroup("QQQ");
    OpCoUtil.checkDuplicateAdpPayGroupOnEdit(true, opCoDTO, opCo, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.ADP_PAY_GROUP_FIELD_NAME));
    assertEquals(opCoDTO.getAdpPayGroup(), duplicates.get(OpCoUtil.ADP_PAY_GROUP_FIELD_NAME));
  }

  @Test
  void whenAdpPayGroupNotEqualsInOpCoAndOpCoDTOAndNotDuplicateAdpPayGroupOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    opCo.setAdpPayGroup("QQQ");
    OpCoUtil.checkDuplicateAdpPayGroupOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.ADP_PAY_GROUP_FIELD_NAME));
  }

  @Test
  void whenAdpLocationIdEqualsInOpCoAndOpCoDTOAndDuplicateAdpLocationIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateAdpLocationIdOnEdit(true, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME));
  }

  @Test
  void whenAdpLocationIdEqualsInOpCoAndOpCoDTOAndNotDuplicateAdpLocationIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    OpCoUtil.checkDuplicateAdpLocationIdOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME));
  }

  @Test
  void whenAdpLocationIdNotEqualsInOpCoAndOpCoDTOAndDuplicateAdpLocationIdOnEdit_thenAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = new OpCo();
    opCo.setAdpLocationId(2);
    OpCoUtil.checkDuplicateAdpLocationIdOnEdit(true, opCoDTO, opCo, duplicates);
    assertTrue(duplicates.containsKey(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME));
    assertEquals(String.valueOf(opCoDTO.getAdpLocationId()), duplicates.get(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME));
  }

  @Test
  void whenAdpLocationIdNotEqualsInOpCoAndOpCoDTOAndNotDuplicateAdpLocationIdOnEdit_thenNotAddToDuplicatesMap() {
    Map<String, String> duplicates = new HashMap<>();
    OpCoDTO opCoDTO = createOpCoDTO();
    OpCo opCo = opCoDTO.merge(new OpCo());
    opCo.setAdpLocationId(2);
    OpCoUtil.checkDuplicateAdpLocationIdOnEdit(false, opCoDTO, opCo, duplicates);
    assertFalse(duplicates.containsKey(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME));
  }

  @Test
  void whenDuplicatesOpCoFieldsListNotEmpty_thenThrowException() {
    OpCoDTO opCoDTO = createOpCoDTO();
    Map<String, String> duplicates = new HashMap<>();
    duplicates.put(OpCoUtil.OPCO_NUMBER_FIELD_NAME, opCoDTO.getOpCoNumber());
    duplicates.put(OpCoUtil.ADP_LOCATION_ID_FIELD_NAME, String.valueOf(opCoDTO.getAdpLocationId()));
    assertThrows(DuplicateRecordException.class, () -> OpCoUtil.checkDuplicateOpCoFields(duplicates, opCoDTO));

    DuplicateRecordException duplicateRecordException = assertThrows(DuplicateRecordException.class,
        () -> OpCoUtil.checkDuplicateOpCoFields(duplicates, opCoDTO)
    );
    assertEquals(duplicateRecordException.getErrorDTO().getCode(), ErrorCode.DUPLICATE_RECORD.getCode());
    assertEquals(duplicateRecordException.getErrorDTO().getMessage(), ErrorCode.DUPLICATE_RECORD.getDescription());
    assertEquals(duplicateRecordException.getErrorDTO().getErrorData(), duplicates);
    assertEquals(duplicateRecordException.getErrorDTO().getOriginalData(), opCoDTO);
  }

  @Test
  void whenDuplicatesOpCoFieldsIsEmpty_thenDoesNotThrowException() {
    OpCoDTO opCoDTO = createOpCoDTO();
    Map<String, String> duplicates = new HashMap<>();
    assertDoesNotThrow(() -> OpCoUtil.checkDuplicateOpCoFields(duplicates, opCoDTO));
  }
}
