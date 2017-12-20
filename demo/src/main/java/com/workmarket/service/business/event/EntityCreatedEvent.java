package com.workmarket.service.business.event;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;

public class EntityCreatedEvent extends EntityEvent {

	private static final long serialVersionUID = 6006748223359057842L;

	public EntityCreatedEvent() {
		super();
	}

	public EntityCreatedEvent(User user, User masqueradeUser, User onBehalfUser, AbstractEntity entity) {
		super(user, masqueradeUser, onBehalfUser, entity);
	}

}
