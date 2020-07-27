package com.sysco.rps.service.masterdata;

import com.sysco.rps.dto.pp.ListResponse;
import com.sysco.rps.dto.pp.masterdata.OpCoDTO;
import com.sysco.rps.entity.pp.masterdata.OpCo;
import com.sysco.rps.repository.pp.masterdata.OpCoRepository;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.exception.ValidationException;

import com.sysco.rps.util.OpCoUtil;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/19/20 Time: 12:54 PM
 */

@Service
public class OpCoServiceImpl implements OpCoService {

  private static final Logger logger = LoggerFactory.getLogger(OpCoServiceImpl.class);

  @Autowired
  OpCoRepository opCoRepository;

  @Autowired
  OpCoValidationService opCoValidationService;

  private static final String ERROR_MESSAGE_OPCO_NOT_FOUND = "OpCo with opco number {0} not found";


  @Override
  public OpCoDTO findByOpCoNumber(String opCoNumber) throws RecordNotFoundException {
    logger.info("retrieving opco with opco number {}", opCoNumber);
    OpCo opCo = findOpCoByOpCoNumber(opCoNumber);
    logger.info("successfully retrieved opco with opco number {}", opCoNumber);
    return new OpCoDTO(opCo);
  }

  @Override
  public ListResponse<OpCoDTO> findAllOpCos(String countryCode, Pageable pageable) throws RecordNotFoundException {
    logger.info("retrieving opcos with country code {}", countryCode);
    Page<OpCo> opCoList = opCoRepository.findAllByCountryCodeAndIsCurrentTrue(countryCode, pageable);
    OpCoUtil.checkOpCoListEmpty(opCoList, countryCode);
    List<OpCoDTO> opCoDTOList = opCoList.stream().map(OpCoDTO::new).collect(Collectors.toList());
    logger.info("successfully retrieved {} opcos with country code {}", opCoDTOList.size(), countryCode);
    return new ListResponse<>(opCoList.getTotalElements(), opCoList.getTotalPages(), opCoDTOList);
  }

  @Override
  public OpCoDTO saveOpCo(OpCoDTO opCoDTO) throws ValidationException, DuplicateRecordException {
    opCoValidationService.opCoDTOValidator.validate(opCoDTO);
    logger.info("successfully validated opCoDTO with opco number {}", opCoDTO.getOpCoNumber());
    Map<String, String> duplicateOpCoFields = new HashMap<>();
    OpCoUtil.checkDuplicateOpCoNumberOnInsert(isOpCoNumberExist(opCoDTO.getOpCoNumber()), opCoDTO, duplicateOpCoFields);
    OpCoUtil.checkDuplicateWorkdayNameOnInsert(isWorkdayNameExist(opCoDTO.getWorkdayName()), opCoDTO, duplicateOpCoFields);
    OpCoUtil.checkDuplicateSapEntityIdOnInsert(isSapEntityIdExist(opCoDTO.getSapEntityId()), opCoDTO, duplicateOpCoFields);
    OpCoUtil.checkDuplicateSusEntityIdOnInsert(isSusEntityIdExist(opCoDTO.getSusEntityId()), opCoDTO, duplicateOpCoFields);
    OpCoUtil.checkDuplicateAdpPayGroupOnInsert(isAdpPayGroupExist(opCoDTO.getAdpPayGroup()), opCoDTO, duplicateOpCoFields);
    OpCoUtil.checkDuplicateAdpLocationIdOnInsert(isAdpLocationIdExist(opCoDTO.getAdpLocationId()), opCoDTO, duplicateOpCoFields);
    OpCoUtil.checkDuplicateOpCoFields(duplicateOpCoFields, opCoDTO);
    logger.info("no duplicate fields found on insertion for the opCoDTO with opco number {}", opCoDTO.getOpCoNumber());
    OpCo opCo = new OpCo();
    opCo = opCoDTO.merge(opCo);
    logger.info("saving opco with opco number {}", opCoDTO.getOpCoNumber());
    opCo = opCoRepository.save(opCo);
    logger.info("successfully saved opco with opco number {}", opCoDTO.getOpCoNumber());
    return new OpCoDTO(opCo);
  }

  @Override
  public OpCoDTO updateOpCo(String opCoNumber, OpCoDTO opCoDTO) throws ValidationException, RecordNotFoundException, DuplicateRecordException {
    opCoValidationService.opCoDTOValidator.validate(opCoDTO);
    logger.info("successfully validated opCoDTO with opco number {}", opCoDTO.getOpCoNumber());
    OpCo opCo = findOpCoByOpCoNumber(opCoNumber);
    Map<String, String> duplicateOpCoFields = new HashMap<>();
    OpCoUtil.checkDuplicateOpCoNumberOnEdit(isOpCoNumberExist(opCoDTO.getOpCoNumber()), opCoDTO, opCo, duplicateOpCoFields);
    OpCoUtil.checkDuplicateWorkdayNameOnEdit(isWorkdayNameExist(opCoDTO.getWorkdayName()), opCoDTO, opCo, duplicateOpCoFields);
    OpCoUtil.checkDuplicateSapEntityIdOnEdit(isSapEntityIdExist(opCoDTO.getSapEntityId()), opCoDTO, opCo, duplicateOpCoFields);
    OpCoUtil.checkDuplicateSusEntityIdOnEdit(isSusEntityIdExist(opCoDTO.getSusEntityId()), opCoDTO, opCo, duplicateOpCoFields);
    OpCoUtil.checkDuplicateAdpPayGroupOnEdit(isAdpPayGroupExist(opCoDTO.getAdpPayGroup()), opCoDTO, opCo, duplicateOpCoFields);
    OpCoUtil.checkDuplicateAdpLocationIdOnEdit(isAdpLocationIdExist(opCoDTO.getAdpLocationId()), opCoDTO, opCo, duplicateOpCoFields);
    OpCoUtil.checkDuplicateOpCoFields(duplicateOpCoFields, opCoDTO);
    logger.info("no duplicate fields found on update for the opCoDTO with opco number {}", opCoDTO.getOpCoNumber());
    opCo = opCoDTO.merge(opCo);
    logger.info("updating opco with opco number {}", opCoDTO.getOpCoNumber());
    opCo = opCoRepository.save(opCo);
    logger.info("successfully updated opco with opco number {}", opCoDTO.getOpCoNumber());
    return new OpCoDTO(opCo);
  }

  private OpCo findOpCoByOpCoNumber(String opCoNumber) throws RecordNotFoundException {
    return opCoRepository.findByOpCoNumberAndIsCurrentTrue(opCoNumber).orElseThrow(() -> new RecordNotFoundException(
        MessageFormat.format(ERROR_MESSAGE_OPCO_NOT_FOUND, opCoNumber)));
  }

  private boolean isOpCoNumberExist(String opCoNumber) {
    return opCoRepository.existsByOpCoNumberAndIsCurrentTrue(opCoNumber);
  }

  private boolean isWorkdayNameExist(String workdayName) {
    return opCoRepository.existsByWorkdayNameAndIsCurrentTrue(workdayName);
  }

  private boolean isSapEntityIdExist(Integer sapEntityId) {
    return opCoRepository.existsBySapEntityIdAndIsCurrentTrue(sapEntityId);
  }

  private boolean isSusEntityIdExist(Integer susEntityId) {
    return opCoRepository.existsBySusEntityIdAndIsCurrentTrue(susEntityId);
  }

  private boolean isAdpPayGroupExist(String adpPayGroup) {
    return opCoRepository.existsByAdpPayGroupAndIsCurrentTrue(adpPayGroup);
  }

  private boolean isAdpLocationIdExist(Integer adpLocationId) {
    return opCoRepository.existsByAdpLocationIdAndIsCurrentTrue(adpLocationId);
  }
}
