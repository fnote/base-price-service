package com.sysco.rps.controller.masterdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.rps.dto.ListResponse;
import com.sysco.rps.dto.masterdata.BaseCustomerDTO;
import com.sysco.rps.dto.masterdata.CustomerDTO;
import com.sysco.rps.entity.masterdata.enums.StopClass;
import com.sysco.rps.service.masterdata.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CustomerController.class)
@EnableAutoConfiguration
class CustomerControllerTest {

  private final String API_PATH = "/ref-price/v1/master-data";
  @Autowired
  private MockMvc mvc;
  @MockBean
  private CustomerService customerService;

  @Test
  void whenAPICalls_thenValidSuccessCodes() throws Exception {
    when(customerService.findCustomers(anyString(), any())).thenReturn(new ListResponse<CustomerDTO>());
    when(customerService.findCustomer(anyString(), anyString()))
            .thenReturn(new CustomerDTO("US0030", "1111111", "John Smith"));
    doAnswer((i) -> null).when(customerService).updateCustomer(anyString(), anyString(), any());

    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0075/customers")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0075/customers/11111111")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());

    BaseCustomerDTO baseCustomerDTO = new BaseCustomerDTO();
    baseCustomerDTO.setStopClass(StopClass.REGULAR);
    String updateRequestBody = new ObjectMapper().writeValueAsString(baseCustomerDTO);
    mvc.perform(MockMvcRequestBuilders.patch(API_PATH + "/opcos/US0075/customers/11111111")
            .content(updateRequestBody)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andDo(print());
  }

  @Test
  @DisplayName("PAYP-457 Verify response retrieved by 'Edit' endpoint for invalid request body")
  void whenUpdateCustomerInvalidRequestBody_thenFailure() throws Exception {
    doAnswer((i) -> null).when(customerService).updateCustomer(anyString(), anyString(), any());

    String updateRequestBody = "{\"stopClass\":\"INVALID\"}";

    mvc.perform(MockMvcRequestBuilders.patch(API_PATH + "/opcos/US0075/customers/11111111")
            .content(updateRequestBody)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print());

  }
}
