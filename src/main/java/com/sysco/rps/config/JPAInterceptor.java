package com.sysco.rps.config;

import com.sysco.rps.entity.pp.AuditableEntity;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/17/20 Time: 12:54 PM
 */

public class JPAInterceptor extends EmptyInterceptor {

  @Override
  public boolean onSave(
      Object entity,
      Serializable id,
      Object[] state,
      String[] propertyNames,
      Type[] types) {
    if (entity instanceof AuditableEntity) {
      // TODO: Remove if saved_by is not required
      String user = "SYSTEM";
      //Setting object properties does not seem to work,. So we need to change the status for each property name
      for (int i = 0; i < propertyNames.length; i++) {
        if ("savedTime".equals(propertyNames[i])) {
          state[i] = new Date().getTime();
        }
        if ("savedBy".equals(propertyNames[i])) {
          state[i] = user;
        }
      }
      return true;
    }
    return super.onSave(entity,
        id,
        state,
        propertyNames,
        types);
  }
}
