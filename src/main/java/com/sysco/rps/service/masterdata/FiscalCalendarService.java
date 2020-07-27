package com.sysco.rps.service.masterdata;

import com.sysco.rps.dto.pp.masterdata.FiscalCalendarDTO;
import com.sysco.rps.entity.pp.masterdata.FiscalCalendar;
import com.sysco.rps.service.exception.RecordNotFoundException;

import java.util.Date;
import java.util.List;

public interface FiscalCalendarService {

  FiscalCalendar findByCalDate(Date date) throws RecordNotFoundException;

  List<FiscalCalendarDTO> findAllByYearAndWeek(Integer year, Integer week) throws RecordNotFoundException;

  List<FiscalCalendarDTO> findAll(Integer opCoNumber, Integer year) throws RecordNotFoundException;

}
