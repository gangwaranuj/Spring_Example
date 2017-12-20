package com.workmarket.common.template.email;

import com.workmarket.common.template.NotificationModel;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.Template;
import com.workmarket.common.template.UserNotification;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class NotificationEmailTemplate extends EmailTemplate implements NotificationModel, UserNotification {

	private static final long serialVersionUID = 7484975609152450956L;
	private NotificationTemplate parent;
	private Long onBehalfOfId;

	private String subjectTemplatePath;
	private String templateTemplate;
	private final String headerTemplatePath;
	private String templateTemplatePath;
	private final String footerTemplatePath;

	public NotificationEmailTemplate(Long fromId, Long toId, Long onBehalfOfId, String subject, NotificationType notificationType, NotificationTemplate parent, ReplyToType replyToType, String[] ccEmails, PDFTemplate pdfTemplate) {
		super(fromId, toId, subject, notificationType);
		this.parent = parent;
		this.onBehalfOfId = onBehalfOfId;
		this.subjectTemplatePath = Template.makeEmailSubjectPath(canonicalizeClassName(
			this.parent.getClass().getSimpleName()).replace("NotificationTemplate", "EmailTemplate"));
		this.templateTemplate = canonicalizeClassName(this.parent.getClass().getSimpleName())
			.replace("NotificationTemplate", "EmailTemplate");
		this.headerTemplatePath = Template.makeEmailPath(getHeaderTemplate());
		this.templateTemplatePath = Template.makeEmailPath(getTemplateTemplate());
		this.footerTemplatePath = Template.makeEmailPath(getFooterTemplate());
		setReplyToType(replyToType);
		setCcEmails(ccEmails);
		setPdfTemplate(pdfTemplate);
	}

	public NotificationTemplate getParent() {
		return parent;
	}

	public void setParent(NotificationTemplate parent) {
		this.parent = parent;
		this.subjectTemplatePath = Template.makeEmailSubjectPath(canonicalizeClassName(
			this.parent.getClass().getSimpleName()).replace("NotificationTemplate", "EmailTemplate"));
		this.templateTemplate = canonicalizeClassName(this.parent.getClass().getSimpleName())
			.replace("NotificationTemplate", "EmailTemplate");
		this.templateTemplatePath = Template.makeEmailPath(getTemplateTemplate());
	}

	public String getHeaderTemplate() {
		return Constants.EMAIL_HEADER_TEMPLATE;
	}

	public String getTemplateTemplate() {
		return templateTemplate;
	}

	public String getFooterTemplate() {
		return Constants.EMAIL_FOOTER_TEMPLATE;
	}

	public String getSubjectTemplatePath() {
		return subjectTemplatePath;
	}

	public String getHeaderTemplatePath() {
		return headerTemplatePath;
	}

	public String getTemplateTemplatePath() {
		return templateTemplatePath;
	}

	public String getFooterTemplatePath() {
		return footerTemplatePath;
	}

	public Long getOnBehalfOfId() {
		return onBehalfOfId;
	}

	public void setOnBehalfOfId(Long onBehalfOfId) {
		this.onBehalfOfId = onBehalfOfId;
	}

	@Override
	public Object getModel() {
		return parent;
	}
}
