package com.sysco.payplus.entity.masterdata;

import com.sysco.payplus.entity.RawBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/21/20 Time: 12:54 PM
 */
@Entity
@Table(name = "fiscal_calendar",
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"id"})
)
public class FiscalCalendar extends RawBaseEntity {

  @Column(name = "cal_date", nullable = false)
  private Date calDate;

  @Column(name = "dow", nullable = false, length = 45)
  private String dow;

  @Column(name = "fw_number", nullable = false)
  private int fwNumber;

  @Column(name = "fy_number", nullable = false)
  private int fyNumber;

  public Date getCalDate() {
    return calDate;
  }

  public void setCalDate(Date calDate) {
    this.calDate = calDate;
  }

  public String getDow() {
    return dow;
  }

  public void setDow(String dow) {
    this.dow = dow;
  }

  public int getFwNumber() {
    return fwNumber;
  }

  public void setFwNumber(int fwNumber) {
    this.fwNumber = fwNumber;
  }

  public int getFyNumber() {
    return fyNumber;
  }

  public void setFyNumber(int fyNumber) {
    this.fyNumber = fyNumber;
  }
}
