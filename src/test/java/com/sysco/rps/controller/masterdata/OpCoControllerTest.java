package com.sysco.rps.controller.masterdata;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.rps.dto.ListResponse;
import com.sysco.rps.dto.masterdata.OpCoDTO;
import com.sysco.rps.dto.masterdata.OpCoDTOTestUtil;
import com.sysco.rps.service.exception.DuplicateRecordException;
import com.sysco.rps.service.exception.RecordNotFoundException;
import com.sysco.rps.service.masterdata.OpCoService;
import com.sysco.rps.service.security.IntrospectRestClientService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(OpCoController.class)
@EnableAutoConfiguration
class OpCoControllerTest extends OpCoDTOTestUtil {

  private final String API_PATH = "/payplus/v1/master-data";
  @MockBean
  IntrospectRestClientService introspectRestClientService;
  @Autowired
  private MockMvc mvc;
  @MockBean
  @Qualifier("applicationUserService")
  private UserDetailsService userDetailsService;
  @MockBean
  private OpCoService opCoService;

  //TODO: This test case should return 401
  @Test
  @DisplayName("PAYP-235 | PAYP-242 | PAYP-247 | PAYP-251 Verify invalid authentication")
  void whenNotAuthenticated_thenUnAuthorized() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0001")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());

    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());

    mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/opco")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());

    mvc.perform(MockMvcRequestBuilders.put(API_PATH + "/opcos/US0001")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-237 | PAYP-243 | PAYP-248 | PAYP-252 Verify invalid authorization")
  @WithMockUser(username = "admin", roles = {"NONE"})
  void whenNotAuthorized_thenForbidden() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());

    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0075")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());

    mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/opco")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());

    mvc.perform(MockMvcRequestBuilders.put(API_PATH + "/opcos/US0001")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-249 Verify find all OpCos for valid query parameters")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenFindOpCo_thenSuccess() throws Exception {
    OpCoDTO opCoDTO = createOpCoDTO();
    when(opCoService.findByOpCoNumber(opCoDTO.getOpCoNumber())).thenReturn(opCoDTO);
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0001"))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-245 Verify find all OpCos for valid query parameters")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
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
  @WithMockUser(username = "admin", roles = {"ADMIN"})
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
  @WithMockUser(username = "admin", roles = {"ADMIN"})
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
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenFindOpCo_thenRecordNotFoundException() throws Exception {
    when(opCoService.findByOpCoNumber("US0002")).thenThrow(RecordNotFoundException.class);
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0002")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-236 Verify adding an OpCo for duplicate records")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
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
  @WithMockUser(username = "admin", roles = {"ADMIN"})
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
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenFindOpCos_thenRecordNotFoundException() throws Exception {
    when(opCoService.findAllOpCos("US", PageRequest.of(0, Integer.MAX_VALUE))).thenThrow(RecordNotFoundException.class);
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos?country_code=US")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @DisplayName("PAYP-250 Verify find OpCo for invalid OpCo Number")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenFindOpCo_thenConstraintViolationException() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US001"))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
}
