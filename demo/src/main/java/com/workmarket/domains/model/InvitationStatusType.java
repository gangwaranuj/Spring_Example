package com.workmarket.domains.model;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.Entity;
import javax.persistence.Table;

@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
@Entity(name="invitation_status_type")
@Table(name="invitation_status_type")
public class InvitationStatusType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;

	public static final String SENT = "sent";
	public static final String REGISTERED = "registered";
	public static final String INSYSTEM = "insystem";
	public static final String DECLINED = "declined";

	public InvitationStatusType() {
		
	}
	
	public InvitationStatusType(String code) {
		super(code);
	}

}