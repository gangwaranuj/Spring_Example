package com.workmarket.domains.model.assessment.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.assessment.AbstractItemWithChoices;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@AuditChanges
@DiscriminatorValue(AbstractItem.MULTIPLE_CHOICE)
public class MultipleChoiceItem extends AbstractItemWithChoices {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getType() {
		return AbstractItem.MULTIPLE_CHOICE;
	}

	@Transient
	public Boolean isManuallyGraded() {
		return getOtherAllowed();
	}
}
