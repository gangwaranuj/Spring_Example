package com.workmarket.domains.model.acl;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.LookupEntity;

@Entity(name="aclRoleType")
@Table(name="acl_role_type")
public class AclRoleType extends LookupEntity {

private static final long serialVersionUID = 1L;
		
	public static final String SYSTEM = "system";
	public static final String CUSTOM = "custom";
	public static final String INTERNAL = "internal";
	public static final String NETWORK = "network";

	public AclRoleType(){
		super();
	}
	
	public AclRoleType(String code){
		super(code);
	}
	
}
