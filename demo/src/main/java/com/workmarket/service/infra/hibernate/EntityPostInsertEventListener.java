package com.workmarket.service.infra.hibernate;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import com.workmarket.service.business.event.EntityUpdateEvent;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.infra.security.SecurityContext;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class EntityPostInsertEventListener implements PostInsertEventListener {
	private static final long serialVersionUID = -8986565861504238463L;

	private final static Set<Class> whiteList = Sets.newHashSet();
	static {
		whiteList.add(TravelDistanceRequirement.class);
	}

	@Autowired private JmsService jmsService;
	@Autowired private SecurityContext securityContext;

	@Override
	public void onPostInsert(PostInsertEvent event) {
		boolean sendEvent = whiteList.contains(event.getEntity().getClass());
		if (sendEvent) {
			jmsService.sendEventMessage(
				new EntityUpdateEvent(
					securityContext.getCurrentUser(),
					securityContext.getMasqueradeUser(),
					null,
					(AbstractEntity) event.getEntity(),
					event.getPersister().getPropertyNames(),
					null,
					event.getState()));
		}
	}
}
