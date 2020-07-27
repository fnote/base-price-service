package com.sysco.rps.controller.masterdata;

import com.sysco.rps.dto.pp.masterdata.KeyValueDTO;
import com.sysco.rps.entity.pp.masterdata.enums.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Controller
@RequestMapping("/ref-price/v1/master-data/enums")
public class EnumController extends AbstractController {


  @GetMapping("/activation-statuses")
  @ApiOperation(value = "Returns activation-statuses", notes = "Returns activation-statuses", response = KeyValueDTO.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "activation status found"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized.")
  })
  public @ResponseBody
  ResponseEntity<List<KeyValueDTO>> getActivationStatuses() {
    List<KeyValueDTO> list = new ArrayList<>();
    EnumSet.allOf(ActivationStatus.class).forEach(c -> list.add(new KeyValueDTO(c.name(), c.getValue())));
    return ResponseEntity.status(HttpStatus.OK)
        .body(list);
  }

  @GetMapping("/timezones")
  @ApiOperation(value = "Returns timezones", notes = "Returns timezones", response = KeyValueDTO.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "timezones"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized.")
  })
  public @ResponseBody
  ResponseEntity<List<KeyValueDTO>> getTimezones() {
    List<KeyValueDTO> list = new ArrayList<>();
    EnumSet.allOf(Timezone.class).forEach(c -> list.add(new KeyValueDTO(c.name(), c.getValue())));
    return ResponseEntity.status(HttpStatus.OK)
        .body(list);
  }

  @GetMapping("/units-of-length")
  @ApiOperation(value = "Returns units of length", notes = "Returns units of length", response = KeyValueDTO.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "units of length"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized.")
  })
  public @ResponseBody
  ResponseEntity<List<KeyValueDTO>> getUnitsOfLength() {
    List<KeyValueDTO> list = new ArrayList<>();
    EnumSet.allOf(UnitOfLength.class).forEach(c -> list.add(new KeyValueDTO(c.name(), c.getValue())));
    return ResponseEntity.status(HttpStatus.OK)
        .body(list);
  }

  @GetMapping("/markets")
  @ApiOperation(value = "Returns markets", notes = "Returns markets", response = KeyValueDTO.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "markets"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized.")
  })
  public @ResponseBody
  ResponseEntity<List<KeyValueDTO>> getMarkets() {
    List<KeyValueDTO> list = new ArrayList<>();
    EnumSet.allOf(Market.class).forEach(c -> list.add(new KeyValueDTO(c.name(), c.getValue())));
    return ResponseEntity.status(HttpStatus.OK)
        .body(list);
  }


  @GetMapping("/stop-classes")
  @ApiOperation(value = "Returns stop-classes", notes = "Returns stop-classes", response = KeyValueDTO.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "stop-classes"),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized.")
  })
  public @ResponseBody
  ResponseEntity<List<KeyValueDTO>> getStopClasses() {
    List<KeyValueDTO> list = new ArrayList<>();
    EnumSet.allOf(StopClass.class).forEach(c -> list.add(new KeyValueDTO(c.name(), c.getValue())));
    return ResponseEntity.status(HttpStatus.OK)
        .body(list);
  }
}
