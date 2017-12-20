package com.workmarket.service.infra.email;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.email.PublicEmailDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class EmailServiceImpl implements EmailService {

	private static final Log logger = LogFactory.getLog(EmailServiceImpl.class);

	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired private RawEmailService rawEmailService;
	@Autowired private MetricRegistry metricRegistry;

	private WMMetricRegistryFacade wmMetricRegistryFacade;
	private Meter emailSendMeter;

	@PostConstruct
	private void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "email-service");
		emailSendMeter = wmMetricRegistryFacade.meter("send");
	}

	@Override
	public EmailNotifyResponse sendEmail(EMailDTO eMailDTO) {
		logger.trace("EmailDTO found - checking if valid");

		ReplyToType replyToType = ReplyToType.valueOf(eMailDTO.geteMaliReplyToType());
		Assert.notNull(replyToType);

		if (!ReplyToType.PUBLIC_USER.equals(replyToType)) {
			Assert.notNull(eMailDTO.getFromId());
		}
		Assert.notNull(eMailDTO.getSubject());
		Assert.notNull(eMailDTO.getText());

		logger.trace("Preparing email message");

		String fromEmail = null;
		String displayName = null;
		String replyToEMail = null;

		String toEmail = eMailDTO.getToEmail();
		String toName = "";

		User fromUser = null;
		User toUser;

		if (eMailDTO.getToUserId() != null) {
			toUser = userService.findUserById(eMailDTO.getToUserId());
			Assert.notNull(toUser);
			toName = toUser.getFullName();

			if (StringUtils.isBlank(toEmail)) {
				toEmail = toUser.getEmail();
			}
		}

		logger.debug("Check email type and preparing display information: " + replyToType);
		switch (replyToType) {
			case TRANSACTIONAL:
				logger.trace("Transactional Email Found!");
				fromUser = userService.findUserById(Constants.EMAIL_USER_ID_TRANSACTIONAL);
				fromEmail = fromUser.getEmail();
				displayName = fromUser.getFullName();
				replyToEMail = fromUser.getReplyToEmail();
				break;
			case INVITATION:
				logger.trace("Invitation Email Found!");
				fromUser = userService.findUserById(Constants.EMAIL_USER_ID_INVITES);
				fromEmail = fromUser.getEmail();
				displayName = fromUser.getFullName();
				User user = userService.getUser(eMailDTO.getFromId());
				replyToEMail = user.getEmail();
				break;
			case USER:
				logger.trace("User Email Found!");
				User displayUser = userService.findUserById(eMailDTO.getFromId());
				fromUser = displayUser;
				displayName = displayUser.getCompany().getEffectiveName() + Constants.EMAIL_DISPLAY_NAME;
				replyToEMail = isNotBlank(displayUser.getReplyToEmail()) ?
						displayUser.getReplyToEmail() :
						displayUser.getEmail();

				User sendUser = userService.getUser(Constants.EMAIL_USER_ID_TRANSACTIONAL);
				fromEmail = sendUser.getEmail();
				break;
			case TRANSACTIONAL_FROM_USER:
				logger.trace("Transactional from User Found!");
				fromUser = userService.findUserById(eMailDTO.getFromId());
				displayName = fromUser.getCompany().getEffectiveName() + Constants.EMAIL_DISPLAY_NAME;

				User transactionalFromUser = userService.getUser(Constants.EMAIL_USER_ID_TRANSACTIONAL);
				fromEmail = transactionalFromUser.getEmail();
				replyToEMail = transactionalFromUser.getReplyToEmail();
				break;
			case TRANSACTIONAL_FROM_COMPANY:
				logger.trace("Transactional from Company Found!");
				Company fromCompany = profileService.findCompany(eMailDTO.getFromId());
				displayName = fromCompany.getEffectiveName() + Constants.EMAIL_DISPLAY_NAME;

				fromUser = userService.findUserById(Constants.EMAIL_USER_ID_TRANSACTIONAL);
				fromEmail = fromUser.getEmail();
				replyToEMail = fromUser.getReplyToEmail();
				break;
			case INVITATION_FROM_USER:
				logger.trace("Invitation From User!");
				fromUser = userService.findUserById(eMailDTO.getFromId());
				displayName = fromUser.getCompany().getEffectiveName() + Constants.EMAIL_DISPLAY_NAME;

				User invitationFromUser = userService.getUser(Constants.EMAIL_USER_ID_INVITES);
				fromEmail = invitationFromUser.getEmail();
				replyToEMail = invitationFromUser.getReplyToEmail();
				break;
			case INVOICE:
				logger.trace("Invoices email");
				fromUser = userService.findUserById(Constants.EMAIL_USER_ID_TRANSACTIONAL);
				fromEmail = Constants.INVOICES_EMAIL;
				displayName = fromUser.getFullName();
				replyToEMail = fromUser.getReplyToEmail();
				break;
			case PUBLIC_USER:
				logger.trace("public user email");
				fromUser = userService.findUserById(Constants.EMAIL_USER_ID_TRANSACTIONAL);
				fromEmail = fromUser.getEmail();
				displayName = eMailDTO.getFromName();
				replyToEMail = eMailDTO.getFromEmail();
				break;
			default:
				Assert.isTrue(false, "Unable to process an email of unknown reply type");
		}

		logger.trace("[email] >>>");
		logger.trace(String.format("Subject: %s", eMailDTO.getSubject()));
		logger.trace(String.format("To: %s <%s>", toName, toEmail));
		logger.trace(String.format("From: %s <%s>", displayName, fromEmail));
		logger.trace(String.format("# of attachments: %d", eMailDTO.getAttachments().size()));

		Assert.notNull(fromUser);
		Assert.isTrue(StringUtils.isNotBlank(toEmail) || ArrayUtils.isNotEmpty(eMailDTO.getBccEmails()));
		Assert.hasText(fromEmail);
		Assert.hasText(displayName);
		Assert.hasText(replyToEMail);

		// Filling the missing data in the EmailDTO
		eMailDTO.setToName(toName);
		eMailDTO.setToEmail(toEmail);
		eMailDTO.setFromName(displayName);
		eMailDTO.setFromEmail(fromEmail);
		eMailDTO.setReplyToEmail(replyToEMail);

		if (ReplyToType.PUBLIC_USER.equals(replyToType) && eMailDTO instanceof PublicEmailDTO) {
			eMailDTO.setText(((PublicEmailDTO) eMailDTO).getFormattedText());
		}

		final EmailNotifyResponse response = rawEmailService.sendEmail(eMailDTO);
		emailSendMeter.mark();
		return response;
	}

	@Override
	public EmailNotifyResponse sendEmail(
			final String toName,
			final String toEmail,
			final String fromName,
			final String fromEmail,
			final String replyToEmail,
			final String subject,
			final String text) {
		return rawEmailService.sendEmail(
				toName,
				toEmail,
				fromName,
				fromEmail,
				replyToEmail,
				subject,
				text);
	}

}
