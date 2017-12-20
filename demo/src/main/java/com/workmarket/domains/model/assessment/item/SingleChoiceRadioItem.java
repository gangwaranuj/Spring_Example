package com.workmarket.domains.model.assessment.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.assessment.AbstractItem;
import com.workmarket.domains.model.assessment.AbstractItemWithChoices;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@AuditChanges
@DiscriminatorValue(AbstractItem.SINGLE_CHOICE_RADIO)
public class SingleChoiceRadioItem extends AbstractItemWithChoices {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getType() {
		return AbstractItem.SINGLE_CHOICE_RADIO;
	}

	@Transient
	public Boolean isManuallyGraded() {
		return getOtherAllowed();
	}
}
