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
 * @author Sanjaya Amarasinghe
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

        String query = "select ll.PRICE, ll.SUPC, ll.EXPORTED_DATE, ll.EFFECTIVE_DATE, ll.PRICE_ZONE from  " +
              "PA ll  " +
              "inner join  " +
              "( " +
              " select max(pr.EXPORTED_DATE) max_expo, pr.SUPC, pr.PRICE_ZONE, pr.EFFECTIVE_DATE from PA pr " +
              " inner join  " +
              " ( " +
              " select max(pp.EFFECTIVE_DATE) max_eff, pp.SUPC, pp.PRICE_ZONE from PA pp  " +
              "  inner join  " +
              "  (select ea.SUPC, ea.CUSTOMER_ID, ea.PRICE_ZONE, ea.EFFECTIVE_DATE from EATS ea  " +
              "   inner join  " +
              "    ( " +
              "     select MAX(e.EFFECTIVE_DATE) eat_max_eff , e.SUPC, e.CUSTOMER_ID  " +
              "     from EATS e " +
              "     where e.CUSTOMER_ID =:customerId and SUPC in ( :supcs ) and EFFECTIVE_DATE <= :maxEffectiveDate group by e.SUPC " +
              "    ) aa " +
              "   " +
              "    where ea.SUPC in (aa.SUPC) and ea.CUSTOMER_ID = aa.CUSTOMER_ID and ea.EFFECTIVE_DATE=aa.eat_max_eff " +
              "  ) bb  " +
              " on bb.PRICE_ZONE = pp.PRICE_ZONE and pp.SUPC in (bb.SUPC) and pp.EFFECTIVE_DATE <= :maxEffectiveDate group by pp.SUPC " +
              " ) cc " +
              "  " +
              " where pr.EFFECTIVE_DATE = cc.max_eff and pr.SUPC =cc.SUPC and pr.PRICE_ZONE = cc.PRICE_ZONE group by pr.SUPC " +
              ") ee " +
              " " +
              "where ll.EFFECTIVE_DATE = ee.EFFECTIVE_DATE and ll.SUPC in (ee.SUPC) and ll.PRICE_ZONE = ee.PRICE_ZONE and ll.EXPORTED_DATE = ee.max_expo";

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
            int priceZone = resultSet.getInt("PRICE_ZONE");
            Double price = resultSet.getDouble("PRICE");
            Date effectiveDate = resultSet.getDate("EFFECTIVE_DATE");
            Long exportedDate = resultSet.getLong("EXPORTED_DATE");

            return new Product(supc, priceZone, price, getDate(effectiveDate), exportedDate);
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
