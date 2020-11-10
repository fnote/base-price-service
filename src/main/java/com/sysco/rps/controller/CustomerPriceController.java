package com.sysco.rps.controller;

import com.sysco.rps.dto.CustomerPriceRequest;
import com.sysco.rps.dto.CustomerPriceResponse;
import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.dto.MetricsEvent;
import com.sysco.rps.service.CustomerPriceService;
import com.sysco.rps.util.MetricsLoggerUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controller class that defines the Customer Price endpoints
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 03. Jul 2020 10:02
 */
@RestController
@RequestMapping("/ref-price/")
public class CustomerPriceController extends AbstractController {

    @Autowired
    private CustomerPriceService customerPriceService;

    @PostMapping("/customer-prices")
    @ApiOperation(
          value = "Gets the reference pricing for the customer item",
          notes = "Effective reference prices will be identified considering the priceRequestDate provided in the req body.",
          response = CustomerPriceResponse.class)
    @ApiResponses(value = {
          @ApiResponse(code = org.apache.http.HttpStatus.SC_OK, message = "Data fetch successful"),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_BAD_REQUEST, response = ErrorDTO.class,
                message = "<table>" +
                      "  <tr> <th>code</th> <th>message</th> </tr>" +
                      "  <tr> <td>102040</td> <td>OpCo ID is either null or empty</td> </tr>" +
                      "  <tr> <td>102030</td> <td>Products not found in the request</td> </tr>" +
                      "  <tr> <td>102050</td> <td>Customer ID is either null or empty</td> </tr>" +
                      "  <tr> <td>102060</td> <td>Price request date is either null, empty or invalid</td> </tr>" +
                      "</table>"),
          @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND,
                message = "<table>" +
                      "  <tr> <th>code</th> <th>message</th> <th>error data</th> </tr>" +
                      "  <tr> <td>102010</td> <td>OpCo Invalid</td> <td>Couldn't find a matching DB for the requested OpCo</td> </tr>" +
                      "</table>"),
    })
    public @ResponseBody
    Mono<CustomerPriceResponse> getCustomerPrices(@RequestBody CustomerPriceRequest request, @RequestParam(required = false) Integer supcsPerQuery) {
        return customerPriceService.pricesByOpCo(request, supcsPerQuery)
              .doOnError(e -> {
                  MetricsEvent metricsEvent = new MetricsEvent("customer-prices", request, null, 0L, 0L, supcsPerQuery, null);
                  MetricsLoggerUtils.logError(metricsEvent);
              });
    }
}
