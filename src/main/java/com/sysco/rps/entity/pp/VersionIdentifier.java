package com.sysco.rps.entity.pp;

import java.io.Serializable;
import java.util.Objects;

public class VersionIdentifier implements Serializable {

  private Long id;

  private Long version;

  public VersionIdentifier(Long id, Long version){
    this.id = id;
    this.version = version;
  }
  private VersionIdentifier() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }
    VersionIdentifier versionIdentifier = (VersionIdentifier) o;
    return Objects.equals( id, versionIdentifier.id ) &&
        Objects.equals( version, versionIdentifier.version );
  }

  @Override
  public int hashCode() {
    return Objects.hash( id, version );
  }

}
