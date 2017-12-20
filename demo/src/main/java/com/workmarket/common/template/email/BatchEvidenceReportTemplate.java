package com.workmarket.common.template.email;

import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.configuration.Constants;

public class BatchEvidenceReportTemplate extends EmailTemplate{

	private static final long serialVersionUID = 3996785677347226606L;

	public BatchEvidenceReportTemplate(String toEmail, PDFTemplate pdfTemplate) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL,toEmail);
		setPdfTemplate(pdfTemplate);
	}
}
