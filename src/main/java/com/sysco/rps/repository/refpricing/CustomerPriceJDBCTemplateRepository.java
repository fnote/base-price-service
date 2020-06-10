package com.sysco.rps.repository.refpricing;

import com.sysco.rps.dto.refpricing.CustomerPrice;
import com.sysco.rps.dto.refpricing.CustomerPriceReqDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 08. Jun 2020 14:36
 */
@Repository
public class CustomerPriceJDBCTemplateRepository extends NamedParameterJdbcDaoSupport implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPriceJDBCTemplateRepository.class);

    @Autowired()
    @Qualifier("dataSource")
    public DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }


    // Using query with no duplicate filter
    public List<CustomerPrice> getCustomerPrice(CustomerPriceReqDTO customerPriceReqDTO) {

        String strQuery = "SELECT p.SUPC, p.PRICE_ZONE, e.CUSTOMER_ID, p.PRICE, p.EFFECTIVE_DATE " +
              "FROM PA_HIS p " +
              "INNER JOIN EATS_001 e ON p.PRICE_ZONE = e.PRICE_ZONE and p.SUPC = e.SUPC " +
              "WHERE e.CUSTOMER_ID=:customerId and e.SUPC in " +
              "(:supcs) " +
              "ORDER BY p.EFFECTIVE_DATE DESC";

        SqlParameterSource namedParameters = new MapSqlParameterSource("customerId", customerPriceReqDTO.getCustomerId())
              .addValue("supcs", customerPriceReqDTO.getSupcs());

        LOGGER.debug("SQL statement:[{}]", NamedParameterUtils.substituteNamedParameters(strQuery, namedParameters));

        Map<String, CustomerPrice> supcCustomerPriceMap = new HashMap<>();

        getNamedParameterJdbcTemplate().query(strQuery, namedParameters, (resultSet, rowNum) -> {
            String supc = resultSet.getString("SUPC");

            if (!supcCustomerPriceMap.containsKey(supc)) {
                String priceZone = resultSet.getString("PRICE_ZONE");
                String customerId = resultSet.getString("CUSTOMER_ID");
                Double price = resultSet.getDouble("PRICE");
                Date date = resultSet.getDate("EFFECTIVE_DATE");
                CustomerPrice customerPrice = new CustomerPrice(supc, priceZone, customerId, price, date);
                supcCustomerPriceMap.put(supc, customerPrice);
                return customerPrice;
            }

            return null;
        });

        return new ArrayList<>(supcCustomerPriceMap.values());
    }

    // Using query with duplicate filter
    public List<CustomerPrice> getCustomerPrice2(CustomerPriceReqDTO customerPriceReqDTO) {

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
                    "ON c.max_eff_date = p.EFFECTIVE_DATE AND c.SUPC = p.SUPC AND c.PRICE_ZONE = p.PRICE_ZONE " +
                    "ORDER BY c.CUSTOMER_ID, SUPC";

        String maxEffectiveDate = StringUtils.isEmpty(customerPriceReqDTO.getEffectiveDate()) ? getCurrentDate() :
              customerPriceReqDTO.getEffectiveDate();

        SqlParameterSource namedParameters = new MapSqlParameterSource()
              .addValue("customerId", customerPriceReqDTO.getCustomerId())
              .addValue("supcs", customerPriceReqDTO.getSupcs())
              .addValue("maxEffectiveDate", maxEffectiveDate);

        LOGGER.debug("SQL statement:[{}]", NamedParameterUtils.substituteNamedParameters(query, namedParameters));

        return getNamedParameterJdbcTemplate().query(query, namedParameters, (resultSet, rowNum) -> {
            String supc = resultSet.getString("SUPC");

            String priceZone = resultSet.getString("PRICE_ZONE");
            String customerId = resultSet.getString("CUSTOMER_ID");
            Double price = resultSet.getDouble("PRICE");
            Date date = resultSet.getDate("EFFECTIVE_DATE");
            return new CustomerPrice(supc, priceZone, customerId, price, date);
        });

    }

    private String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

}
