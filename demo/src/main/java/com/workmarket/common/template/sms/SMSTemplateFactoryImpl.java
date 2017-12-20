package com.workmarket.common.template.sms;

import com.workmarket.common.template.email.AbstractWorkEmailTemplate;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.domains.model.notification.NotificationType;
import org.springframework.stereotype.Service;

@Service
public class SMSTemplateFactoryImpl implements SMSTemplateFactory {

	@Override
	public PhoneVerificationSMSTemplate buildPhoneVerificationSMSTemplate(Long providerId, String toNumber, String msg) {
		return new PhoneVerificationSMSTemplate(providerId, toNumber, msg);
	}

	@Override
	public SMSTemplate buildSMSTemplateFromEmailTemplate(Long providerId, String toNumber, String msg, EmailTemplate template) {

		if (template.getNotificationType().getCode().equals(NotificationType.RESOURCE_WORK_CONFIRM)) {
			AbstractWorkEmailTemplate templ = (AbstractWorkEmailTemplate) template;
			return new WorkResourceConfirmationSMSTemplate(providerId, toNumber, msg,
				templ.getWorkTitle(), templ.getWorkShortUrl(), templ.getWorkRelativeURI());
		}

		return new SMSTemplate(providerId, toNumber);
	}

}
