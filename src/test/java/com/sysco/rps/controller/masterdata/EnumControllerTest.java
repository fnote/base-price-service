package com.sysco.rps.controller.masterdata;


import com.sysco.rps.dto.masterdata.KeyValueDTO;
import com.sysco.rps.entity.masterdata.enums.*;
import com.sysco.rps.service.security.IntrospectRestClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnumController.class)
@EnableAutoConfiguration
class EnumControllerTest {

  private final String API_PATH = "/payplus/v1/master-data/enums";
  @Autowired
  private MockMvc mvc;

  @MockBean
  @Qualifier("applicationUserService")
  private UserDetailsService userDetailsService;
  @MockBean
  IntrospectRestClientService introspectRestClientService;



  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenGetActivationStatuses_thenListOfKeyValueDTOsOfActivationStatuses() throws Exception {
    List<KeyValueDTO> dtoList = Stream.of(ActivationStatus.values()).
        map(c -> new KeyValueDTO(c.name(), c.getValue())).
        collect(Collectors.toList());

    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/activation-statuses")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(dtoList.size())))
        .andExpect(jsonPath("$[0].key", is(dtoList.get(0).getKey())))
        .andExpect(jsonPath("$[0].displayString", is(dtoList.get(0).getDisplayString())))
        .andExpect(jsonPath("$[1].key", is(dtoList.get(1).getKey())))
        .andExpect(jsonPath("$[1].displayString", is(dtoList.get(1).getDisplayString())))
        .andExpect(status().isOk())
        .andDo(print());
  }


  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenGetTimezones_thenListOfKeyValueDTOsOfTimezones() throws Exception {
    List<KeyValueDTO> dtoList = Stream.of(Timezone.values()).
        map(c -> new KeyValueDTO(c.name(), c.getValue())).
        collect(Collectors.toList());
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/timezones")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(dtoList.size())))
        .andExpect(jsonPath("$[0].key", is(dtoList.get(0).getKey())))
        .andExpect(jsonPath("$[0].displayString", is(dtoList.get(0).getDisplayString())))
        .andExpect(jsonPath("$[1].key", is(dtoList.get(1).getKey())))
        .andExpect(jsonPath("$[1].displayString", is(dtoList.get(1).getDisplayString())))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenGetUnitsOfLength_thenListOfKeyValueDTOsOfUnitsOfLength() throws Exception {
    List<KeyValueDTO> dtoList = Stream.of(UnitOfLength.values()).
        map(c -> new KeyValueDTO(c.name(), c.getValue())).
        collect(Collectors.toList());
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/units-of-length")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(dtoList.size())))
        .andExpect(jsonPath("$[0].key", is(dtoList.get(0).getKey())))
        .andExpect(jsonPath("$[0].displayString", is(dtoList.get(0).getDisplayString())))
        .andExpect(jsonPath("$[1].key", is(dtoList.get(1).getKey())))
        .andExpect(jsonPath("$[1].displayString", is(dtoList.get(1).getDisplayString())))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenGetMarkets_thenListOfKeyValueDTOsOfMarkets() throws Exception {
    List<KeyValueDTO> dtoList = Stream.of(Market.values()).
        map(c -> new KeyValueDTO(c.name(), c.getValue())).
        collect(Collectors.toList());
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/markets")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(dtoList.size())))
        .andExpect(jsonPath("$[0].key", is(dtoList.get(0).getKey())))
        .andExpect(jsonPath("$[0].displayString", is(dtoList.get(0).getDisplayString())))
        .andExpect(jsonPath("$[4].key", is(dtoList.get(4).getKey())))
        .andExpect(jsonPath("$[4].displayString", is(dtoList.get(4).getDisplayString())))
        .andExpect(status().isOk())
        .andDo(print());
  }




  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenGetStopClasses_thenListOfKeyValueDTOsOfStopClasses() throws Exception {
    List<KeyValueDTO> dtoList = Stream.of(StopClass.values()).
        map(c -> new KeyValueDTO(c.name(), c.getValue())).
        collect(Collectors.toList());
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/stop-classes")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(dtoList.size())))
        .andExpect(jsonPath("$[0].key", is(dtoList.get(0).getKey())))
        .andExpect(jsonPath("$[0].displayString", is(dtoList.get(0).getDisplayString())))
        .andExpect(status().isOk())
        .andDo(print());
  }
}
