package com.workmarket.domains.model.request;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.LookupEntity;

@Entity(name="requestStatusType")
@Table(name="request_status_type")
public class RequestStatusType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final String SENT = "sent";
	public static final String ACCEPTED = "accepted";
	public static final String DECLINED = "declined";
	public static final String IGNORED = "ignored";
	
	public RequestStatusType() {}
	
	public RequestStatusType(String code){
		super(code);
	}
}
