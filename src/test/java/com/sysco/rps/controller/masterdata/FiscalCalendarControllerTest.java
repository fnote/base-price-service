package com.sysco.rps.controller.masterdata;

import com.sysco.rps.dto.masterdata.FiscalCalendarDTO;
import com.sysco.rps.entity.masterdata.FiscalCalendar;
import com.sysco.rps.service.masterdata.FiscalCalendarService;
import com.sysco.rps.service.security.IntrospectRestClientService;
import com.sysco.rps.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FiscalCalendarController.class)
@EnableAutoConfiguration
class FiscalCalendarControllerTest  {

  private final String API_PATH = "/ref-price/v1/master-data";
  @MockBean
  IntrospectRestClientService introspectRestClientService;
  @Autowired
  private MockMvc mvc;
  @MockBean
  private FiscalCalendarService fiscalCalendarService;

  @Test
  void whenFindFiscalCalendar_thenFiscalCalendar() throws Exception {
    List<FiscalCalendarDTO> calendarList = new ArrayList<>();
    FiscalCalendar day1 = new FiscalCalendar();
    day1.setCalDate(DateUtil.toDate("12-20-2020", "MM-dd-yyyy"));
    day1.setDow("MON");
    day1.setFwNumber(50);
    day1.setFyNumber(2020);
    day1.setId(1L);
    calendarList.add(new FiscalCalendarDTO(day1));
    FiscalCalendar day2 = new FiscalCalendar();
    day2.setCalDate(DateUtil.toDate("12-21-2020", "MM-dd-yyyy"));
    day2.setDow("TUE");
    day2.setFwNumber(50);
    day2.setFyNumber(2020);
    day2.setId(2L);
    calendarList.add(new FiscalCalendarDTO(day2));
    when(fiscalCalendarService.findAll(1, 2020)).thenReturn(calendarList);

    mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/opcos/1/calendar/2020")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].fyNumber", is(2020)))
        .andExpect(jsonPath("$[0].calDate", is("12-20-2020")))
        .andExpect(jsonPath("$[0].dow", is("MON")))
        .andExpect(jsonPath("$[0].fwNumber", is(50)))
        .andExpect(jsonPath("$[1].calDate", is("12-21-2020")))
        .andExpect(jsonPath("$[1].fyNumber", is(2020)))
        .andExpect(jsonPath("$[1].dow", is("TUE")))
        .andExpect(jsonPath("$[1].fwNumber", is(50)))
        .andDo(print());
  }

}
