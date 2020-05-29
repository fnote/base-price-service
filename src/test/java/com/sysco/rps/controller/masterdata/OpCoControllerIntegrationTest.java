package com.sysco.rps.controller.masterdata;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.rps.dto.masterdata.OpCoDTO;
import com.sysco.rps.entity.masterdata.enums.ActivationStatus;
import com.sysco.rps.entity.masterdata.enums.Currency;
import com.sysco.rps.entity.masterdata.enums.Market;
import com.sysco.rps.entity.masterdata.enums.Timezone;
import com.sysco.rps.entity.masterdata.enums.UnitOfLength;
import com.sysco.rps.service.security.IntrospectRestClientService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.transaction.Transactional;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Opco Controller Component  Tests")
public class OpCoControllerIntegrationTest {

  private final String API_PATH = "/ref-price/v1/master-data";
  @MockBean
  IntrospectRestClientService introspectRestClientService;
  @Autowired
  private MockMvc mvc;
  @Autowired
  private WebApplicationContext wac;

  private OpCoDTO createOpcoDTO() {
    OpCoDTO opCoDTO = new OpCoDTO();
    opCoDTO.setOpCoNumber("US0002");
    opCoDTO.setWorkdayName("Sysco Test 1");
    opCoDTO.setSapEntityId(12234);
    opCoDTO.setSusEntityId(2);
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

  @Test
  @Transactional
  @DisplayName("Verify response retrieved by 'Add' endpoint for valid request body")
  public void whenSaveOpCoDTO_thenSuccess() throws Exception {
    OpCoDTO opCoDTO = createOpcoDTO();
    String opCoJson = new ObjectMapper().writeValueAsString(opCoDTO);
    mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/opco")
        .content(opCoJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.opCoNumber", is(opCoDTO.getOpCoNumber())))
        .andExpect(jsonPath("$.workdayName", is(opCoDTO.getWorkdayName())))
        .andExpect(jsonPath("$.countryCode", is(opCoDTO.getCountryCode())))
        .andExpect(jsonPath("$.market", is(opCoDTO.getMarket())))
        .andExpect(jsonPath("$.sapEntityId", is(opCoDTO.getSapEntityId())))
        .andExpect(jsonPath("$.susEntityId", is(opCoDTO.getSusEntityId())))
        .andExpect(jsonPath("$.adpPayGroup", is(opCoDTO.getAdpPayGroup())))
        .andExpect(jsonPath("$.adpLocationId", is(opCoDTO.getAdpLocationId())))
        .andExpect(jsonPath("$.timezone", is(opCoDTO.getTimezone())))
        .andExpect(jsonPath("$.targetPiecesPerTrip", is(opCoDTO.getTargetPiecesPerTrip())))
        .andExpect(jsonPath("$.activeStatus", is(opCoDTO.getActiveStatus())))
        .andExpect(status().isCreated())
        .andDo(print());
  }
}
