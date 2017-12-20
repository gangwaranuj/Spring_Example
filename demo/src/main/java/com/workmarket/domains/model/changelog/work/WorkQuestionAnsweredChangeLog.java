package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.workmarket.domains.model.audit.AuditChanges;


@Entity
@DiscriminatorValue(WorkChangeLog.WORK_QUESTION_ANSWERED)
@AuditChanges
public class WorkQuestionAnsweredChangeLog extends WorkQuestionAskedChangeLog {

	private static final long serialVersionUID = 7441689383696088139L;

	public WorkQuestionAnsweredChangeLog() {}

	public WorkQuestionAnsweredChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor) {
		super(work, actor, masqueradeActor, onBehalfOfActor);
	}

	@Transient
	public static String getDescription() {
		return "Question answered";
	}
}
