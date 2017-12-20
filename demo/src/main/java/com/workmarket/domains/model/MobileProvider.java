package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "mobileProvider")
@Table(name = "mobile_provider")
@AuditChanges
public class MobileProvider extends DeletableEntity {

	public static final MobileProvider ATT = new MobileProvider(1L, "ATT");
	public static final MobileProvider VERIZON = new MobileProvider(12L, "Verizon");
	private static final long serialVersionUID = 1L;
	private String name;
	private String smsGatewayEmail;

	public MobileProvider() {
	}

	public MobileProvider(Long id, String name) {
		setId(id);
		this.name = name;
	}

	@Column(name = "name", nullable = false, length = 200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "sms_gateway_email")
	public String getSmsGatewayEmail() {
		return smsGatewayEmail;
	}

	public void setSmsGatewayEmail(String smsGatewayEmail) {
		this.smsGatewayEmail = smsGatewayEmail;
	}
}
