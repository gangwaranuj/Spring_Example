package com.workmarket.domains.model.assessment.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.assessment.AbstractItemWithChoices;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@AuditChanges
@Deprecated
@DiscriminatorValue("truefalse")
public class TrueFalseQuestion extends AbstractItemWithChoices {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getType() {
		return "truefalse";
	}

	@Transient
	public Boolean isManuallyGraded() {
		return false;
	}
}
