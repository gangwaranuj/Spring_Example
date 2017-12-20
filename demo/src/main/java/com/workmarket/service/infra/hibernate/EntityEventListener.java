package com.workmarket.service.infra.hibernate;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.business.event.EntityUpdateEvent;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class EntityEventListener implements PostUpdateEventListener {

	private static final long serialVersionUID = -8986565861504238462L;

	@SuppressWarnings("rawtypes")
	private final static Set<Class> whiteList = Sets.newHashSet();

	static {
		whiteList.add(User.class);
		whiteList.add(Profile.class);
		whiteList.add(AbstractWork.class);
		whiteList.add(Work.class);
		whiteList.add(Project.class);
		whiteList.add(LaneAssociation.class);
		whiteList.add(BackgroundCheck.class);
		whiteList.add(DrugTest.class);
		whiteList.add(Company.class);
		whiteList.add(TravelDistanceRequirement.class);
	}

	@Autowired private JmsService jmsService;
	@Autowired private SecurityContext securityContext;

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		boolean sendEvent = false;
		if (whiteList.contains(event.getEntity().getClass())) {
			if (event.getEntity() instanceof Work) {
				if (HibernateUtilities.hasRelevantPropertyChange(event.getEntity(), event.getPersister().getPropertyNames(), event.getOldState(), event.getState())) {
					Work work = (Work) event.getEntity();
					sendEvent = work.isSent();
				}
			} else if (event.getEntity() instanceof Company) {
				if (HibernateUtilities.hasRelevantPropertyChange(event.getEntity(), event.getPersister().getPropertyNames(), event.getOldState(), event.getState())) {
					sendEvent = true;
				}
			} else {
				sendEvent = event.getOldState() != null;
			}
		}

		if (sendEvent) {
			sendEvent(event);
		}
	}

	private void sendEvent(PostUpdateEvent event) {
		jmsService.sendEventMessage(
			new EntityUpdateEvent(
				securityContext.getCurrentUser(),
				securityContext.getMasqueradeUser(),
				null,
				(AbstractEntity) event.getEntity(),
				event.getPersister().getPropertyNames(),
				event.getOldState(),
				event.getState()));
	}
}
