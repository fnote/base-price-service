package com.sysco.rps.repository.masterdata;

import com.sysco.rps.entity.VersionIdentifier;
import com.sysco.rps.entity.masterdata.OpCo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/19/20 Time: 12:54 PM
 */

public interface OpCoRepository extends JpaRepository<OpCo, VersionIdentifier> {

    Page<OpCo> findAllByCountryCodeAndIsCurrentTrue(String countryCode, Pageable pageable);

    Optional<OpCo> findByOpCoNumberAndIsCurrentTrue(String opCoNumber);

    boolean existsByOpCoNumberAndIsCurrentTrue(String opCoNumber);

    boolean existsByWorkdayNameAndIsCurrentTrue(String workdayName);

    boolean existsBySapEntityIdAndIsCurrentTrue(Integer sapEntityId);

    boolean existsBySusEntityIdAndIsCurrentTrue(Integer susEntityId);

    boolean existsByAdpPayGroupAndIsCurrentTrue(String adpPayGroup);

    boolean existsByAdpLocationIdAndIsCurrentTrue(Integer adpLocationId);
}
