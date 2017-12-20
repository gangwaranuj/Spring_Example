package com.workmarket.domains.model.directory;

import com.workmarket.domains.model.AbstractEntity;

public interface EntityEmailAssociation<T extends AbstractEntity> {
	
	public T getEntity();
	
	public Email getEmail();	
}