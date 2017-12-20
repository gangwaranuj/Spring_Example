package com.workmarket.domains.model.directory;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.DeletableEntity;

@MappedSuperclass
public abstract class AbstractEntityEmailAssociation<T extends AbstractEntity> extends DeletableEntity {
	private static final long serialVersionUID = 1L;
	
	private T entity;
	private Email email;
		
	public AbstractEntityEmailAssociation() {}
	public AbstractEntityEmailAssociation(T entity, Email email) {
		this.entity = entity;
		this.email = email;
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
	@JoinColumn(name="email_id")
	public Email getEmail() {
		return email;
	}
	
	public void setEmail(Email email) {
		this.email = email;
	}
	
}