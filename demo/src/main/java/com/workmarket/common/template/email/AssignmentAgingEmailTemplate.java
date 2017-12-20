package com.workmarket.common.template.email;

import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

import java.util.Calendar;
import java.util.List;

public class AssignmentAgingEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -2389775223915366525L;
	private List<SolrWorkData> assignmentList;
	private Calendar alertDate;

	public AssignmentAgingEmailTemplate(String toEmail, List<SolrWorkData> assignments) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toEmail);
		setReplyToType(ReplyToType.TRANSACTIONAL);
		this.assignmentList = assignments;
		this.alertDate = Calendar.getInstance();
	}

	public List<SolrWorkData> getAssignmentList() {
		return assignmentList;
	}

	public Calendar getAlertDate() {
		return alertDate;
	}
}
