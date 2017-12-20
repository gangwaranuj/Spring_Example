package com.workmarket.domains.model.linkedin;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.workmarket.domains.model.DeletableEntity;

@MappedSuperclass
@Access(AccessType.FIELD)
public class LinkedInEntity extends DeletableEntity implements Serializable
{
	private static final long serialVersionUID = 7912265973466680313L;

	@Column(name="linkedin_id")
	protected String linkedInId;

	public LinkedInEntity() {
	}

	public String getLinkedInId() {
		return linkedInId;
	}

	public void setLinkedInId(String linkedInId) {
		this.linkedInId = linkedInId;
	}
}
