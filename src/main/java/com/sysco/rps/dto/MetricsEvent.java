package com.sysco.rps.dto;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Model class to store metrics log attributes
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 10. Nov 2020 06:43
 */
public class MetricsEvent {
    private String functionName;
    private String customerId;
    private String requestDate;
    private Integer requestedProductCount;
    private Long queryTime;
    private Long totalTime;
    private Integer successfullyPricedProductCount = 0;
    private Integer defaultPricedProductCount = 0;
    private Integer productCountWithNoPrices = 0;
    private Integer supcsPerQuery;
    private String clientAddress;

    public MetricsEvent(String functionName, CustomerPriceRequest request, @Nullable CustomerPriceResponse response,
                        Long queryTime, Long totalTime, Integer supcsPerQuery, String clientAddress) {
        this.functionName = functionName;
        this.customerId = request.getCustomerAccount();
        this.requestDate = request.getPriceRequestDate();
        this.requestedProductCount = request.getProducts().size();
        this.queryTime = queryTime;
        this.totalTime = totalTime;

        if (response != null) {
            List<Product> products = response.getProducts();

            if (products != null) {
                this.successfullyPricedProductCount = products.size();
                this.defaultPricedProductCount = (int) products.stream().filter(Product::getDefaultPriced).count();
            }

            List<MinorErrorDTO> failedProducts = response.getFailedProducts();
            if (failedProducts != null) {
                this.productCountWithNoPrices = failedProducts.size();
            }
        }

        this.supcsPerQuery = supcsPerQuery;
        this.clientAddress = clientAddress;
    }

    @Override
    public String toString() {
        return String.join("\t",
              functionName,
              customerId,
              requestDate,
              intToString(requestedProductCount),
              clientAddress,
              longToString(queryTime),
              longToString(totalTime),
              intToString(successfullyPricedProductCount),
              intToString(defaultPricedProductCount),
              intToString(productCountWithNoPrices),
              intToString(supcsPerQuery)
        );
    }

    private String intToString(Integer val){
        return val == null ? null : Integer.toString(val);
    }

    private String longToString(Long val) {
        return val == null ? null : Long.toString(val);
    }
}

