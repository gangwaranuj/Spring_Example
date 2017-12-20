package com.workmarket.domains.model.directory;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.DeletableEntity;

@MappedSuperclass
public abstract class AbstractEntityPhoneAssociation<T extends AbstractEntity> extends DeletableEntity {
	private static final long serialVersionUID = 1L;
	
	private T entity;
	private Phone phone;
		
	public AbstractEntityPhoneAssociation() {}
	public AbstractEntityPhoneAssociation(T entity, Phone phone) {
		this.entity = entity;
		this.phone = phone;
	}
			
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="entity_id")
	public T getEntity() {
		return entity;
	}
	
	public void setEntity(T entity) {
		this.entity = entity;
	}
	
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="phone_id")
	public Phone getPhone() {
		return phone;
	}
	
	public void setPhone(Phone phone) {
		this.phone = phone;
	}
	
}