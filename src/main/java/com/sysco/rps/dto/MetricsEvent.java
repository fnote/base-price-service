package com.sysco.rps.dto;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 10. Nov 2020 06:43
 */
public class MetricsEvent {
    private String functionName;
    private String customerId;
    private String requestDate;
    private long requestedProductCount;
    private long queryTime;
    private long totalTime;
    private Integer successfullyPricedProductCount = 0;
    private Integer defaultPricedProductCount = 0;
    private Integer productCountWithNoPrices = 0;
    private Integer supcsPerQuery;
    private String clientAddress;

    public MetricsEvent(String functionName, CustomerPriceRequest request, @Nullable CustomerPriceResponse response,
                        long queryTime, long totalTime, Integer supcsPerQuery, String clientAddress) {
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
                this.defaultPricedProductCount = Long.valueOf(products.stream().filter(Product::getDefaultPriced).count()).intValue();
            }

            List<MinorErrorDTO> failedProducts = response.getFailedProducts();
            if (failedProducts != null) {
                this.productCountWithNoPrices = failedProducts.size();
            }
        }

        this.supcsPerQuery = supcsPerQuery;
        this.clientAddress = clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public void setSuccessfullyPricedProductCount(Integer successfullyPricedProductCount) {
        this.successfullyPricedProductCount = successfullyPricedProductCount;
    }

    public void setDefaultPricedProductCount(Integer defaultPricedProductCount) {
        this.defaultPricedProductCount = defaultPricedProductCount;
    }

    public void setProductCountWithNoPrices(Integer productCountWithNoPrices) {
        this.productCountWithNoPrices = productCountWithNoPrices;
    }

    public void setSupcsPerQuery(Integer supcsPerQuery) {
        this.supcsPerQuery = supcsPerQuery;
    }

    @Override
    public String toString() {
        Object[] objs = new Object[]{
              functionName,
              customerId,
              requestDate,
              requestedProductCount,
              clientAddress,
              queryTime,
              totalTime,
              successfullyPricedProductCount,
              defaultPricedProductCount,
              productCountWithNoPrices,
              supcsPerQuery
        };

        return StringUtils.join(objs, "/t");
    }
}

