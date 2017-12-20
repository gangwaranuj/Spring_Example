package com.workmarket.service.infra.notification;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.common.kafka.KafkaData;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.AbstractWorkNotificationTemplate;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.WorkResourceNotificationTemplate;
import com.workmarket.common.template.email.AbstractWorkEmailTemplate;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.notification.UserNotificationTemplate;
import com.workmarket.common.template.push.PushTemplate;
import com.workmarket.common.template.sms.SMSTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserDeviceAssociation;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.id.IdGenerator;
import com.workmarket.notification.NotificationClient;
import com.workmarket.notification.sms.vo.SmsNotifyResponse;
import com.workmarket.notification.template.vo.TemplateRenderRequest;
import com.workmarket.notification.template.vo.TemplateStringResponse;
import com.workmarket.notification.vo.EmailNotifyResponse;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserNotificationPreferencePojo;
import com.workmarket.service.business.UserNotificationPrefsService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.business.wrapper.PushResponse;
import com.workmarket.service.business.wrapper.PushResponseAndDeviceType;
import com.workmarket.service.infra.PushService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.email.EmailService;
import com.workmarket.service.infra.sms.SMSService;
import com.workmarket.service.notification.NotificationErrorReporter;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.PDFUtilities;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import static org.apache.commons.lang3.BooleanUtils.toBooleanDefaultIfNull;

@Service
public class NotificationDispatcherImpl implements NotificationDispatcher {
	@Autowired private UserService userService;
	@Autowired private RegistrationService registrationService;
	@Autowired private TemplateService templateService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private SMSService smsService;
	@Autowired private EmailService emailService;
	@Autowired private ProfileService profileService;
	@Autowired private NotificationValidator notificationValidator;
	@Autowired private UserNotificationPrefsService userNotificationPrefsService;
	@Autowired private PushService pushService;
	@Autowired MetricRegistry metricRegistry;
	@Autowired private NotificationErrorReporter notificationErrorReporter;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private IdGenerator idGenerator;
	WMMetricRegistryFacade metricRegistryFacade;
	@Autowired private NotificationClient notificationClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Autowired @Qualifier("AppKafkaClient") private KafkaClient kafkaClient;

	private static final Logger logger = LoggerFactory.getLogger(NotificationDispatcherImpl.class);

	@PostConstruct
	private void init() {
		metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "notification-dispatcher");
	}

	@Override
	public void dispatchNotification(NotificationTemplate notificationTemplate) {
		Assert.notNull(notificationTemplate);
		Assert.notNull(notificationTemplate.getFromId());
		Assert.notNull(notificationTemplate.getToId());
		Assert.notNull(notificationTemplate.getNotificationType());

		dispatchNotification(
			notificationTemplate,
			userNotificationService.findNotificationTypeByCode(notificationTemplate.getNotificationType().getCode())
		);
	}

	private void logEmailRenderResultToKafka(
			final long toUserId,
			final String emailBodyTemplateKey,
			final String emailSubjectTemplateKey,
			final String languageCode,
			final TemplateStringResponse bodyRenderResponse,
            final TemplateStringResponse subjectRenderResponse) {
        final ImmutableMap map = ImmutableMap.<String, Object>builder()
                .put("toUserId", toUserId)
                .put("emailBodyTemplateKey", emailBodyTemplateKey)
                .put("emailSubjectTemplateKey", emailSubjectTemplateKey)
                .put("languageCode", languageCode)
                .put("body", bodyRenderResponse.getString())
                .put("subject", subjectRenderResponse.getString())
                .put("bodyRenderingSuccess", bodyRenderResponse.getStatus().isSuccess())
                .put("bodyRenderingMessage", bodyRenderResponse.getStatus().getMessage())
                .put("subjectRenderingSuccess", subjectRenderResponse.getStatus().isSuccess())
                .put("subjectRenderingMessage", subjectRenderResponse.getStatus().getMessage())
                .build();
        final KafkaData kafkaData = new KafkaData(map);
        kafkaClient.send("serviceRendering.log.email", kafkaData);
    }

	/**
	 * Return whether the template for notification notificationCode should be emailed to the user with id userId.
	 *
	 * @param notificationCode
	 * @param userId
	 * @return
	 */
	private boolean shouldSendEmail(final String notificationCode, final long userId) {
		return shouldSendEmail(notificationCode, userId, false);
	}

	/**
	 * Return whether the template for notification notificationCode should be emailed to the user with id userId.
	 *
	 * @param notificationCode
	 * @param userId
	 * @param userIsFollowerOfWork Whether the user identified by userId is a follower of the work this notification is about.
	 * @return
	 */
	private boolean shouldSendEmail(final String notificationCode, final long userId, final boolean userIsFollowerOfWork) {
		final NotificationType notificationType = userNotificationService.findNotificationTypeByCode(notificationCode);
		if (!userIsNotifiable(userId)) {
			return false;
		}

		final UserNotificationPreferencePojo pref = userNotificationPrefsService.findByUserAndNotificationType(
				userId, notificationCode);
		Assert.notNull(pref);

		final Optional<PersonaPreference> personaPref = userService.getPersonaPreference(userId);
		final boolean isDispatcher = personaPref.isPresent() ? personaPref.get().isDispatcher() : false;

		//these are accurate when a notificationType is passed in to this function. We have some logging
		//to determine if a notificationType is never passed in to where this was originally used:
		//NotificationDispatcherImpl.dispatchNotification(<two-arg version>)
		// Otherwise, use
			/*
					emailEnabledForNotification = toBooleanDefaultIfNull(notificationTemplate.getEmailEnabled(), false);
		      followEnabledForNotification = toBooleanDefaultIfNull(notificationTemplate.getFollowEnabled(), false);
			 */
		final boolean followEnabledForNotification = toBooleanDefaultIfNull(notificationType.getFollowFlag(), false);
		final boolean emailEnabledForNotification = toBooleanDefaultIfNull(notificationType.getEmailFlag(), false);


		if (userIsFollowerOfWork) {

			final boolean shouldSend = isDispatcher ? pref.getDispatchEmailFlag() && followEnabledForNotification :
					pref.getFollowFlag() && followEnabledForNotification;
			logger.info("shouldSendEmail userIsFollower shouldSend {} [isDispatcher {}  dispatchEmailPref {} "
					+ " followEnabled {}  followPref {}]",
					shouldSend, isDispatcher, pref.getDispatchEmailFlag(), followEnabledForNotification,
					pref.getFollowFlag());
			return shouldSend;
		}

		final boolean shouldSend = isDispatcher ? pref.getDispatchEmailFlag() && emailEnabledForNotification :
				pref.getEmailFlag() && emailEnabledForNotification;
		logger.info("shouldSendEmail userIsNotFollower shouldSend {} [isDispatcher {}  dispatchEmailPref {} "
				+ " emailEnabled {}  emailPref{}",
				shouldSend, isDispatcher, pref.getDispatchEmailFlag(), emailEnabledForNotification, pref.getEmailFlag());
		return shouldSend;

	}

    private boolean shouldSendSMS(final String notificationCode, final long userId) {
		final NotificationType notificationType = userNotificationService.findNotificationTypeByCode(notificationCode);
		final UserNotificationPreferencePojo pref = userNotificationPrefsService.findByUserAndNotificationType(
				userId, notificationCode);
		Assert.notNull(pref);

		final boolean smsEnabledForNotification = toBooleanDefaultIfNull(notificationType.getSmsFlag(), false);

		final boolean shouldSend = pref.getSmsFlag() && smsEnabledForNotification;
		logger.info("shouldSendSMS shouldSend {} [smsPref {}  smsEnabled {}]", shouldSend, pref.getSmsFlag(),
				smsEnabledForNotification);
		return shouldSend;
	}

	private boolean shouldSendPush(final String notificationCode, final long userId) {
		final NotificationType notificationType = userNotificationService.findNotificationTypeByCode(notificationCode);
		final UserNotificationPreferencePojo pref = userNotificationPrefsService.findByUserAndNotificationType(
				userId, notificationCode);
		Assert.notNull(pref);

		final boolean pushEnabledForNotification = toBooleanDefaultIfNull(notificationType.getPushFlag(), false);

		final boolean shouldSend = pref.getPushFlag() && pushEnabledForNotification;
		logger.info("shouldSendPush shouldSend {} [pushPref {}  pushEnabled {}]", shouldSend, pref.getPushFlag(),
				pushEnabledForNotification);
		return shouldSend;
	}

	/**
	 * Whether to send a user notification (a.k.a. "bullhorn").
	 * @param notificationCode
	 * @param userId
	 * @return
	 */
	private boolean shouldSendUserNotification(final String notificationCode, final long userId) {
		final NotificationType notificationType = userNotificationService.findNotificationTypeByCode(notificationCode);
		final UserNotificationPreferencePojo pref = userNotificationPrefsService.findByUserAndNotificationType(
				userId, notificationCode);
		Assert.notNull(pref);

		final boolean bullhornEnabledForNotification = toBooleanDefaultIfNull(notificationType.getBullhornFlag(), false);

		final boolean shouldSend = pref.getBullhornFlag() && bullhornEnabledForNotification;
		logger.info("shouldSendBullhorn shouldSend {} [bullhornPref {}  bullhornEnabled {}]", shouldSend,
				pref.getBullhornFlag(), bullhornEnabledForNotification);
		return shouldSend;
	}

	@Override
	public void dispatchNotification(NotificationTemplate notificationTemplate, NotificationType notificationType) {
		Assert.notNull(notificationTemplate);
		Assert.notNull(notificationTemplate.getFromId());
		final Long userId = notificationTemplate.getToId();
		Assert.notNull(userId);
		Assert.notNull(notificationTemplate.getNotificationType());

		boolean userNotificationEnabledForNotification;
		boolean smsEnabledForNotification;
		boolean emailEnabledForNotification;
		boolean followEnabledForNotification;
		boolean pushEnabledForNotification;

		metricRegistryFacade.meter("dispatched").mark();
		logger.info("starting dispatch process for notification destined to user id [{}] template [{}]", userId, notificationTemplate.getClass().getName());

		// use the DB notification settings whenever possible, otherwise use the bean defaults
		if (notificationType != null) {
			if (!isValidNotification(notificationTemplate)) {
				metricRegistryFacade.meter("invalidNotification").mark();
				logger.debug(String.format("Invalid notification: %s", notificationTemplate));
				return;
			}

			userNotificationEnabledForNotification = toBooleanDefaultIfNull(notificationType.getUserNotificationFlag(), false);
			smsEnabledForNotification = toBooleanDefaultIfNull(notificationType.getSmsFlag(), false);
			emailEnabledForNotification = toBooleanDefaultIfNull(notificationType.getEmailFlag(), false);
			followEnabledForNotification = toBooleanDefaultIfNull(notificationType.getFollowFlag(), false);
			pushEnabledForNotification = toBooleanDefaultIfNull(notificationType.getPushFlag(), false);
		} else {
			logger.debug(String.format("Did not find notification type settings for: %s, using default notification prefs", notificationTemplate.getNotificationType().getCode()));
			metricRegistryFacade.meter("missing_notification." + notificationTemplate.getNotificationType().getCode());
			userNotificationEnabledForNotification = toBooleanDefaultIfNull(notificationTemplate.getUserNotificationEnabled(), false);
			smsEnabledForNotification = toBooleanDefaultIfNull(notificationTemplate.getSMSEnabled(), false);
			emailEnabledForNotification = toBooleanDefaultIfNull(notificationTemplate.getEmailEnabled(), false);
			followEnabledForNotification = toBooleanDefaultIfNull(notificationTemplate.getFollowEnabled(), false);
			pushEnabledForNotification = toBooleanDefaultIfNull(notificationTemplate.getPushEnabled(), false);
		}
		logger.info("enabled notification channels: bullhorn [{}] sms [{}] email [{}] push [{}] follow [{}]",
			userNotificationEnabledForNotification, smsEnabledForNotification, emailEnabledForNotification,
				pushEnabledForNotification, followEnabledForNotification);

		if (notificationTemplate.getToId() != null) {
			// these templates already inject the right time zone from the work object
			if (notificationTemplate instanceof AbstractWorkNotificationTemplate
				|| notificationTemplate instanceof WorkResourceNotificationTemplate) {

				User user = userService.getUser(userId);
				if (StringUtils.isNotBlank(user.getSecondaryEmail())) {
					notificationTemplate.setCcEmail(user.getSecondaryEmail());
				}

			} else {
				ProfileDTO profileDTO = profileService.findProfileDTO(userId);
				if (profileDTO != null) {
					notificationTemplate.setTimeZoneId(profileDTO.getTimeZoneCode());
				}
			}
		}

		UserNotificationPreferencePojo pref = userNotificationPrefsService.findByUserAndNotificationType(
				userId, notificationTemplate.getNotificationType().getCode()
		);
		logger.info("notification user preferences: bullhorn [{}] sms [{}] email [{}] push [{}] follow [{}]",
			pref.getBullhornFlag(), pref.getSmsFlag(), pref.getEmailFlag(), pref.getPushFlag(), pref.getFollowFlag());

		Assert.notNull(pref);

		Optional<PersonaPreference> personaPref = userService.getPersonaPreference(userId);
		boolean isDispatcher = personaPref.isPresent() && personaPref.get().isDispatcher();

		logger.info("notification has email template? [{}]", notificationTemplate.getEmailTemplate() != null);
		if (notificationTemplate.getEmailTemplate() != null) {
			boolean canSendEmail;
			if (notificationTemplate instanceof AbstractWorkNotificationTemplate) {
				AbstractWorkNotificationTemplate abstractWorkNotificationTemplate = (AbstractWorkNotificationTemplate)notificationTemplate;

				if (abstractWorkNotificationTemplate.getEncryptedWorkFollowId() != null) {
					canSendEmail = isDispatcher ? pref.getDispatchEmailFlag() && followEnabledForNotification :
						pref.getFollowFlag() && followEnabledForNotification;
					logger.info("[1] canSendEmail {}  isDispatcher {}  dispatchEmailPref {} "
							+ " followEnabledForNotification {}  followPref {}", canSendEmail, isDispatcher,
							pref.getDispatchEmailFlag(), followEnabledForNotification, pref.getFollowFlag());
				} else {
					canSendEmail = isDispatcher ? pref.getDispatchEmailFlag() && emailEnabledForNotification :
						pref.getEmailFlag() && emailEnabledForNotification;
					logger.info("[2] canSendEmail {}  isDispatcher {}  dispatchEmailPref {} "
							+ " getEmailFlag {} emailPref {}", canSendEmail, isDispatcher, pref.getDispatchEmailFlag(),
							emailEnabledForNotification);
				}
			} else {
				canSendEmail = isDispatcher ? pref.getDispatchEmailFlag() && emailEnabledForNotification :
					pref.getEmailFlag() && emailEnabledForNotification;
				logger.info("[3] canSendEmail {}  isDispatcher {}  dispatchEmailPref {} "
						+ " emailEnabled {}  emailPref {}", canSendEmail, isDispatcher, pref.getDispatchEmailFlag(),
						emailEnabledForNotification, pref.getEmailFlag());
			}

			if (canSendEmail) {
				try {
					logger.info("dispatching email");
					dispatchEmail(notificationTemplate.getEmailTemplate());
				} catch (Exception x) {
					logger.error("Exception while sending email ", x);
				}
			} else {
				logger.info("not dispatching email");
			}
		}

		logger.info("sms dispatch evaluation [hasTemplate={}] [userPreference={}] [channelEnabled={}]",
			notificationTemplate.getSMSTemplate() != null, pref.getSmsFlag(), smsEnabledForNotification);
		if (notificationTemplate.getSMSTemplate() != null && pref.getSmsFlag() && smsEnabledForNotification) {
			try {
				logger.info("dispatching sms");
				dispatchSMS(notificationTemplate.getSMSTemplate());
			} catch (Exception e) {
				logger.error("Exception while sending sms ", e);
			}
		} else {
			logger.info("not dispatching sms");
		}

		logger.info("push dispatch evaluation [hasTemplate={}] [userPreference={}] [channelEnabled={}]",
			notificationTemplate.getPushTemplate() != null, pref.getPushFlag(), pushEnabledForNotification);
		if (notificationTemplate.getPushEnabled() != null && pref.getPushFlag() && pushEnabledForNotification) {
			try {
				logger.info("dispatching push");
				dispatchPush(notificationTemplate.getPushTemplate());
			} catch (Exception e) {
				logger.error("Exception while sending push", e);
			}
		} else {
			logger.info("not dispatching push");
		}

		boolean bullHornFlag = isDispatcher ? pref.getDispatchBullhornFlag() : pref.getBullhornFlag();
		logger.info("bullhorn dispatch evaluation [hasTemplate={}] [isDispatcher={}] [dispatcherUserPreference={}] [userPreference={}] [channelEnabled={}]",
				notificationTemplate.getUserNotificationTemplate() != null, isDispatcher, bullHornFlag,
				pref.getBullhornFlag(), pref.getDispatchBullhornFlag());
		if (notificationTemplate.getUserNotificationTemplate() != null && bullHornFlag && userNotificationEnabledForNotification) {
			try {
				logger.info("dispatching bullhorn");
				dispatchUserNotification(notificationTemplate.getUserNotificationTemplate());
			} catch (Exception e) {
				logger.error("Exception storing the user notification ", e);
			}
		} else {
			logger.info("not dispatching bullhorn");
		}
	}

	@Override
	public void dispatchNotifications(List<? extends NotificationTemplate> templates) {
		for (NotificationTemplate template: templates) {
			try {
				dispatchNotification(template);
			} catch (Exception e) {
				logger.error("Error dispatching notification " + template, e);
			}
		}
	}

	@Override
	public EmailNotifyResponse dispatchEmail(final EmailTemplate template) {
		String text = templateService.render(template);
		String subject = (StringUtils.isNotBlank(template.getSubject())) ? template.getSubject() : templateService.renderSubject(template);

		if (template.hasPdfTemplate()) {
			String pdfContent = templateService.renderPDFTemplate(template.getPdfTemplate());
			String outputFile = Constants.TEMPORARY_FILE_DIRECTORY + template.getPdfTemplate().getOutputFileName();
			try {
				PDFUtilities.createFromHtml(pdfContent, outputFile);

				FileDTO attachment = new FileDTO();
				attachment.setName(template.getPdfTemplate().getOutputFileName());
				attachment.setMimeType(template.getPdfTemplate().getMimeType());
				attachment.setSourceFilePath(outputFile);
				template.addAttachment(attachment);
			} catch (Exception e) {
				notificationErrorReporter.sendNotificationErrorToKafka(template, NotificationErrorReporter.ERROR_EXCEPTION_THROWN);

				logger.error("Error rendering PDFTemplate " + e);
			}
		}

		EMailDTO emailDTO = new EMailDTO(template, text, subject);

		if (template instanceof AbstractWorkEmailTemplate) {
			emailDTO.setWorkId(((AbstractWorkEmailTemplate) template).getWorkId());
		}

		final EmailNotifyResponse emailResponse = emailService.sendEmail(emailDTO);
		metricRegistryFacade.meter("dispatchedEmail").mark();

		return emailResponse;
	}

	/**
	 * Dispatch an email using the notification service to render and send the email.
	 *
	 * @param toUserId                the userId of the user we're sending this to
	 * @param fromUserId              the userId of the user sending this email
	 * @param emailBodyTemplateKey    the template key for the body
	 * @param emailSubjectTemplateKey the template key for the subject
	 * @param languageCode            the code of the language to send the email in
	 * @param replacements            the replacements for the template
	 * @param notificationCode        the notification code for the database, like "manage.work.invited"
	 */
	@Override
	public EmailNotifyResponse dispatchEmail(
			final long toUserId,
			final long fromUserId,
			final String emailBodyTemplateKey,
			final String emailSubjectTemplateKey,
			final String languageCode,
			final Map<String, Object> replacements,
			final String notificationCode) {
		if (!shouldSendEmail(notificationCode, toUserId)) {
			return new EmailNotifyResponse(EmailNotifyResponse.Status.FAIL);
		}

		final RequestContext requestContext = webRequestContextProvider.getRequestContext();

		final TemplateRenderRequest bodyRenderRequest = new TemplateRenderRequest(emailBodyTemplateKey, languageCode, replacements);
		final TemplateRenderRequest subjectRenderRequest = new TemplateRenderRequest(emailSubjectTemplateKey, languageCode, replacements);

		return Observable.zip(
				notificationClient.renderTemplate(bodyRenderRequest, requestContext),
				notificationClient.renderTemplate(subjectRenderRequest, requestContext),
				new Func2<TemplateStringResponse, TemplateStringResponse, EmailNotifyResponse>() {
					@Override
					public EmailNotifyResponse call(final TemplateStringResponse bodyRenderResponse, final TemplateStringResponse subjectRenderResponse) {
						if (bodyRenderResponse.getStatus().isSuccess() && subjectRenderResponse.getStatus().isSuccess()) {
							final User toUser = userService.findUserById(toUserId);
							final User fromUser = userService.getUser(fromUserId);
							return emailService.sendEmail(
									toUser.getFullName(),
									toUser.getEmail(),
									fromUser.getFullName(),
									fromUser.getEmail(),
									fromUser.getReplyToEmail(),
									subjectRenderResponse.getString(),
									bodyRenderResponse.getString());
						}

						return new EmailNotifyResponse(EmailNotifyResponse.Status.FAIL);
					}
				})
				.doOnError(new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						String errorString = String.format(
								"Error rendering template %s",
								emailBodyTemplateKey);
						logger.error(errorString, throwable);
					}
				})
				.toBlocking()
				.single();
	}

	private boolean dispatchUserNotification(
			final boolean isSticky,
			final String text,
			final Long toUserId,
			final Long fromUserId,
			final String notificationTypeCode) {
		if(shouldSendUserNotification(notificationTypeCode, toUserId)) {
			return idGenerator.next()
					.first()
					.map(new Func1<String, Boolean>() {
						@Override
						public Boolean call(final String uuid) {
							return userNotificationService.sendUserNotification(
									uuid,
									isSticky,
									text,
									toUserId,
									fromUserId,
									notificationTypeCode);
						}
					})
					.onErrorReturn(new Func1<Throwable, Boolean>() {
						@Override
						public Boolean call(final Throwable throwable) {
							return false;
						}
					})
					.defaultIfEmpty(false)
					.toBlocking()
					.first();
		}

		return true;
	}

	@Override
	public boolean dispatchUserNotification(
			final boolean isSticky,
			final String userNotificationTemplateKey,
			final Long toUserId,
			final Long fromUserId,
			final String notificationTypeCode,
			final Map<String, Object> replacements,
			final String languageCode) {
		metricRegistryFacade.meter("dispatchedBullhorn").mark();
		final TemplateRenderRequest userNotificationRenderRequest =
				new TemplateRenderRequest(userNotificationTemplateKey, languageCode, replacements);
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		return notificationClient.renderTemplate(userNotificationRenderRequest, requestContext)
				.first()
				.map(new Func1<TemplateStringResponse, Boolean>() {
					@Override
					public Boolean call(final TemplateStringResponse templateStringResponse) {
						if(templateStringResponse.getStatus().isSuccess()) {
							return dispatchUserNotification(
									isSticky,
									templateStringResponse.getString(),
									toUserId,
									fromUserId,
									notificationTypeCode);
						}

						return true;
					}
				})
				.onErrorReturn(new Func1<Throwable, Boolean>() {
					@Override
					public Boolean call(final Throwable throwable) {
						return false;
					}
				})
				.defaultIfEmpty(false)
				.toBlocking()
				.first();
	}

	@Override
	public boolean dispatchUserNotification(final UserNotificationTemplate template) throws Exception {
		String text = templateService.render(template);

		if (StringUtils.isEmpty(text)) {
			notificationErrorReporter.sendNotificationErrorToKafka(template, NotificationErrorReporter.ERROR_EMPTY_TEXT);
			return false;
		}

		return dispatchUserNotification(
				template.isSticky(),
				text,
				template.getToId(),
				template.getFromId(),
				template.getNotificationType().getCode());
	}

	@Override
	public SmsNotifyResponse dispatchSMS(final SMSTemplate smsTemplate) throws Exception {
		final Long toUserId = smsTemplate.getToId();
		final String text = templateService.render(smsTemplate);

		return dispatchSMS(toUserId, text, smsTemplate.getNotificationType().getCode());
	}

	@Override
	public SmsNotifyResponse dispatchSMS(
			final Long toUserId,
			final String smsTemplateKey,
			final String languageCode,
			final Map<String, Object> replacements,
			final String notificationTypeCode) {
		final TemplateRenderRequest renderSMSRequest =
				new TemplateRenderRequest(smsTemplateKey, languageCode, replacements);
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		return notificationClient.renderTemplate(renderSMSRequest, requestContext)
				.map(new Func1<TemplateStringResponse, SmsNotifyResponse>() {
					@Override
					public SmsNotifyResponse call(final TemplateStringResponse renderSMSResponse) {
						if(renderSMSResponse.getStatus().isSuccess()) {
							return dispatchSMS(toUserId, renderSMSResponse.getString(), notificationTypeCode);
						} else {
							return new SmsNotifyResponse("FAIL");
						}
					}
				})
				.defaultIfEmpty(new SmsNotifyResponse("FAIL"))
				.onErrorReturn(new Func1<Throwable, SmsNotifyResponse>() {
					@Override
					public SmsNotifyResponse call(final Throwable throwable) {
						return new SmsNotifyResponse("FAIL");
					}
				})
				.toBlocking()
				.single();
	}

	private SmsNotifyResponse dispatchSMS(final Long toUserId, final String text, final String notificationTypeCode) {
		if (!userService.isAvailableForNotification(toUserId)) {
			logger.info("User " + toUserId + " is not available to receive SMS at this time");
			return new SmsNotifyResponse("FAIL");
		}

		if(shouldSendSMS(notificationTypeCode, toUserId)) {
			final User toUser = userService.findUserById(toUserId);
			final String phoneNumber = toUser.getProfile().getSmsPhone();
			final SmsNotifyResponse response = smsService.sendSMS(phoneNumber, text);
			metricRegistryFacade.meter("dispatchedSMS").mark();
			return response;
		}

		return new SmsNotifyResponse("OK");
	}

	@Override
	public List<PushResponseAndDeviceType> dispatchPush(final PushTemplate pushTemplate) throws Exception {
		String text = templateService.render(pushTemplate);

		if (StringUtils.isEmpty(text)) {
			notificationErrorReporter.sendNotificationErrorToKafka(pushTemplate, NotificationErrorReporter.ERROR_EMPTY_TEXT);
			return Lists.newArrayList();
		}

		return dispatchPush(pushTemplate.getToId(), text, pushTemplate.getNotificationType().getCode());
	}

	private List<PushResponseAndDeviceType> dispatchPush(final Long toUserId, final String text, final String notificationTypeCode) {
		logger.debug("[Push] start dispatchPush");

		if(!shouldSendPush(notificationTypeCode, toUserId)) {
			return Lists.newArrayList();
		}

		final Set<UserDeviceAssociation> deviceAssociations = Sets.newHashSet(userService.findAllUserDevicesByUserId(toUserId));
		final List<PushResponseAndDeviceType> returnValues = Lists.newArrayListWithCapacity(deviceAssociations.size());
		for (UserDeviceAssociation association : deviceAssociations) {
			String deviceType = association.getDeviceType();
			final PushResponse pushResponse = pushService
					.sendPush(toUserId, text, association.getDeviceUid(), deviceType);

            returnValues.add(new PushResponseAndDeviceType(pushResponse, deviceType));
            metricRegistryFacade.meter("dispatchedPush").mark();
        }

        return returnValues;
	}

	@Override
	public List<PushResponseAndDeviceType> dispatchPush(
			final Long toUserId,
			final String pushTemplateKey,
			final String languageCode,
			final Map<String, Object> replacements,
			final String notificationTypeCode)  {
		final TemplateRenderRequest renderPushRequest =
				new TemplateRenderRequest(pushTemplateKey, languageCode, replacements);
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
        return notificationClient.renderTemplate(renderPushRequest, requestContext)
                .first()
                .map(new Func1<TemplateStringResponse, List<PushResponseAndDeviceType>>() {
                    @Override
                    public List<PushResponseAndDeviceType> call(final TemplateStringResponse renderPushResponse) {
                        if (renderPushResponse.getStatus().isSuccess()) {
                            return dispatchPush(toUserId, renderPushResponse.getString(), notificationTypeCode);
                        } else {
                            return Lists.newArrayList();
                        }
                    }
                })
                .onErrorReturn(new Func1<Throwable, List<PushResponseAndDeviceType>>() {
                    @Override
                    public List<PushResponseAndDeviceType> call(final Throwable throwable) {
                        return Lists.newArrayList();
                    }
                })
                .defaultIfEmpty(Lists.<PushResponseAndDeviceType>newArrayList())
                .toBlocking()
                .single();
    }

	private boolean isValidNotification(NotificationTemplate notificationTemplate) {
		Assert.notNull(notificationTemplate);

		/*
		 * Don't send notifications to deactivated users or in the
		 * blacklisted_mail
		 */
		final Long toId = notificationTemplate.getToId();
		if (toId == null) {
			metricRegistryFacade.meter("notification.oldTemplate.missingToId").mark();
		} else if(!userIsNotifiable(notificationTemplate.getToId())) {
			return false;
		}

		if (notificationTemplate.getEmailTemplate() != null && StringUtils.isNotBlank(notificationTemplate.getEmailTemplate().getToEmail())) {
			if (registrationService.isBlacklisted(notificationTemplate.getEmailTemplate().getToEmail())) {
				logger.info("email is blacklisted: " + notificationTemplate.getEmailTemplate().getToEmail());
				return false;
			}
		}

		return notificationValidator.validateNotification(notificationTemplate);
	}

	private boolean userIsNotifiable(final Long userId) {
		final User user = userService.getUser(userId);
		if (user == null) {
			logger.info(String.format("User id %s isn't found!", userId));
			return false;
		}

		if (authenticationService.isSuspended(user) || authenticationService.isDeactivated(user)) {
			logger.info(String.format("User id %s status code: %s", userId, authenticationService.getUserStatus(user).getCode()));
			return false;
		}

		if (registrationService.isBlacklisted(user.getEmail())) {
			logger.info(String.format("User id %s email is blacklisted: ", userId, user.getEmail()));
			return false;
		}
		logger.info("User id [%s] is notifiable", userId);
		return true;
	}
}
