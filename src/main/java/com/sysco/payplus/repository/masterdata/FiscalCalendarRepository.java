package com.sysco.payplus.repository.masterdata;

import com.sysco.payplus.entity.masterdata.FiscalCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FiscalCalendarRepository extends JpaRepository<FiscalCalendar, Long> {

    Optional<FiscalCalendar> findByCalDate(Date calDate);

    List<FiscalCalendar> findByFyNumberOrderByCalDateAsc(Integer fyNumber);

    List<FiscalCalendar> findByFyNumberAndFwNumberOrderByCalDateAsc(Integer fyNumber, Integer fwNumber);
}
