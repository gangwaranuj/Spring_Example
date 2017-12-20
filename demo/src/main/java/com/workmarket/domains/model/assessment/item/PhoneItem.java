package com.workmarket.domains.model.assessment.item;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@AuditChanges
@DiscriminatorValue(AbstractItem.PHONE)
public class PhoneItem extends AbstractItem {

	private static final long serialVersionUID = 1L;

	@Override
	@Column(insertable=false, updatable=false)
	public Boolean isGraded() {
		return false;
	}

	@Override
	public void setGraded(Boolean graded) {
		super.setGraded(false);
	}

	@Transient
	public String getType() {
		return AbstractItem.PHONE;
	}

	@Transient
	public Boolean isManuallyGraded() {
		return true;
	}
}
