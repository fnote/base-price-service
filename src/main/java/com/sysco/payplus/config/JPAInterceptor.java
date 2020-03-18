package com.sysco.payplus.config;

import com.sysco.payplus.entity.AuditableEntity;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/17/20
 * Time: 12:54 PM
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
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //Setting object properties does not seem to work,. So we need to change the status for each property name
            for (int i = 0; i < propertyNames.length; i++) {
                //todo:can we do savedAt/savedBy
                if ("updatedAt".equals(propertyNames[i])) state[i] = new Date().getTime();
                if ("updatedBy".equals(propertyNames[i])) state[i] = user.getUsername();
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
