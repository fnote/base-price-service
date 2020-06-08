package com.sysco.rps.repository.refpricing;

import com.sysco.rps.dto.refpricing.CustomerPrice;
import com.sysco.rps.dto.refpricing.CustomerPriceReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author
 * @copyright (C) 2020,
 * @doc
 * @end Created : 07. Jun 2020 06:59
 */
@Component
public class CustomerPriceRepository {

    @Autowired
    EntityManagerFactory entityManagerFactory;


    public List<CustomerPrice> getCustomerPrice(CustomerPriceReqDTO customerPriceReqDTO) {

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        String strQuery = "SELECT p.SUPC, p.PRICE_ZONE, e.CUSTOMER_ID, p.PRICE, p.EFFECTIVE_DATE " +
              "FROM PA p " +
              "INNER JOIN EATS_001 e ON p.PRICE_ZONE = e.PRICE_ZONE and p.SUPC = e.SUPC " +
              "WHERE e.CUSTOMER_ID=\"" + customerPriceReqDTO.getCustomerId() +
              "\" and e.SUPC in " +
              "(" + convertList(customerPriceReqDTO.getSupcs()) + ") " +
              "ORDER BY p.EFFECTIVE_DATE DESC";

        Query query = entityManager.createNativeQuery(strQuery);


        List<Object[]> resultList = query.getResultList();
        Map<String, CustomerPrice> supcCustomerPriceMap = new HashMap<>();

        for (Object[] result : resultList) {
            String supc = (String) result[0];

            if(!supcCustomerPriceMap.containsKey(supc)) {
                Double price = (Double) result[3];

                Timestamp timestamp = (Timestamp) result[4];
                Date date = new Date(timestamp.getTime());

                CustomerPrice customerPrice = new CustomerPrice(supc, (String) result[1], (String) result[2], price, date);
                supcCustomerPriceMap.put(supc, customerPrice);
            }

        }

        return new ArrayList<>(supcCustomerPriceMap.values());
    }

    private String convertList(List<String> stringList) {
        return stringList.stream().map(str -> "\"" + str + "\"").collect(Collectors.joining(","));
    }

}
