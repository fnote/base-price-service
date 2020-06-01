package com.sysco.rps.controller.masterdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.rps.dto.ListResponse;
import com.sysco.rps.dto.masterdata.OpCoDTO;
import com.sysco.rps.dto.masterdata.OpCoDTOTestUtil;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.masterdata.OpCoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OpCoController.class)
@EnableAutoConfiguration
class OpCoControllerTest extends OpCoDTOTestUtil {

  private final String API_PATH = "/ref-price/v1/master-data";

  @Autowired
  private MockMvc mvc;
  @MockBean
  private OpCoService opCoService;

  @Test
  @DisplayName("PAYP-249 Verify find all OpCos for valid query parameters")
  void whenFindOpCo_thenSuccess() throws Exception {
    OpCoDTO opCoDTO = createOpCoDTO();
    when(opCoService.findByOpCoNumber(opCoDTO.getOpCoNumber())).thenReturn(opCoDTO);
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0001"))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-245 Verify find all OpCos for valid query parameters")
  void whenFindOpCos_thenSuccess() throws Exception {
    OpCoDTO opCoDTO = createOpCoDTO();
    List<OpCoDTO> opCoDTOList = new ArrayList<>();
    opCoDTOList.add(opCoDTO);
    ListResponse<OpCoDTO> listResponse = new ListResponse<>();
    listResponse.setItems(opCoDTOList);
    when(opCoService.findAllOpCos("US", null)).thenReturn(listResponse);
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos?country_code=US")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-233 Verify adding an OpCo for valid request body")
  void whenSaveOpCoDTO_thenSuccess() throws Exception {
    OpCoDTO opCoDTO = createOpCoDTO();
    String opCoJson = new ObjectMapper().writeValueAsString(opCoDTO);
    when(opCoService.saveOpCo(opCoDTO)).thenReturn(opCoDTO);
    mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/opco")
        .content(opCoJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-239 Verify editing an OpCo for valid request body")
  void whenUpdateOpCoDTO_thenSuccess() throws Exception {
    OpCoDTO opCoDTO = createOpCoDTO();
    String opCoJson = new ObjectMapper().writeValueAsString(opCoDTO);
    when(opCoService.updateOpCo("US0001", opCoDTO)).thenReturn(opCoDTO);
    mvc.perform(MockMvcRequestBuilders.put(API_PATH + "/opcos/US0001")
        .content(opCoJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-250 Verify find OpCo with invalid OpCo Number")
  void whenFindOpCo_thenRecordNotFoundException() throws Exception {
    when(opCoService.findByOpCoNumber("US0002")).thenThrow(RecordNotFoundException.class);
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0002")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-236 Verify adding an OpCo for duplicate records")
  void whenSaveOpCoDTO_thenThrowDuplicateRecordException() throws Exception {
    OpCoDTO opCoDTO = createOpCoDTO();
    String opCoJson = new ObjectMapper().writeValueAsString(opCoDTO);
    when(opCoService.saveOpCo(opCoDTO)).thenThrow(DuplicateRecordException.class);
    mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/opco")
        .content(opCoJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-240 Verify editing an OpCo for non existing OpCo")
  void whenUpdateOpCoDTO_thenRecordNotFoundException() throws Exception {
    OpCoDTO opCoDTO = createOpCoDTO();
    String opCoJson = new ObjectMapper().writeValueAsString(opCoDTO);
    when(opCoService.updateOpCo("US0001", opCoDTO)).thenThrow(RecordNotFoundException.class);
    mvc.perform(MockMvcRequestBuilders.put(API_PATH + "/opcos/US0001")
        .content(opCoJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-246 Verify find all OpCos for invalid query parameters")
  void whenFindOpCos_thenRecordNotFoundException() throws Exception {
    when(opCoService.findAllOpCos("US", PageRequest.of(0, Integer.MAX_VALUE))).thenThrow(RecordNotFoundException.class);
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos?country_code=US")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-250 Verify find OpCo for invalid OpCo Number")
  void whenFindOpCo_thenConstraintViolationException() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US001"))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
}
