package com.workmarket.domains.model.directory;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.DeletableEntity;

@MappedSuperclass
public abstract class AbstractEntityWebsiteAssociation<T extends AbstractEntity> extends DeletableEntity {
	private static final long serialVersionUID = 1L;
	
	private T entity;
	private Website website;
		
	public AbstractEntityWebsiteAssociation() {}
	public AbstractEntityWebsiteAssociation(T entity, Website website) {
		this.entity = entity;
		this.website = website;
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
	@JoinColumn(name="website_id")
	public Website getWebsite() {
		return website;
	}
	
	public void setWebsite(Website website) {
		this.website = website;
	}
	
}