package com.sysco.payplus.service.masterdata;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sysco.payplus.dto.masterdata.FiscalCalendarDTO;
import com.sysco.payplus.service.exception.RecordNotFoundException;
import com.sysco.payplus.util.DateUtil;
import java.text.ParseException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;


@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
class FiscalCalendarServiceTest {

  @Autowired
  FiscalCalendarService fiscalCalendarService;

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void findByCalDate() throws RecordNotFoundException, ParseException {
    assertNotNull(fiscalCalendarService.findByCalDate(DateUtil.toDate("07-03-2018", "MM-dd-yyyy")));
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenFindByCalDateWithUnAvailableDate_thenThrowsException() {
    //2018-07-03
    assertThrows(RecordNotFoundException.class, () -> {
      fiscalCalendarService.findByCalDate(DateUtil.toDate("07-03-1919", "MM-dd-yyyy"));
    });
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenFindAll_thenReturnsAllDates() throws RecordNotFoundException {
    List<FiscalCalendarDTO> fiscalCalendarDTOList = fiscalCalendarService.findAll(1, 2019);
    assertTrue(fiscalCalendarDTOList.size() > 0);
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN", "PREP", "APPROVER", "REPORT_ACCESS"})
  void whenFindAllByYearAndWeek_thenReturnsAllDates() throws RecordNotFoundException {
    List<FiscalCalendarDTO> fiscalCalendarDTOList = fiscalCalendarService.findAllByYearAndWeek(2019, 2);
    assertTrue(!fiscalCalendarDTOList.isEmpty());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN", "PREP", "APPROVER", "REPORT_ACCESS"})
  void whenFindAllByYearAndWeekWithUnavailableDate_thenReturnsAllDates() throws RecordNotFoundException {
    assertThrows(RecordNotFoundException.class, () -> fiscalCalendarService.findAllByYearAndWeek(2050, 2));
  }
}
