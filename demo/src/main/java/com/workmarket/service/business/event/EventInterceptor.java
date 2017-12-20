package com.workmarket.service.business.event;

import com.workmarket.service.infra.security.SecurityContext;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * This listener is called every time an entity is updated or inserted. It will update
 * <p/>
 * created_on
 * modified_on
 * created_by
 * modified_by
 * <p/>
 * filled based on current user and current time.
 */
@Component
public class EventInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;
	@Autowired SecurityContext securityContext;

	public boolean onFlushDirty(
			Object entity,
			Serializable id,
			Object[] currentState,
			Object[] previousState,
			String[] propertyNames,
			Type[] types) {

		return false;
	}

	public boolean onSave(
			Object entity,
			Serializable id,
			Object[] state,
			String[] propertyNames,
			Type[] types) {

		return false;
	}
}
