package com.sysco.rps.controller.masterdata;

import com.sysco.rps.dto.pp.ListResponse;
import com.sysco.rps.dto.pp.masterdata.OpCoDTO;
import com.sysco.rps.service.masterdata.OpCoService;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.exception.ValidationException;
import com.sysco.rps.validators.annotations.ValidOpCoNumberFormat;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/19/20 Time: 12:54 PM
 */

@RestController
@RequestMapping("/ref-price/v1/master-data")
@Validated
public class OpCoController {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpCoController.class);

  @Autowired
  private OpCoService opCoService;

  @GetMapping("/opcos/{opco-number}")
  @ApiOperation(value = "Returns the OpCo given by the OpCo Number", notes = "Returns the OpCo given by the OpCo Number", response = OpCoDTO.class)
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "OpCo found"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authenticated"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "OpCo not found")
  })
  public ResponseEntity<OpCoDTO> findOpCo(
      @ApiParam(value = "OpCo Number", example = "US0075", required = true)
      @ValidOpCoNumberFormat(message = "OpCo number should have 2 letters followed by 4 digits") @PathVariable(name = "opco-number") String opCoNumber)
      throws RecordNotFoundException {
    OpCoDTO opCoDTO = opCoService.findByOpCoNumber(opCoNumber);
    return new ResponseEntity<>(opCoDTO, HttpStatus.OK);
  }

  @GetMapping(value = "/opcos")
  @ApiOperation(value = "Returns all the OpCos for a given country, specified by country code",
      notes = "Returns all the OpCos for a given country, specified by country code", response = OpCoDTO.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "OpCo found"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authenticated"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "OpCo not found")
  })
  public ResponseEntity<ListResponse<OpCoDTO>> findOpCos(
      @ApiParam(value = "Country code", required = true)
      @Length(min = 2, max = 3, message = "Length of the Country Code should be between 2 to 3") @RequestParam(name = "country_code") String countryCode,
      @ApiParam(value = "Page number to fetch") @RequestParam(name = "page", required = false) Integer page,
      @ApiParam(value = "Number of records per page") @RequestParam(name = "page_size", required = false) Integer pageSize)
      throws RecordNotFoundException {

    Pageable pageable;
    if (page == null || pageSize == null) {
      //send everything
      pageable = PageRequest.of(0, Integer.MAX_VALUE);
    } else {
      pageable = PageRequest.of(page, pageSize);
    }

    ListResponse<OpCoDTO> opCoDTOList = opCoService.findAllOpCos(countryCode, pageable);
    return new ResponseEntity<>(opCoDTOList, HttpStatus.OK);
  }

  @PostMapping("/opco")
  @ApiOperation(value = "Creates a new OpCo ", notes = "Creates a new OpCo", response = OpCoDTO.class)
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_CREATED, message = "OpCo created"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "OpCo validation failed"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authenticated"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_CONFLICT, message = "OpCo already available")
  })
  public ResponseEntity<OpCoDTO> createOpCo(@ApiParam(value = "OpCo details", required = true) @RequestBody OpCoDTO opCoDTO)
      throws ValidationException, DuplicateRecordException {
    LOGGER.debug("create opco request {}", opCoDTO);
    opCoDTO = opCoService.saveOpCo(opCoDTO);
    return new ResponseEntity<>(opCoDTO, HttpStatus.CREATED);
  }

  @PutMapping("/opcos/{opco-number}")
  @ApiOperation(value = "Updates the given OpCo ", notes = "Updates the given OpCo", response = OpCoDTO.class)
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_NO_CONTENT, message = "OpCo updated"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, message = "OpCo validation failed"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authenticated"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND, message = "OpCo not found"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_CONFLICT, message = "OpCo locked or duplicate record")
  })
  public ResponseEntity<Void> updateOpCo(
      @ApiParam(value = "OpCo Number", required = true)
      @ValidOpCoNumberFormat(message = "OpCo number should have 2 letters followed by 4 digits") @PathVariable(name = "opco-number") String opCoNumber,
      @ApiParam(value = "OpCo details", required = true) @RequestBody OpCoDTO opCoDTO)
      throws ValidationException, RecordNotFoundException, DuplicateRecordException {
    LOGGER.debug("update opco request {}", opCoDTO);
    opCoService.updateOpCo(opCoNumber, opCoDTO);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
