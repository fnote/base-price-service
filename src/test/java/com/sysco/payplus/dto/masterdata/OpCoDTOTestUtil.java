package com.sysco.payplus.dto.masterdata;

import com.sysco.payplus.entity.masterdata.enums.ActivationStatus;
import com.sysco.payplus.entity.masterdata.enums.Currency;
import com.sysco.payplus.entity.masterdata.enums.Market;
import com.sysco.payplus.entity.masterdata.enums.Timezone;
import com.sysco.payplus.entity.masterdata.enums.UnitOfLength;
import java.util.ArrayList;
import java.util.List;

public class OpCoDTOTestUtil {

  public OpCoDTO createOpCoDTO() {
    OpCoDTO opCoDTO = new OpCoDTO();
    opCoDTO.setOpCoNumber("US0001");
    opCoDTO.setWorkdayName("Sysco Test 1");
    opCoDTO.setSapEntityId(1234);
    opCoDTO.setSusEntityId(1);
    opCoDTO.setAdpPayGroup("SSS");
    opCoDTO.setAdpLocationId(123);
    opCoDTO.setCountryCode("US");
    opCoDTO.setMarket(Market.MIDWEST.toString());
    opCoDTO.setTimezone(Timezone.CENTRAL.toString());
    opCoDTO.setActiveStatus(ActivationStatus.ACTIVE.toString());
    opCoDTO.setTargetPiecesPerTrip(850);
    opCoDTO.setLocked(false);
    return opCoDTO;
  }

  public OpCoDTO createOpCoDTO(String opCoNumber, String workdayName, Integer sapEntityId, Integer susEntityId, String adpPayGroup,
      Integer adpLocationId) {
    OpCoDTO opCoDTO = new OpCoDTO();
    opCoDTO.setOpCoNumber(opCoNumber);
    opCoDTO.setWorkdayName(workdayName);
    opCoDTO.setSapEntityId(sapEntityId);
    opCoDTO.setSusEntityId(susEntityId);
    opCoDTO.setAdpPayGroup(adpPayGroup);
    opCoDTO.setAdpLocationId(adpLocationId);
    opCoDTO.setCountryCode("US");
    opCoDTO.setMarket(Market.MIDWEST.toString());
    opCoDTO.setTimezone(Timezone.CENTRAL.toString());
    opCoDTO.setActiveStatus(ActivationStatus.ACTIVE.toString());
    opCoDTO.setTargetPiecesPerTrip(850);
    opCoDTO.setLocked(false);
    return opCoDTO;
  }

  public List<OpCoDTO> createOpCoDTOList() {
    List<OpCoDTO> opCoDTOList = new ArrayList<>();
    OpCoDTO opCoDTO1 = new OpCoDTO();
    opCoDTO1.setOpCoNumber("US0001");
    opCoDTO1.setWorkdayName("Sysco Test 1");
    opCoDTO1.setSapEntityId(111);
    opCoDTO1.setSusEntityId(1);
    opCoDTO1.setAdpPayGroup("SSS");
    opCoDTO1.setAdpLocationId(111);
    opCoDTO1.setCountryCode("US");
    opCoDTO1.setMarket(Market.MIDWEST.toString());
    opCoDTO1.setTimezone(Timezone.CENTRAL.toString());
    opCoDTO1.setActiveStatus(ActivationStatus.ACTIVE.toString());
    opCoDTO1.setTargetPiecesPerTrip(850);
    opCoDTO1.setLocked(false);
    opCoDTOList.add(opCoDTO1);

    OpCoDTO opCoDTO = new OpCoDTO();
    opCoDTO.setOpCoNumber("US0002");
    opCoDTO.setWorkdayName("Sysco Test 2");
    opCoDTO.setSapEntityId(222);
    opCoDTO.setSusEntityId(2);
    opCoDTO.setAdpPayGroup("TTT");
    opCoDTO.setAdpLocationId(2222);
    opCoDTO.setCountryCode("US");
    opCoDTO.setMarket(Market.MIDWEST.toString());
    opCoDTO.setTimezone(Timezone.CENTRAL.toString());
    opCoDTO.setActiveStatus(ActivationStatus.ACTIVE.toString());
    opCoDTO.setTargetPiecesPerTrip(850);
    opCoDTO.setLocked(false);

    return opCoDTOList;
  }
}
