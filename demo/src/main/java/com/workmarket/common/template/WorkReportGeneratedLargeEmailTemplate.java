package com.workmarket.common.template;

import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.configuration.Constants;
import org.apache.commons.collections.CollectionUtils;

import java.util.Set;

public class WorkReportGeneratedLargeEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -8732907212718314979L;
	private String reportName;
	private Long reportId;
	private String downloadUri;

	public WorkReportGeneratedLargeEmailTemplate(String reportKey, String reportName, Set<String> recipients, String downloadUri) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, "");
		if (CollectionUtils.isNotEmpty(recipients) && recipients.size() == 1) {
			this.toEmail = recipients.iterator().next();
		} else {
			this.toEmail = Constants.EMAIL_REPORTS_REPLY;
			if (CollectionUtils.isNotEmpty(recipients)) {
				this.bccEmails = recipients.toArray(new String[recipients.size()]);
			}
		}
		this.reportName = reportName;
		this.downloadUri = downloadUri;
	}

	public String getReportName() {
		return reportName;
	}

	public Long getReportId() {
		return reportId;
	}

	public String getDownloadUri() {
		return downloadUri;
	}
}
