package com.workmarket.domains.model.directory;

import com.workmarket.domains.model.AbstractEntity;

public interface EntityWebsiteAssociation<T extends AbstractEntity> {
	
	public T getEntity();
	
	public Website getWebsite();	
}