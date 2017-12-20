package com.workmarket.service.business.mock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

public class MockJavaMailSender implements JavaMailSender {

	private static final Log logger = LogFactory.getLog(MockJavaMailSender.class);
	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		logger.debug("send(SimpleMailMessage simpleMessage)");
	}

	@Override
	public void send(SimpleMailMessage[] simpleMessages) throws MailException {
		logger.debug("send(SimpleMailMessage[] simpleMessages)");
	}

	@Override
	public MimeMessage createMimeMessage() {
		logger.debug("createMimeMessage()");
		return null;
	}

	@Override
	public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
		logger.debug("createMimeMessage(InputStream contentStream)");
		return null;
	}

	@Override
	public void send(MimeMessage mimeMessage) throws MailException {
		logger.debug("send(MimeMessage mimeMessage)");
	}

	@Override
	public void send(MimeMessage[] mimeMessages) throws MailException {
		logger.debug("send(MimeMessage[] mimeMessages)");
	}

	@Override
	public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
		logger.debug("send(MimeMessagePreparator mimeMessagePreparator)");
	}

	@Override
	public void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException {
		logger.debug("send(MimeMessagePreparator[] mimeMessagePreparators)");
	}
}
