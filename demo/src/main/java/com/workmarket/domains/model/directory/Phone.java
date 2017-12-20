package com.workmarket.domains.model.directory;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

@Entity(name="phone")
@Table(name="phone")
@AuditChanges
public class Phone extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String phone;
	private String extension;
	private ContactContextType contactContextType;
	public static Integer phoneLengthWithFormat = 13;

	public Phone() {}

	public Phone(String phone, String extension, ContactContextType contactContextType) {
		this.phone = phone;
		this.extension = extension;
		this.contactContextType = contactContextType;
	}

	@Column(name="phone", nullable=false, length=25)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name="extension", length=15)
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
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
