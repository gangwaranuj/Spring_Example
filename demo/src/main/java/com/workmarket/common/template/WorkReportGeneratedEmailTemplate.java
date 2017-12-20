package com.workmarket.common.template;

import com.google.common.collect.Lists;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.thrift.work.display.ReportResponse;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;

public class WorkReportGeneratedEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -8732907212118314979L;
	private String reportName;
	private Long reportId;
	private ReportResponse response;

	public WorkReportGeneratedEmailTemplate(Long reportId, String reportName, Set<String> recipients, FileDTO file, ReportResponse response) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, "");
		if (CollectionUtils.isNotEmpty(recipients) && recipients.size() == 1) {
			this.toEmail = recipients.iterator().next();
		} else {
			this.toEmail = Constants.EMAIL_REPORTS_REPLY;
			if (CollectionUtils.isNotEmpty(recipients)) {
				this.bccEmails = recipients.toArray(new String[recipients.size()]);
			}
		}
		this.attachments = Lists.newArrayList(file);
		this.reportName = reportName;
		this.reportId = reportId;
		this.response = response;
	}

	public String getReportName() {
		return reportName;
	}

	public Long getReportId() {
		return reportId;
	}

	public ReportResponse getResponse() {
		return response;
	}
}
