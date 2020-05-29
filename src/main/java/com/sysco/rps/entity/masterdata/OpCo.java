package com.sysco.rps.entity.masterdata;

import com.sysco.rps.entity.VersionBaseEntity;
import com.sysco.rps.entity.VersionIdentifier;
import com.sysco.rps.entity.masterdata.enums.ActivationStatus;
import com.sysco.rps.entity.masterdata.enums.Market;
import com.sysco.rps.entity.masterdata.enums.Timezone;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/19/20 Time: 12:54 PM
 */
@Entity
@Table(name = "opco",
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"id","version"})
)
@IdClass( VersionIdentifier.class )
public class OpCo extends VersionBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "opco_sequence_generator")
  @SequenceGenerator(name="opco_sequence_generator", sequenceName = "opco_sequence", initialValue = 1, allocationSize = 1)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;
  @Column(name = "workday_name", nullable = false, length = 45)
  private String workdayName;
  @Column(name = "country_code", nullable = false, length = 3)
  private String countryCode;
  @Column(name = "market", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private Market market;
  @Column(name = "sap_entity_id", nullable = false)
  private Integer sapEntityId;
  @Column(name = "sus_entity_id", nullable = false)
  private Integer susEntityId;
  @Column(name = "adp_pay_group", nullable = false, length = 45)
  private String adpPayGroup;
  @Column(name = "adp_location_id", nullable = false)
  private Integer adpLocationId;
  @Column(name = "timezone", nullable = false, length = 15)
  @Enumerated(EnumType.STRING)
  private Timezone timezone;
  @Column(name = "target_pieces_per_trip", nullable = false)
  private Integer targetPiecesPerTrip;
  @Column(name = "active_status", nullable = false, length = 15)
  @Enumerated(EnumType.STRING)
  private ActivationStatus activeStatus;

  public String getWorkdayName() {
    return workdayName;
  }

  public void setWorkdayName(String workdayName) {
    this.workdayName = workdayName;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public Market getMarket() {
    return market;
  }

  public void setMarket(Market market) {
    this.market = market;
  }

  public Integer getSapEntityId() {
    return sapEntityId;
  }

  public void setSapEntityId(Integer sapEntityId) {
    this.sapEntityId = sapEntityId;
  }

  public Integer getSusEntityId() {
    return susEntityId;
  }

  public void setSusEntityId(Integer susEntityId) {
    this.susEntityId = susEntityId;
  }

  public String getAdpPayGroup() {
    return adpPayGroup;
  }

  public void setAdpPayGroup(String adpPayGroup) {
    this.adpPayGroup = adpPayGroup;
  }

  public Integer getAdpLocationId() {
    return adpLocationId;
  }

  public void setAdpLocationId(Integer adpLocationId) {
    this.adpLocationId = adpLocationId;
  }
  public Timezone getTimezone() {
    return timezone;
  }

  public void setTimezone(Timezone timezone) {
    this.timezone = timezone;
  }

  public Integer getTargetPiecesPerTrip() {
    return targetPiecesPerTrip;
  }

  public void setTargetPiecesPerTrip(Integer targetPiecesPerTrip) {
    this.targetPiecesPerTrip = targetPiecesPerTrip;
  }


  public ActivationStatus getActiveStatus() {
    return activeStatus;
  }

  public void setActiveStatus(ActivationStatus activeStatus) {
    this.activeStatus = activeStatus;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  //todo: read from the database
  public Map<String, Boolean> getRulesMap() {
    Map<String, Boolean> map = new HashMap();
    map.put("Stop Pay", true);
    map.put("Road Miles Distance Actual Pay", true);
    map.put("Road Miles Pay Based On Odometer Reading", true);
    map.put("Road Miles Pay Based On Distance Planned", true);
    return map;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpCo opCo = (OpCo) o;
    return opCoNumber.equals(opCo.opCoNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(opCoNumber);
  }
}
