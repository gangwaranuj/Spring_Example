package com.workmarket.domains.model.directory;

import com.workmarket.domains.model.AbstractEntity;

public interface EntityPhoneAssociation<T extends AbstractEntity> {
	
	public T getEntity();
	
	public Phone getPhone();	
}