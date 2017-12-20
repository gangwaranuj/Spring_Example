package com.workmarket.domains.model.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.AbstractEntity;

@Entity(name = "permissionGroup")
@Table(name = "permission_group")
public class PermissionGroup extends AbstractEntity{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;

	public static final Long FUNDS_AND_PAYMENTS_GROUP = 1L;
	public static final Long MANAGE_FUNDS_GROUP = 8L;
	
	@Column(name = "name", nullable = false, length=45)	
	public String getName(){
		return name;
	}
	
	@Column(name = "description", length=100)	
	public String getDescription() {
		return description;
	}
	
	public void setName(String name){
		this.name = name;		
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
