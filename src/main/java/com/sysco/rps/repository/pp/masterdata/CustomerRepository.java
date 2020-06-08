package com.sysco.rps.repository.pp.masterdata;

import com.sysco.rps.entity.pp.masterdata.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByOpCoNumberAndCustomerNumberAndIsCurrentTrue(String opCoNumber, String customerNumber);

    Page<Customer> findAllByOpCoNumberAndIsCurrentTrue(String opCoNumber, Pageable pageable);

    boolean existsByOpCoNumberAndCustomerNumberAndIsCurrentTrue(String opCoNumber, String customerNumber);

}
