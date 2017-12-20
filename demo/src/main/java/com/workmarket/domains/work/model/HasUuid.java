package com.workmarket.domains.work.model;

/**
 * For things that have UUIDs on them and want to use the EnsuresUuid EntityListener.
 */
public interface HasUuid {
	String getUuid();

	void setUuid(final String uuid);
}
