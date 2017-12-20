package com.workmarket.domains.model.directory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="email")
@Table(name="email")
@AuditChanges
public class Email extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String email;
	private ContactContextType contactContextType;

	public Email() {}

	public Email(String email, ContactContextType contactContextType) {
		this.email = email;
		this.contactContextType = contactContextType;
	}

	@Column(name="email", nullable=false, length=255)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
