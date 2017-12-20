package com.workmarket.domains.model.directory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="website")
@Table(name="website")
@AuditChanges
public class Website extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String website;
	private ContactContextType contactContextType;

	public Website() {}

	public Website(String website, ContactContextType contactContextType) {
		this.website = website;
		this.contactContextType = contactContextType;
	}

	@Column(name="website", nullable=false, length=255)
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "contact_context_type")
	public ContactContextType getContactContextType() {
		return contactContextType;
	}

	public void setContactContextType(ContactContextType contactContextType) {
		this.contactContextType = contactContextType;
	}

}
