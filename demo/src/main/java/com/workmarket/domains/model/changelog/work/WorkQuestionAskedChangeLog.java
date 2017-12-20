package com.workmarket.domains.model.changelog.work;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.Transient;


@Entity
@NamedQueries({
})
@DiscriminatorValue(WorkChangeLog.WORK_QUESTION_ASKED)
@AuditChanges
public class WorkQuestionAskedChangeLog extends WorkChangeLog {

	private static final long serialVersionUID = 1516939166534196075L;

	public WorkQuestionAskedChangeLog() {
	}

	public WorkQuestionAskedChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor) {
		super(work, actor, masqueradeActor, onBehalfOfActor);
	}

	@Transient
	public static String getDescription() {
		return "Question asked";
	}
}
