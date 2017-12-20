package com.workmarket.common.template;

import com.workmarket.common.template.push.NotificationPushTemplate;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.common.template.email.NotificationEmailTemplate;
import com.workmarket.common.template.notification.NotificationUserNotificationTemplate;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.common.template.sms.NotificationSMSTemplate;
import com.workmarket.common.template.sms.SMSTemplate;
import com.workmarket.common.template.voice.NotificationVoiceTemplate;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.service.web.AbstractWebRequestContextAware;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * This is a base class for notifications. It provides generic approach to notifications (E-Mail, SMS, etc.)
 * All you have to do it derive form this class and create templates for all transports (E-mail, SMS, etc.)
 * and dispatch it to NotificationService
 *
 * This class should only be used to notify users who are on the system
 */
public class NotificationTemplate extends AbstractWebRequestContextAware implements Serializable {

	private static final long serialVersionUID = 288072205644367273L;

	// common
	private Long fromId;
	private Long toId;
	private NotificationType notificationType;
	private ReplyToType replyToType = ReplyToType.TRANSACTIONAL;
	protected String timeZoneId = Constants.DEFAULT_TIMEZONE;
	private Long onBehalfOfId;

	// Email
	@Deprecated
	// Please, use Velocity subject template
	protected String emailSubject;
	private String[] ccEmails;

	// SMS
	private Long providerId;
	private String toNumber;

	// For User Notification on system
	private boolean sticky;

	// Sending controls
	// Use as filters if necessary to explicitly exclude the sending of a particular notification.
	private Boolean emailEnabled = Boolean.TRUE;
	private Boolean smsEnabled = Boolean.TRUE;
	private Boolean userNotificationEnabled = Boolean.TRUE;
	private Boolean voiceEnabled = Boolean.TRUE;
	private Boolean followEnabled = Boolean.TRUE;
	private Boolean pushEnabled = Boolean.TRUE;

	private PDFTemplate pdfTemplate;
	private String creatorFullName;
	private boolean isCompanyNotification;

    public NotificationTemplate() {}

	/**
	 * Constructor that should be used for E-mail only notifications
	 *
	 * @param fromId user sending an email
	 * @param toId user that should receive the E-mail
	 * @param notificationType type of notification
	 * @param replyToType reply type, what type of reply to E-mails should be used
	 */
	public NotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType) {
		Assert.notNull(fromId);
		Assert.notNull(toId);
		Assert.notNull(notificationType);

		this.notificationType = notificationType;
		this.fromId = fromId;
		this.toId = toId;
		this.replyToType = replyToType;
	}

	public NotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, String creatorFullName) {
		this(fromId, toId, notificationType, replyToType);
		this.creatorFullName = creatorFullName;
	}

	/**
	 * Returns E-mail template associated with this notification, this should be rarely overwritten
	 *
	 * @return EmailTemplate
	 */
	public NotificationEmailTemplate getEmailTemplate() {
		return new NotificationEmailTemplate(fromId, toId, onBehalfOfId, emailSubject, notificationType, this, this.getReplyToType(), this.getCcEmails(), pdfTemplate);
	}

	/**
	 * Returns SMS template associated with this notification, this should be rarely overwritten
	 *
	 * @return SMSTemplate
	 */
	public SMSTemplate getSMSTemplate() {
		return new NotificationSMSTemplate(providerId, toNumber, fromId, toId, notificationType, this);
	}

	/**
	 * Returns user notification template associated with this notification, this should be rarely overwritten.
	 *
	 * @return UserNotificationTemplate
	 */
	public NotificationUserNotificationTemplate getUserNotificationTemplate() {
		return new NotificationUserNotificationTemplate(toId, fromId, notificationType, sticky, this);
	}


	/**
	 * Returns voice notification template associated with this notification.
	 *
	 * @return VoiceTemplate
	 */
	public NotificationVoiceTemplate getVoiceTemplate() {
		return new NotificationVoiceTemplate(fromId, toId, notificationType, this);
	}

	public NotificationPushTemplate getPushTemplate() {
		return new NotificationPushTemplate(fromId, toId, notificationType, this);
	}

	/**
	 * Returns a model for all templates
	 *
	 * @return model
	 */
	public NotificationTemplate getModel() {
		return this;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public Long getFromId() {
		return fromId;
	}

	public void setFromId(Long fromId) {
		this.fromId = fromId;
	}

	public Long getToId() {
		return toId;
	}

	public void setToId(Long toId) {
		this.toId = toId;
	}

	public String getCreatorFullName() {
		return creatorFullName;
	}

	public void setCreatorFullName(String creatorFullName) {
		this.creatorFullName = creatorFullName;
	}

	@Deprecated
	public String getEmailSubject() {
		return emailSubject;
	}

	@Deprecated
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public ReplyToType getReplyToType() {
		return replyToType;
	}

	public void setReplyToType(ReplyToType replyToType) {
		this.replyToType = replyToType;
	}

	// Delivery-mechanism exclusions

	public void setEnabledDeliveryMethods(Boolean emailEnabled, Boolean smsEnabled, Boolean userNotificationEnabled, Boolean voiceEnabled, Boolean followEnabled) {
		this.emailEnabled = emailEnabled;
		this.smsEnabled = smsEnabled;
		this.userNotificationEnabled = userNotificationEnabled;
		this.voiceEnabled = voiceEnabled;
		this.followEnabled = followEnabled;
	}

	public Boolean getEmailEnabled() {
		return this.emailEnabled;
	}
	public void setEmailEnabled(Boolean emailEnabled) {
		this.emailEnabled = emailEnabled;
	}

	public Boolean getSMSEnabled() {
		return this.smsEnabled;
	}
	public void setSMSEnabled(Boolean smsEnabled) {
		this.smsEnabled = smsEnabled;
	}

	public Boolean getUserNotificationEnabled() {
		return this.userNotificationEnabled;
	}
	public void setUserNotificationEnabled(Boolean userNotificationEnabled) {
		this.userNotificationEnabled = userNotificationEnabled;
	}

	public Boolean getVoiceEnabled() {
		return this.voiceEnabled;
	}
	public void setVoiceEnabled(Boolean voiceEnabled) {
		this.voiceEnabled = voiceEnabled;
	}

	public Boolean getFollowEnabled() {
		return followEnabled;
	}

	public void setFollowEnabled(Boolean followEnabled) {
		this.followEnabled = followEnabled;
	}

	public Boolean getPushEnabled() {
		return pushEnabled;
	}

	public void setPushEnabled(Boolean pushEnabled) {
		this.pushEnabled = pushEnabled;
	}

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public boolean isSticky() {
		return sticky;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public TimeZone getTimeZone() {
		return StringUtils.isEmpty(timeZoneId) ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZoneId);
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmails = new String[] {ccEmail};
	}

	public void setCcEmails(String[] ccEmails) {
		if (ccEmails == null) {
			this.ccEmails = new String[0];
		} else {
			this.ccEmails = Arrays.copyOf(ccEmails, ccEmails.length);
		}
	}

	public String[] getCcEmails() {
		return ccEmails;
	}

	public PDFTemplate getPdfTemplate() {
		return pdfTemplate;
	}

	public void setPdfTemplate(PDFTemplate pdfTemplate) {
		this.pdfTemplate = pdfTemplate;
	}

	public Long getOnBehalfOfId() {
		return onBehalfOfId;
	}

	public void setOnBehalfOfId(Long onBehalfOfId) {
		this.onBehalfOfId = onBehalfOfId;
	}

	public boolean isCompanyNotification() {
		return isCompanyNotification;
	}

	public void setCompanyNotification(boolean isCompanyNotification) {
		this.isCompanyNotification = isCompanyNotification;
	}
}
