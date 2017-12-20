package com.workmarket.service.business.event;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;
import com.workmarket.utility.StringUtilities;
import org.springframework.util.Assert;

public class EntityUpdateEvent extends EntityEvent {

	private static final long serialVersionUID = -8865286150496719516L;

	private String[] propertyNames;
	private Object[] oldState;
	private Object[] state;

	public EntityUpdateEvent() {
	}

	public EntityUpdateEvent(User user, User masqueradeUser, User onBehalfUser, AbstractEntity entity, String[] propertyNames, Object[] oldState, Object[] state) {
		super(user, masqueradeUser, onBehalfUser, entity);

		Assert.notNull(propertyNames);
		Assert.noNullElements(propertyNames);
		Assert.notNull(state);
		this.propertyNames = propertyNames;
		this.state = state;
		this.oldState = oldState;
	}

	public Object[] getState() {
		return state;
	}

	public Object[] getOldState() {
		return oldState;
	}

	public String[] getPropertyNames() {
		return propertyNames;
	}

	public boolean hasPropertyValueChanged(String propertyName) {
		Assert.notNull(propertyName);
		int i = StringUtilities.getStringIndex(propertyNames, propertyName);
		return (getState()[i] != null) && (getOldState()[i] != null) && (!getState()[i].equals(getOldState()[i]));
	}

	public int getPropertyIndex(String propertyName) {
		Assert.notNull(propertyName);
		return StringUtilities.getStringIndex(propertyNames, propertyName);
	}
}
