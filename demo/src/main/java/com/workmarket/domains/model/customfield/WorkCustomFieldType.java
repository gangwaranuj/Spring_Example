package com.workmarket.domains.model.customfield;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.LookupEntity;

@Entity(name="work_custom_field_type")
@Table(name="work_custom_field_type")
public class WorkCustomFieldType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static String OWNER = "owner";
	public static String RESOURCE = "resource";
	
	public WorkCustomFieldType() {
		super();
	}

	public WorkCustomFieldType(String code) {
		super(code);
	}
}
