package com.sysco.payplus.controller.masterdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.payplus.dto.ListResponse;
import com.sysco.payplus.dto.masterdata.BaseCustomerDTO;
import com.sysco.payplus.dto.masterdata.CustomerDTO;
import com.sysco.payplus.entity.masterdata.enums.StopClass;
import com.sysco.payplus.service.masterdata.CustomerService;
import com.sysco.payplus.service.security.IntrospectRestClientService;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CustomerController.class)
@EnableAutoConfiguration
class CustomerControllerTest {

  private final String API_PATH = "/payplus/v1/master-data";
  @MockBean
  IntrospectRestClientService introspectRestClientService;
  @Autowired
  private MockMvc mvc;
  @MockBean
  private CustomerService customerService;
  @MockBean
  @Qualifier("applicationUserService")
  private UserDetailsService userDetailsService;

  @Test
  @DisplayName("PAYP-458 | PAYP-463 | PAYP-467 all endpoints for unauthenticated user")
  void whenNotAuthenticated_thenForbidden() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0075/customers")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andDo(print());

    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0075/customers/11111111")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andDo(print());

    mvc.perform(MockMvcRequestBuilders.patch(API_PATH + "/opcos/US0075/customers/11111111")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andDo(print());
  }

  @Test
  @DisplayName("PAYP-459 | PAYP-464 | PAYP-468 all endpoints for unauthorized user")
  @WithMockUser(username = "admin", roles = {"NONE"})
  void whenNotAuthorized_thenForbidden() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0075/customers")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andDo(print());

    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/US0075/customers/11111111")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andDo(print());

    mvc.perform(MockMvcRequestBuilders.patch(API_PATH + "/opcos/US0075/customers/11111111")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andDo(print());
  }


  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
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
  @WithMockUser(username = "admin", roles = {"ADMIN"})
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