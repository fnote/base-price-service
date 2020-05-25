package com.sysco.payplus.service.masterdata;

import com.sysco.payplus.dto.Constant;
import com.sysco.payplus.dto.masterdata.FiscalCalendarDTO;
import com.sysco.payplus.entity.masterdata.FiscalCalendar;
import com.sysco.payplus.repository.masterdata.FiscalCalendarRepository;
import com.sysco.payplus.service.exception.RecordNotFoundException;
import com.sysco.payplus.util.DateUtil;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/3/20 Time: 12:54 PM
 */

@Service
@Transactional
public class FiscalCalendarServiceImpl implements FiscalCalendarService {
  private static final Logger logger = LoggerFactory.getLogger(FiscalCalendarServiceImpl.class);

  @Autowired
  FiscalCalendarRepository fiscalCalendarRepository;

  @PreAuthorize("hasAnyRole( T(com.sysco.payplus.entity.security.Authority).PREP ," +
      " T(com.sysco.payplus.entity.security.Authority).ADMIN )")
  @Override
  public FiscalCalendar findByCalDate(Date date) throws RecordNotFoundException {
    logger.info("retrieving fiscal calendar with date {}", date);
    FiscalCalendar fiscalCalendar = fiscalCalendarRepository.findByCalDate(date).orElse(null);
    if (fiscalCalendar == null) {
      throw new RecordNotFoundException(
          MessageFormat.format("Calendar record for {0} ({1}) was not found",  DateUtil.toDate(date, Constant.DATE_FORMAT), Constant.DATE_FORMAT));
    }
    logger.info("successfully retrieved fiscal calendar with date {}", date);
    return fiscalCalendar;
  }

  @PreAuthorize("hasAnyRole( T(com.sysco.payplus.entity.security.Authority).PREP ," +
      " T(com.sysco.payplus.entity.security.Authority).ADMIN )")
  @Override
  public List<FiscalCalendarDTO> findAll(Integer opCoNumber, Integer fyNumber) throws RecordNotFoundException {
    logger.info("retrieving fiscal calendar records with opco number {} for fiscal year {}", opCoNumber, fyNumber);
    List<FiscalCalendar> fiscalCalendarList = fiscalCalendarRepository.findByFyNumberOrderByCalDateAsc(fyNumber);
    List<FiscalCalendarDTO> fiscalCalendarDTOList = new ArrayList<>();
    for (FiscalCalendar fiscalCalendar : fiscalCalendarList) {
      fiscalCalendarDTOList.add(new FiscalCalendarDTO(fiscalCalendar));
    }
    if (fiscalCalendarDTOList.isEmpty()) {
      throw new RecordNotFoundException(MessageFormat.format("No fiscal calendar records found with opco number {0} for the given fiscal year {1}", opCoNumber, fyNumber));
    }
    logger.info("successfully retrieved fiscal calendar records with opco number {} for fiscal year {}", opCoNumber, fyNumber);
    return fiscalCalendarDTOList;
  }

  @PreAuthorize("hasAnyRole( "
      + " T(com.sysco.payplus.entity.security.Authority).PREP ,"
      + " T(com.sysco.payplus.entity.security.Authority).ADMIN  ,"
      + " T(com.sysco.payplus.entity.security.Authority).APPROVER ,"
      + " T(com.sysco.payplus.entity.security.Authority).REPORT_ACCESS )")
  @Override
  public List<FiscalCalendarDTO> findAllByYearAndWeek(Integer fyNumber, Integer fwNumber) throws RecordNotFoundException {
    logger.info("retrieving fiscal calendar records for fiscal year {} and week {}", fyNumber, fwNumber);
    List<FiscalCalendar> fiscalCalendarList = fiscalCalendarRepository.findByFyNumberAndFwNumberOrderByCalDateAsc(fyNumber, fwNumber);
    List<FiscalCalendarDTO> fiscalCalendarDTOList = new ArrayList<>();
    fiscalCalendarList.forEach(fiscalCalendar -> fiscalCalendarDTOList.add(new FiscalCalendarDTO(fiscalCalendar)));

    if (fiscalCalendarDTOList.isEmpty()) {
      throw new RecordNotFoundException(MessageFormat.format("No fiscal calendar records found for the given year {0} and week {1}", fyNumber, fwNumber));
    }
    logger.info("successfully retrieved fiscal calendar records for fiscal year {} and week {}", fyNumber, fwNumber);
    return fiscalCalendarDTOList;
  }
}

