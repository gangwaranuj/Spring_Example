package com.workmarket.domains.model.assessment.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@AuditChanges
@DiscriminatorValue(AbstractItem.NUMERIC)
public class NumericItem extends AbstractItem {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getType() {
		return AbstractItem.NUMERIC;
	}

	@Transient
	public Boolean isManuallyGraded() {
		return true;
	}
}
