package com.workmarket.service.infra.email;

import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.dto.EMailDTO;

public interface EmailService {

	EmailNotifyResponse sendEmail(EMailDTO eMailDTO);
	EmailNotifyResponse sendEmail(final String toName, final String toEmail, final String fromName, final String fromEmail, final String replyToEmail, final String subject, final String text);
}
