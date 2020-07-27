package com.sysco.rps.service.masterdata;

import com.sysco.rps.dto.ListResponse;
import com.sysco.rps.dto.masterdata.OpCoDTO;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.RecordNotFoundException;

import com.sysco.rps.service.exception.ValidationException;
import org.springframework.data.domain.Pageable;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/19/20 Time: 12:54 PM
 */

public interface OpCoService {

  /**
   * Find an OpCo by OpCo Number
   *
   * @param opCoNumber OpCo Number
   * @return OpCO DTO object
   * @throws RecordNotFoundException If the OpCo not available
   */
  OpCoDTO findByOpCoNumber(String opCoNumber) throws RecordNotFoundException;

  /**
   * Find all OpCos by Country Code
   *
   * @param countryCode Country code of the OpCo
   * @param pageable Allow pagination
   * @return List of OpCo DTO objects
   * @throws RecordNotFoundException If the OpCos not available
   */
  ListResponse<OpCoDTO> findAllOpCos(String countryCode, Pageable pageable) throws RecordNotFoundException;

  /**
   * Creates a new OpCo
   *
   * @param opCoDTO OpCo DTO object
   * @return OpCo DTO if successful, otherwise Error DTO
   * @throws ValidationException If the validation fails
   * @throws DuplicateRecordException If the OpCo already available
   */
  OpCoDTO saveOpCo(OpCoDTO opCoDTO) throws ValidationException, DuplicateRecordException;

  /**
   * Updates an existing OpCo
   *
   * @param opCoNumber OpCo Number
   * @param opCoDTO OpCo DTO object
   * @return OpCo DTO if successful, otherwise Error DTO
   * @throws RecordNotFoundException If the OpCo not available
   * @throws ValidationException If the validation fails
   */
  OpCoDTO updateOpCo(String opCoNumber, OpCoDTO opCoDTO) throws  RecordNotFoundException, ValidationException, DuplicateRecordException;
}
