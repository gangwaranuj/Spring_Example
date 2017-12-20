package com.workmarket.service.business.event;

import org.springframework.util.Assert;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;

public class EntityEvent extends Event {

	private static final long serialVersionUID = -235530749797603126L;

	@Deprecated
	private AbstractEntity entity;

	public EntityEvent() {
		super();
	}

	public EntityEvent(User user, User masqueradeUser, User onBehalfUser, AbstractEntity entity) {
		super(user, masqueradeUser, onBehalfUser);
		Assert.notNull(entity);
		this.entity = entity;
	}

	public EntityEvent(AbstractEntity entity) {
		this.entity = entity;
	}

	public AbstractEntity getEntity() {
		return entity;
	}
}
