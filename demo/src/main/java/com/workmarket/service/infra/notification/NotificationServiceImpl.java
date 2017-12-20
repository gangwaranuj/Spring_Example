package com.workmarket.service.infra.notification;

import com.google.common.collect.Sets;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.email.AbstractWorkEmailTemplate;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.push.PushTemplate;
import com.workmarket.common.template.sms.SMSTemplate;
import com.workmarket.common.template.sms.SMSTemplateFactory;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.UserDeviceAssociation;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserNotificationPreferencePojo;
import com.workmarket.service.business.UserNotificationPrefsService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.PushDTO;
import com.workmarket.service.business.dto.SMSDTO;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.notification.NotificationErrorReporter;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.LocaleUtilities;
import com.workmarket.utility.RandomUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired private UserService userService;
	@Autowired private SMSTemplateFactory SMSTemplateFactory;
	@Autowired private JmsService jmsService;
	@Autowired private UserNotificationPrefsService userNotificationPrefsService;
	@Autowired private TemplateService templateService;
	@Autowired private ProfileService profileService;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private NotificationErrorReporter notificationErrorReporter;

	private static final Log logger = LogFactory.getLog(NotificationServiceImpl.class);

	private static final int MOBILE_PHONE_VERIFICATION_EXPIRATION_IN_MINUTES = 60;
	private static final int MOBILE_VERIFICATION_CODE_LENGTH = 4;
	WMMetricRegistryFacade metricRegistryFacade;

	@PostConstruct
	private void init() {
		metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "notification-service");
	}

	@Override
	public void sendNotification(EmailTemplate template) {
		sendNotification(template, Calendar.getInstance());
	}

	@Override
	public void sendNotification(EmailTemplate template, Calendar scheduleDate) {
		if (template.getToId() != null && template.getNotificationType() != null) {
			UserNotificationPreferencePojo pref = userNotificationPrefsService.findByUserAndNotificationType(template.getToId(), template.getNotificationType().getCode());

			if (pref != null) {
				if (pref.getEmailFlag()) {
					sendEmail(template, scheduleDate);
				}

				// Verify that user has requested sms notifications,
				if (pref.getSmsFlag()) {
					User user = userService.getUser(template.getToId());
					Assert.notNull(user);
					Assert.notNull(user.getProfile());

					if (userService.isAvailableForNotification(template.getToId())) {
						//The SMSTemplate depends on the type of notification
						SMSTemplate smsTemplate = SMSTemplateFactory.buildSMSTemplateFromEmailTemplate(user.getProfile().getMobileProviderId(), user.getProfile().getSmsPhone(), null, template);
						dispatchSMS(smsTemplate);
					}
				}
			}

		} else {
			sendEmail(template, scheduleDate);
		}
	}

	@Override
	public void sendWorkNotifyAsync(NotificationTemplate template, List<Long> userIdsWithSms, List<Long> userIdsWithPush, Calendar scheduledDate) {
		if (template.getToId() != null && template.getNotificationType() != null) {
			// If push enabled, send a push ...
			Long userId = template.getToId();
			if (userIdsWithPush.contains(userId) && template.getPushTemplate() != null) {
				PushTemplate pushTemplate = template.getPushTemplate();
				dispatchPush(pushTemplate, scheduledDate);
			// ... otherwise send an SMS if enabled
			} else if (userIdsWithSms.contains(userId) && template.getSMSTemplate() != null) {
				User user = userService.getUser(template.getToId());
				Assert.notNull(user);
				Assert.notNull(user.getProfile());

				SMSTemplate smsTemplate = template.getSMSTemplate();
				smsTemplate.setToNumber(user.getProfile().getSmsPhone()); //! needed?
				dispatchSMS(smsTemplate, scheduledDate);
			}
		}
	}

	private void dispatchPush(PushTemplate pushTemplate, Calendar scheduledDate) {
		logger.debug("[Push] start dispatchPush");

		String text = templateService.render(pushTemplate);

		if (StringUtils.isEmpty(text)) {
			notificationErrorReporter.sendNotificationErrorToKafka(pushTemplate, NotificationErrorReporter.ERROR_EMPTY_TEXT);
			return;
		}

		Set<UserDeviceAssociation> deviceAssociations = Sets.newHashSet(userService.findAllUserDevicesByUserId(pushTemplate.getToId()));
		for (UserDeviceAssociation association : deviceAssociations) {
			PushDTO pushDTO = new PushDTO();
			pushDTO.setRegid(association.getDeviceUid());
			pushDTO.setType(association.getDeviceType());
			pushDTO.setMessage(text);
			pushDTO.setToUserId(pushTemplate.getToId());
			metricRegistryFacade.meter("jms-push").mark();
			jmsService.sendMessage(pushDTO, scheduledDate);
		}
	}

	@Override
	public void sendNotification(NotificationTemplate template) {
		sendNotification(template, Calendar.getInstance());
	}

	@Override
	public void sendNotification(NotificationTemplate template, Calendar scheduleDate) {
		if (template != null) {
			metricRegistryFacade.meter("jms-notification").mark();
			jmsService.sendMessage(template, scheduleDate);
		}
	}

	@Override
	public void sendNotifications(List<NotificationTemplate> templates) {
		for (NotificationTemplate template : templates) {
			sendNotification(template);
		}
	}

	@Override
	public void sendNotificationsDirectly(List<NotificationTemplate> template) {
		try {
			metricRegistryFacade.meter("directnotification").mark();
			notificationDispatcher.dispatchNotifications(template);
		}
		catch (Exception e) {
			logger.error("An unexpected error occurred trying to send notification!", e);
		}
	}

	private void sendEmail(EmailTemplate template, Calendar scheduledDate) {
		logger.debug("Preparing text for email");

		String text = templateService.render(template);

		if (StringUtils.isEmpty(text)) {
			notificationErrorReporter.sendNotificationErrorToKafka(template, NotificationErrorReporter.ERROR_EMPTY_TEXT);
			return;
		}

		String subject = (StringUtils.isNotBlank(template.getSubject())) ? template.getSubject() : templateService.renderSubject(template);

		EMailDTO emailDTO = new EMailDTO(template, text, subject);

		if (template instanceof AbstractWorkEmailTemplate) {
			emailDTO.setWorkId(((AbstractWorkEmailTemplate) template).getWorkId());
		}

		logger.debug("Putting email message JMS queue");
		metricRegistryFacade.meter("jms-email").mark();
		jmsService.sendMessage(emailDTO, scheduledDate);
	}

	@Override
	public void sendMobileVerificationCode(Long userId) {
		Assert.notNull(userId);

		String code = RandomUtilities.generateNumericString(MOBILE_VERIFICATION_CODE_LENGTH);

		User user = userService.getUser(userId);

		Assert.notNull(user);
		Assert.isTrue(StringUtils.isNotBlank(user.getProfile().getSmsPhone()));

		user.getProfile().setSmsPhoneVerificationCode(code);

		DateTime dt = new DateTime();
		dt = dt.plusMinutes(MOBILE_PHONE_VERIFICATION_EXPIRATION_IN_MINUTES);

		user.getProfile().setSmsPhoneVerificationCodeExpiration(dt.toCalendar(LocaleUtilities.getDefaultLocale()));
		profileService.saveOrUpdateProfile(user.getProfile());

		//The SMSTemplate depends on the type of notification
		SMSTemplate smsTemplate = SMSTemplateFactory.buildPhoneVerificationSMSTemplate(user.getProfile().getMobileProviderId(), user.getProfile().getSmsPhone(), code);
		dispatchSMS(smsTemplate);
	}

	@Override
	public boolean verifyMobileVerificationCode(Long userId, String code) {
		Assert.notNull(userId);
		Assert.notNull(code);

		User user = userService.getUser(userId);

		Assert.notNull(user);
		Assert.notNull(user.getProfile());

		boolean result = code.equals(user.getProfile().getSmsPhoneVerificationCode()) && user.getProfile().getSmsPhoneVerificationCodeExpiration() != null
				&& DateUtilities.getCalendarNow().compareTo(user.getProfile().getSmsPhoneVerificationCodeExpiration()) < 0;

		if (result) {
			user.getProfile().setSmsPhoneVerificationCode(null);
			user.getProfile().setSmsPhoneVerificationCodeExpiration(null);
			user.getProfile().setSmsPhoneVerified(true);
			profileService.saveOrUpdateProfile(user.getProfile());
		}

		return result;
	}

	private void dispatchSMS(SMSTemplate smsTemplate) {
		dispatchSMS(smsTemplate, Calendar.getInstance());
	}

	private void dispatchSMS(SMSTemplate smsTemplate, Calendar scheduledDate) {
		SMSDTO smsdto = null;
		String textSms = templateService.render(smsTemplate);

		if (StringUtils.isEmpty(textSms)) {
			notificationErrorReporter.sendNotificationErrorToKafka(smsTemplate, NotificationErrorReporter.ERROR_EMPTY_TEXT);
			return;
		}

		if (smsTemplate.getToId() == null) {
			smsdto = smsTemplate.toDTO();
		} else {
			User user = userService.getUser(smsTemplate.getToId());
			if (user.getProfile().getSmsPhoneVerified()) {
				smsdto = new SMSDTO(smsTemplate.getFromId(), user.getProfile().getMobileProviderId(), user.getProfile().getSmsPhone());
			}
		}

		if (smsdto != null) {
			smsdto.setMsg(textSms);
			metricRegistryFacade.meter("jms-sms").mark();
			jmsService.sendMessage(smsdto, scheduledDate);
		}
	}
}
