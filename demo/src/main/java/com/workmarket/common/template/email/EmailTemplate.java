package com.workmarket.common.template.email;

import com.google.common.collect.Lists;
import com.workmarket.common.template.TwoWayTypedTemplate;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.infra.communication.ReplyToType;

import java.util.Arrays;
import java.util.List;

public abstract class EmailTemplate extends TwoWayTypedTemplate {

	private static final long serialVersionUID = -2927423740361840180L;

	protected ReplyToType replyToType = ReplyToType.TRANSACTIONAL;

	// if toId is null, then use the to Email
	protected String toEmail;
	protected String[] ccEmails;
	protected String[] bccEmails;
	protected List<FileDTO> attachments = Lists.newArrayList();
	private PDFTemplate pdfTemplate;

	@Deprecated
	protected String subject;

	public EmailTemplate() {
	}

	public EmailTemplate(Long fromId, String toEmail) {
		super(fromId);
		this.toEmail = toEmail;
	}

	public EmailTemplate(Long fromId, Long toId, String subject) {
		super(fromId, toId);
		this.subject = subject;
	}

	public EmailTemplate(Long fromId, String toEmail, String subject) {
		super(fromId);
		this.toEmail = toEmail;
		this.subject = subject;
	}

	public EmailTemplate(Long fromId, Long toId, String toEmail, String subject) {
		super(fromId, toId);
		this.toEmail = toEmail;
	}

	public EmailTemplate(Long fromId, Long toId, String subject, NotificationType notificationType) {
		super(fromId, toId, notificationType);
		this.subject = subject;
	}

	public EmailTemplate(Long fromId, String toEmail, String subject, NotificationType notificationType) {
		super(fromId, notificationType);
		this.toEmail = toEmail;
		this.subject = subject;
	}

	/**
	 * Subject of the email. This is deprecated property. You should put a subject template in the email/subject folder
	 * 
	 * @return subject of the email
	 */
	@Deprecated
	public String getSubject() {
		return subject;
	}

	public String getToEmail() {
		return toEmail;
	}

	public ReplyToType getReplyToType() {
		return replyToType;
	}

	public void setReplyToType(ReplyToType replyToType) {
		this.replyToType = replyToType;
	}

	public String[] getCcEmails() {
		return ccEmails;
	}
	
	public void setCcEmails(String[] ccEmails) {
		if (ccEmails == null) {
			this.ccEmails = new String[0];
		} else {
			this.ccEmails = Arrays.copyOf(ccEmails, ccEmails.length);
		}
	}

	public String[] getBccEmails() {
		return bccEmails;
	}

	public void setBccEmails(String[] bccEmails) {
		if (ccEmails == null) {
			this.ccEmails = new String[0];
		} else {
			this.ccEmails = Arrays.copyOf(ccEmails, ccEmails.length);
		}
	}

	public void setAttachments(List<FileDTO> attachments) {
		this.attachments = attachments;
	}

	public List<FileDTO> getAttachments() {
		return attachments;
	}

	public void addAttachment(FileDTO attachment) {
		this.attachments.add(attachment);
	}

	public PDFTemplate getPdfTemplate() {
		return pdfTemplate;
	}

	public void setPdfTemplate(PDFTemplate pdfTemplate) {
		this.pdfTemplate = pdfTemplate;
	}

	@Override
	public String getPath() {
		return "/template/email/";
	}

	public boolean hasPdfTemplate() {
		return pdfTemplate != null;
	}
}
