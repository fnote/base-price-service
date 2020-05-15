package com.sysco.payplus.service.masterdata;

import com.sysco.payplus.dto.masterdata.FiscalCalendarDTO;
import com.sysco.payplus.entity.masterdata.FiscalCalendar;
import com.sysco.payplus.service.exception.RecordNotFoundException;

import java.util.Date;
import java.util.List;

public interface FiscalCalendarService {

  FiscalCalendar findByCalDate(Date date) throws RecordNotFoundException;

  List<FiscalCalendarDTO> findAllByYearAndWeek(Integer year, Integer week) throws RecordNotFoundException;

  List<FiscalCalendarDTO> findAll(Integer opCoNumber, Integer year) throws RecordNotFoundException;

}
