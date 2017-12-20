package com.workmarket.service.infra.email;

import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.dto.EMailDTO;

public interface RawEmailService {

    EmailNotifyResponse sendEmail(final String toName, final String toEmail, final String fromName, final String fromEmail, final String replyToEmail, final String subject, final String text);

    void sendSMS(final String toName, final String toEmail, final String fromName, final String fromEmail, final String replyToEmail, final String subject, final String text);

    EmailNotifyResponse sendEmail(EMailDTO emailDTO);
}
