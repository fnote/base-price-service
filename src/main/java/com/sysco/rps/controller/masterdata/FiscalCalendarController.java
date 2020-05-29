package com.sysco.rps.controller.masterdata;

import com.sysco.rps.dto.masterdata.FiscalCalendarDTO;
import com.sysco.rps.service.masterdata.FiscalCalendarService;
import com.sysco.rps.service.exception.RecordNotFoundException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/19/20 Time: 12:54 PM
 */

@Controller
@RequestMapping("/ref-price/v1/master-data")
public class FiscalCalendarController {

    Logger logger = LoggerFactory.getLogger(FiscalCalendarController.class);

    @Autowired
    private FiscalCalendarService fiscalCalendarService;

    @GetMapping("/opcos/{opCoNumber}/calendar/{year}")
    @ApiOperation(value = "Returns the Sysco fiscal calendar", notes = "Returns the Sysco fiscal calendar", response = FiscalCalendarDTO.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Calendar found"),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_UNAUTHORIZED, message = "Not authorized."),
            @ApiResponse(code = org.apache.http.HttpStatus.SC_FORBIDDEN, message = "Not authorized.")
    })
    public @ResponseBody
    ResponseEntity<List<FiscalCalendarDTO>> findCalendar(
            @ApiParam(value = "OpCo number", required = true) @PathVariable Integer opCoNumber,
            @ApiParam(value = "Fiscal year", required = true) @PathVariable Integer year) throws RecordNotFoundException {
        List<FiscalCalendarDTO> fiscalCalendarDTOList = fiscalCalendarService.findAll(opCoNumber, year);
        return ResponseEntity.status(HttpStatus.OK)
                .body(fiscalCalendarDTOList);
    }


}

