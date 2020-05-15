package com.sysco.payplus.util;

import static com.sysco.payplus.service.exception.ErrorCode.DUPLICATE_RECORD;

import com.sysco.payplus.dto.ErrorDTO;
import com.sysco.payplus.dto.masterdata.OpCoDTO;
import com.sysco.payplus.entity.masterdata.OpCo;
import com.sysco.payplus.service.exception.DuplicateRecordException;
import com.sysco.payplus.service.exception.RecordNotFoundException;
import java.text.MessageFormat;
import java.util.Map;
import org.springframework.data.domain.Page;

public class OpCoUtil {

  public static final String OPCO_NUMBER_FIELD_NAME = "opCoNumber";
  public static final String WORKDAY_NAME_FIELD_NAME = "workdayName";
  public static final String SAP_ENTITY_ID_FIELD_NAME = "sapEntityId";
  public static final String SUS_ENTITY_ID_FIELD_NAME = "susEntityId";
  public static final String ADP_PAY_GROUP_FIELD_NAME = "adpPayGroup";
  public static final String ADP_LOCATION_ID_FIELD_NAME = "adpLocationId";

  private static final String ERROR_MESSAGE_DUPLICATE_OPCO_FIELDS = "Duplicate OpCo fields";
  private static final String ERROR_MESSAGE_OPCOS_NOT_FOUND = "No OpCo records found for the Country Code: {0}";

  private OpCoUtil() {
  }

  public static void checkOpCoListEmpty(Page<OpCo> opCoList, String countryCode) throws RecordNotFoundException {
    if (opCoList.isEmpty()) {
      throw new RecordNotFoundException(MessageFormat.format(ERROR_MESSAGE_OPCOS_NOT_FOUND, countryCode));
    }
  }

  public static void checkDuplicateOpCoNumberOnInsert(boolean isDuplicate, OpCoDTO opCoDTO, Map<String, String> duplicates) {
    if (isDuplicate) {
      duplicates.put(OPCO_NUMBER_FIELD_NAME, opCoDTO.getOpCoNumber());
    }
  }

  public static void checkDuplicateWorkdayNameOnInsert(boolean isDuplicate, OpCoDTO opCoDTO, Map<String, String> duplicates) {
    if (isDuplicate) {
      duplicates.put(WORKDAY_NAME_FIELD_NAME, opCoDTO.getWorkdayName());
    }
  }

  public static void checkDuplicateSapEntityIdOnInsert(boolean isDuplicate, OpCoDTO opCoDTO, Map<String, String> duplicates) {
    if (isDuplicate) {
      duplicates.put(SAP_ENTITY_ID_FIELD_NAME, String.valueOf(opCoDTO.getSapEntityId()));
    }
  }

  public static void checkDuplicateSusEntityIdOnInsert(boolean isDuplicate, OpCoDTO opCoDTO, Map<String, String> duplicates) {
    if (isDuplicate) {
      duplicates.put(SUS_ENTITY_ID_FIELD_NAME, String.valueOf(opCoDTO.getSusEntityId()));
    }
  }

  public static void checkDuplicateAdpPayGroupOnInsert(boolean isDuplicate, OpCoDTO opCoDTO, Map<String, String> duplicates) {
    if (isDuplicate) {
      duplicates.put(ADP_PAY_GROUP_FIELD_NAME, opCoDTO.getAdpPayGroup());
    }
  }

  public static void checkDuplicateAdpLocationIdOnInsert(boolean isDuplicate, OpCoDTO opCoDTO, Map<String, String> duplicates) {
    if (isDuplicate) {
      duplicates.put(ADP_LOCATION_ID_FIELD_NAME, String.valueOf(opCoDTO.getAdpLocationId()));
    }
  }

  public static void checkDuplicateOpCoNumberOnEdit(boolean isDuplicate, OpCoDTO opCoDTO, OpCo opCo, Map<String, String> duplicates) {
    if (!opCo.getOpCoNumber().equals(opCoDTO.getOpCoNumber()) && isDuplicate) {
      duplicates.put(OPCO_NUMBER_FIELD_NAME, opCoDTO.getOpCoNumber());
    }
  }

  public static void checkDuplicateWorkdayNameOnEdit(boolean isDuplicate, OpCoDTO opCoDTO, OpCo opCo, Map<String, String> duplicates) {
    if (!opCo.getWorkdayName().equals(opCoDTO.getWorkdayName()) && isDuplicate) {
      duplicates.put(WORKDAY_NAME_FIELD_NAME, opCoDTO.getWorkdayName());
    }
  }

  public static void checkDuplicateSapEntityIdOnEdit(boolean isDuplicate, OpCoDTO opCoDTO, OpCo opCo, Map<String, String> duplicates) {
    if (!opCo.getSapEntityId().equals(opCoDTO.getSapEntityId()) && isDuplicate) {
      duplicates.put(SAP_ENTITY_ID_FIELD_NAME, String.valueOf(opCoDTO.getSapEntityId()));
    }
  }

  public static void checkDuplicateSusEntityIdOnEdit(boolean isDuplicate, OpCoDTO opCoDTO, OpCo opCo, Map<String, String> duplicates) {
    if (!opCo.getSusEntityId().equals(opCoDTO.getSusEntityId()) && isDuplicate) {
      duplicates.put(SUS_ENTITY_ID_FIELD_NAME, String.valueOf(opCoDTO.getSusEntityId()));
    }
  }

  public static void checkDuplicateAdpPayGroupOnEdit(boolean isDuplicate, OpCoDTO opCoDTO, OpCo opCo, Map<String, String> duplicates) {
    if (!opCo.getAdpPayGroup().equals(opCoDTO.getAdpPayGroup()) && isDuplicate) {
      duplicates.put(ADP_PAY_GROUP_FIELD_NAME, opCoDTO.getAdpPayGroup());
    }
  }

  public static void checkDuplicateAdpLocationIdOnEdit(boolean isDuplicate, OpCoDTO opCoDTO, OpCo opCo, Map<String, String> duplicates) {
    if (!opCo.getAdpLocationId().equals(opCoDTO.getAdpLocationId()) && isDuplicate) {
      duplicates.put(ADP_LOCATION_ID_FIELD_NAME, String.valueOf(opCoDTO.getAdpLocationId()));
    }
  }

  public static void checkDuplicateOpCoFields(Map<String, String> duplicates, OpCoDTO opCoDTO) throws DuplicateRecordException {
    if (!duplicates.isEmpty()) {
      ErrorDTO errorDTO = new ErrorDTO(DUPLICATE_RECORD.getCode(), DUPLICATE_RECORD.getDescription(), opCoDTO, duplicates);
      throw new DuplicateRecordException(ERROR_MESSAGE_DUPLICATE_OPCO_FIELDS, errorDTO);
    }
  }
}
