package com.sysco.rps.dto.masterdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.sysco.rps.entity.masterdata.FiscalCalendar;
import com.sysco.rps.util.DateUtil;
import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Date;

import static com.sysco.rps.dto.Constant.DATE_FORMAT;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/25/20 Time: 12:54 PM
 */

public class BaseGlobalDTO {
  private static ModelMapper modelMapper = new ModelMapper();

  static {
    modelMapper.getConfiguration()
        .setMatchingStrategy(MatchingStrategies.STRICT);

    //Fiscal calendar is converted to a readable date format MM-dd-yyyy
    modelMapper.addConverter(context -> {
      if (context.getSource() == null) {
        return null;
      }
      try {
        return DateUtil.toDate(context.getSource().getCalDate(), DATE_FORMAT);
      } catch (Exception e) {
        return null;
      }
    }, FiscalCalendar.class, String.class);

    //A date object is converted to a readable date format MM-dd-yyyy
    modelMapper.addConverter(context -> {
      if (context.getSource() == null) {
        return null;
      }
      try {
        return DateUtil.toDate(context.getSource(), DATE_FORMAT);
      } catch (Exception e) {
        return null;
      }
    }, Date.class, String.class);

    //A string object is converted to a date MM-dd-yyyy
    modelMapper.addConverter(context -> {
      if (context.getSource() == null) {
        return null;
      }
      try {
        return DateUtil.toDate(context.getSource(), DATE_FORMAT);
      } catch (Exception e) {
        return null;
      }
    }, String.class, Date.class);


    //A JsonNode object is converted to a readable text format
    modelMapper.addConverter(context -> {
      if (context.getSource() == null) {
        return null;
      }
      return context.getSource().toString();
    }, JsonNode.class, String.class);

    modelMapper.addConverter(context -> {
      if (context.getSource() == null) {
        return null;
      }
      return JacksonUtil.toJsonNode(context.getSource());
    }, String.class, JsonNode.class);


  }

  protected Boolean locked = false;

  protected static ModelMapper getModelMapper() {
    return modelMapper;
  }

  public Boolean getLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
  }
}
