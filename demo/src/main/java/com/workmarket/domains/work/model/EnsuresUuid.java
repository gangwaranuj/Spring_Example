package com.workmarket.domains.work.model;

import com.workmarket.id.IdGenerator;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.event.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Ensures that the item has a UUID.
 */
public class EnsuresUuid implements PreUpdateEventListener, PreInsertEventListener {
	@Autowired
	private IdGenerator idGenerator;

	@Override
	public boolean onPreInsert(final PreInsertEvent event) {
		if (event.getEntity() instanceof HasUuid) {
			ensureUuid(event, event.getState());
		}
		return false; // false means "continue on", true would mean "stop the show!"
	}

	@Override
	public boolean onPreUpdate(final PreUpdateEvent event) {
		if (event.getEntity() instanceof HasUuid) {
			ensureUuid(event, event.getState());
		}
		return false; // false means "continue on", true would mean "stop the show!"
	}

	private void ensureUuid(final AbstractPreDatabaseOperationEvent event, final Object[] state) {
		final HasUuid hu = (HasUuid) event.getEntity();
		if (isBlank(hu.getUuid())) {
			final String uuid = idGenerator.next().toBlocking().single();
			final String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
			final int index = ArrayUtils.indexOf(propertyNames, "uuid");
			if (index >= 0) {
				state[index] = uuid;
			}
			hu.setUuid(uuid);
		}
	}
}
