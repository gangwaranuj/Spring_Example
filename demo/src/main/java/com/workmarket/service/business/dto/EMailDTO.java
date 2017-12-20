package com.workmarket.service.business.dto;

import com.google.common.collect.Lists;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.service.infra.communication.ReplyToType;

import java.util.List;

public class EMailDTO extends NotificationDTO {

	private static final long serialVersionUID = 1L;
	protected String subject;
	protected String text;
	protected String eMaliReplyToType = ReplyToType.TRANSACTIONAL.name(); // NOTE: check that serialization doesn't break before fixing this typo
	// if the recipient had not created a user record, use this as an override
	protected String toEmail;
	// used for additional instructions or data
	protected String description;
	protected String[] ccEmails;
	protected String[] bccEmails;
	protected String toName;
	protected String fromName;
	protected String fromEmail;
	protected String replyToEmail;
	protected List<FileDTO> attachments = Lists.newArrayList();

	public EMailDTO() {
		super();
	}

	public EMailDTO(EmailTemplate template, String text, String subject) {
		setFromId(template.getFromId());
		setToUserId(template.getToId());
		setSubject(subject);
		setToEmail(template.getToEmail());
		setText(text);
		setNotificationType(template.getNotificationType());
		seteMaliReplyToType(template.getReplyToType().name());
		setCcEmails(template.getCcEmails());
		setBccEmails(template.getBccEmails());
		setAttachments(template.getAttachments());
	}

	public Long getFromId() {
		return this.getFromUserId();
	}

	public void setFromId(Long fromId) {
		this.setFromUserId(fromId);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String geteMaliReplyToType() {
		return eMaliReplyToType;
	}

	public void seteMaliReplyToType(String eMaliReplyToType) {
		this.eMaliReplyToType = eMaliReplyToType;
	}

	public String[] getCcEmails() {
		return ccEmails;
	}

	public void setCcEmails(String[] ccEmails) {
		this.ccEmails = ccEmails;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmails = new String[]{ccEmail};
	}

	public String[] getBccEmails() {
		return bccEmails;
	}

	public void setBccEmails(String[] bccEmails) {
		this.bccEmails = bccEmails;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getReplyToEmail() {
		return replyToEmail;
	}

	public void setReplyToEmail(String replyToEmail) {
		this.replyToEmail = replyToEmail;
	}

	public List<FileDTO> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<FileDTO> attachments) {
		this.attachments = attachments;
	}

}
