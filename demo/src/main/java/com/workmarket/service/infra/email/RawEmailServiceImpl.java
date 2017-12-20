package com.workmarket.service.infra.email;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.workmarket.notification.NotificationClient;
import com.workmarket.notification.vo.EmailAttachment;
import com.workmarket.notification.vo.EmailNotifyRequestBuilder;
import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class RawEmailServiceImpl implements RawEmailService {
	private static final Logger logger = LoggerFactory.getLogger(RawEmailServiceImpl.class);
	private final NotificationClient notificationClient;
	private final WebRequestContextProvider webRequestContextProvider;

	@Autowired
	public RawEmailServiceImpl(final NotificationClient notificationClient,
	                           final WebRequestContextProvider webRequestContextProvider) {
		this.notificationClient = notificationClient;
		this.webRequestContextProvider = webRequestContextProvider;
	}

	/**
	 * This is low level email service. Never use this service all email
	 * communications should go through NotificationService Talk to ChrisB first
	 * if you want to use it
	 */
	@Override
	public EmailNotifyResponse sendEmail(
		final String toName, final String toEmail, final String fromName,
		final String fromEmail, final String replyToEmail, final String subject, final String text) {

		return sendEmail(toName, toEmail, fromName, fromEmail, replyToEmail, subject, text,
			ArrayUtils.EMPTY_STRING_ARRAY, ArrayUtils.EMPTY_STRING_ARRAY, new ArrayList<FileDTO>());
	}

	@Override
	public void sendSMS(
		final String toName, final String toEmail, final String fromName, final String fromEmail,
		final String replyToEmail, final String subject, final String text) {

		sendEmail(toName, toEmail, fromName, fromEmail, replyToEmail, subject, text,
			ArrayUtils.EMPTY_STRING_ARRAY, ArrayUtils.EMPTY_STRING_ARRAY, new ArrayList<FileDTO>());

	}

	@Override
	public EmailNotifyResponse sendEmail(EMailDTO emailDTO) {
		return sendEmail(emailDTO.getToName(), emailDTO.getToEmail(),
			emailDTO.getFromName(), emailDTO.getFromEmail(),
			emailDTO.getReplyToEmail(), emailDTO.getSubject(),
			emailDTO.getText(), emailDTO.getCcEmails(),
			emailDTO.getBccEmails(), emailDTO.getAttachments());
	}

	private EmailNotifyResponse sendEmail(
		final String toName, final String toEmail, final String fromName,
		final String fromEmail, final String replyToEmail, final String subject,
		final String text, final String[] ccEmails, final String[] bccEmails,
		final List<FileDTO> attachments) {
		try {
			return microserviceSendEmail(toName, toEmail, fromName, fromEmail, replyToEmail, subject,
				text, ccEmails, bccEmails, attachments);
		} finally {
			deleteInitialAttachments(attachments);
		}
	}

	private List<String> safeCopy(final String[] array) {
		return array == null ? ImmutableList.<String>of() : ImmutableList.copyOf(array);
	}

	private EmailNotifyResponse microserviceSendEmail(
			final String toName, final String toEmail, final String fromName,
			final String fromEmail, final String replyToEmail, final String subject,
			final String text, final String[] ccEmails, final String[] bccEmails,
			final List<FileDTO> attachments) {
		final List<EmailAttachment> converted = convertAttachments(attachments);
		logger.info("sending email notification off to the notification microservice");
		final EmailNotifyResponse result = notificationClient.sendEmail(new EmailNotifyRequestBuilder()
						.setToName(toName)
						.setToEmail(toEmail)
						.setFromName(fromName)
						.setFromEmail(fromEmail)
						.setReplyToEmail(replyToEmail)
						.setSubject(subject)
						.setText(text)
						.setCcAddrs(safeCopy(ccEmails))
						.setBccAddrs(safeCopy(bccEmails))
						.setAttachments(converted)
						.build(),
				webRequestContextProvider.getRequestContext())
				.toBlocking()
				.single();
		logger.info("send to microservice succeeded? [true]");
		return result;
	}

	private List<EmailAttachment> convertAttachments(final List<FileDTO> attachments) {
		final ImmutableList.Builder<EmailAttachment> result = ImmutableList.builder();
		for (final FileDTO attachment : attachments) {
			try {
				result.add(new EmailAttachment(attachment.getName(),
                    attachment.getDescription(), attachment.getMimeType(), attachment.getFileByteSize(),
                    IOUtils.toByteArray(new FileInputStream(attachment.getSourceFilePath()))));
			} catch (final IOException e) {
				throw Throwables.propagate(e);
			}
		}

		return result.build();
	}

	private void deleteInitialAttachments(List<FileDTO> attachments) {
		if (isNotEmpty(attachments)) {
			for (FileDTO dto : attachments) {
				boolean deleted = FileUtils.deleteQuietly(new File(dto.getSourceFilePath()));
				logger.debug(String.format("Deleting file %s, success = %s", dto.getSourceFilePath(), deleted));
			}
		}
	}
}
