package com.sysco.rps.repository.refpricing;

import com.sysco.rps.dto.refpricing.CustomerPrice;
import com.sysco.rps.dto.refpricing.CustomerPriceRequest;
import com.sysco.rps.dto.refpricing.Product;
import com.sysco.rps.repository.common.DataSourceProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 07. Jun 2020 06:59
 */
@Repository
public class CustomerPriceRepository extends NamedParameterJdbcDaoSupport implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPriceRepository.class);

    @Autowired
    CustomerPriceRepository() {
        super();
        setDataSource(DataSourceProvider.getActiveDataSource());
    }

    public CustomerPrice getCustomerPrice(CustomerPriceRequest customerPriceReq) {

        String query =
              "SELECT " +
                    "c.CUSTOMER_ID, p.SUPC, p.PRICE_ZONE, p.PRICE, p.EFFECTIVE_DATE " +
                    "FROM " +
                    "PA_HIS p INNER JOIN" +
                    "(SELECT MAX(p.EFFECTIVE_DATE) max_eff_date, p.SUPC, p.PRICE_ZONE, b.CUSTOMER_ID " +
                    "FROM " +
                    "(SELECT e.SUPC, e.PRICE_ZONE, e.CUSTOMER_ID from EATS_001 e " +
                    "where e.CUSTOMER_ID=:customerId and SUPC in (:supcs) ) b " +
                    "INNER JOIN " +
                    "PA_HIS p ON b.SUPC = p.SUPC AND b.PRICE_ZONE = p.PRICE_ZONE " +
                    "WHERE p.EFFECTIVE_DATE <= :maxEffectiveDate " +
                    "GROUP BY p.SUPC)  c " +
                    "ON c.max_eff_date = p.EFFECTIVE_DATE AND c.SUPC = p.SUPC AND c.PRICE_ZONE = p.PRICE_ZONE ";

        String maxEffectiveDate = StringUtils.isEmpty(customerPriceReq.getPriceRequestDate()) ? getCurrentDate() :
              customerPriceReq.getPriceRequestDate();

        SqlParameterSource namedParameters = new MapSqlParameterSource()
              .addValue("customerId", customerPriceReq.getCustomerAccount())
              .addValue("supcs", customerPriceReq.getProducts())
              .addValue("maxEffectiveDate", maxEffectiveDate);

//        LOGGER.debug("SQL statement:[{}]", NamedParameterUtils.substituteNamedParameters(query, namedParameters));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Product> products = getNamedParameterJdbcTemplate().query(query, namedParameters, (resultSet, rowNum) -> {

            if (stopWatch.isRunning()) {
                stopWatch.stop();
                LOGGER.info("QUERY-LATENCY : [{}]", stopWatch.getLastTaskTimeMillis());
            }

            String supc = resultSet.getString("SUPC");
            String priceZone = resultSet.getString("PRICE_ZONE");
            Double price = resultSet.getDouble("PRICE");
            Date effectiveDate = resultSet.getDate("EFFECTIVE_DATE");

            return new Product(supc, priceZone, price, getDate(effectiveDate));
        });


        return new CustomerPrice(customerPriceReq.getBusinessUnitNumber(), customerPriceReq.getCustomerAccount(),
              customerPriceReq.getPriceRequestDate(), products);
    }

    private String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private String getDate(Date date) {
        if (date == null) {
            return "";
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

}
