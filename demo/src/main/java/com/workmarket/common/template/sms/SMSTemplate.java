package com.workmarket.common.template.sms;

import com.workmarket.common.template.TwoWayTypedTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.business.dto.SMSDTO;
import org.springframework.util.Assert;

public class SMSTemplate extends TwoWayTypedTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = -77692250004673081L;

	private Long providerId;
	private String toNumber;
	private final String templateTemplate;
	private final String headerTemplatePath;
	private final String templateTemplatePath;
	private final String footerTemplatePath;

	public SMSTemplate() {
		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
		this.headerTemplatePath = makeSMSTemplatePath(getHeaderTemplate());
		this.templateTemplatePath = makeSMSTemplatePath(getTemplateTemplate());
		this.footerTemplatePath = makeSMSTemplatePath(getFooterTemplate());
	}

	public SMSTemplate(Long providerId, String toNumber, Long fromId, Long toId, NotificationType notificationType) {
		super(fromId, toId, notificationType);
		this.providerId = providerId;
		this.toNumber = toNumber;
		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
		this.headerTemplatePath = makeSMSTemplatePath(getHeaderTemplate());
		this.templateTemplatePath = makeSMSTemplatePath(getTemplateTemplate());
		this.footerTemplatePath = makeSMSTemplatePath(getFooterTemplate());
	}

	public SMSTemplate(Long providerId, String toNumber) {
		Assert.notNull(toNumber);
		Assert.isTrue(toNumber.length() > 7);

		this.providerId = providerId;
		this.toNumber = toNumber;

		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
		this.headerTemplatePath = makeSMSTemplatePath(getHeaderTemplate());
		this.templateTemplatePath = makeSMSTemplatePath(getTemplateTemplate());
		this.footerTemplatePath = makeSMSTemplatePath(getFooterTemplate());
	}

	public String getHeaderTemplate() {
		return Constants.SMS_HEADER_TEMPLATE;
	}

	public String getTemplateTemplate() {
		return templateTemplate;
	}

	public String getFooterTemplate() {
		return Constants.SMS_FOOTER_TEMPLATE;
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

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public String getToNumber() {
		return toNumber;
	}

	public void setToNumber(String toNumber) {
		this.toNumber = toNumber;
	}

	public SMSDTO toDTO() {
		SMSDTO smsdto = new SMSDTO(getFromId());
		smsdto.setProviderId(getProviderId());
		smsdto.setToNumber(getToNumber());
		return smsdto;
	}

	@Override
	public String getPath() {
		return "/template/sms/";
	}

	static String makeSMSTemplatePath(final String pathName) {
		return Constants.SMS_TEMPLATE_DIRECTORY_PATH +
			"/" +
			pathName +
			Constants.SMS_TEMPLATE_EXTENSION;
	}
}
