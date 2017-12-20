package com.workmarket.domains.model.notification;

import com.workmarket.domains.model.LookupEntity;
import javax.persistence.*;

@Entity(name="notificationType")
@Table(name="notification_type")
@AttributeOverrides({
	@AttributeOverride(name="code", column=@Column(length=50)),
	@AttributeOverride(name="description", column=@Column(length=100))
})
public class NotificationType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;

	// Notifications sent to user with respect to groups
	public static final String GROUP_APPROVED = "find.group.approved";
	public static final String GROUP_DECLINED = "find.group.declined";
	public static final String GROUP_APPLY = "find.group.apply";
	public static final String GROUP_PRIVATE_APPLY = "find.group.private.apply";
	public static final String GROUP_INVITED = "find.group.invited";
	public static final String GROUP_MESSAGE = "find.group.message";
	public static final String GROUP_INVITED_REQUIREMENTS_MODIFIED = "find.group.requirements";
	public static final String GROUP_INVITED_REQUIREMENTS_EXPIRED = "find.group.requirements.expired";
	public static final String GROUP_INVITED_PROFILE_MODIFIED = "find.group.profileModified";
	public static final String GROUP_INVITED_PROFILE_MODIFIED_GROUP_OWNER = "find.group.profileModified.groupOwner";
	public static final String GROUP_INACTIVE_DEACTIVATED = "find.group.inactive.deactivated";

	// Notifications sent to buyers or followers with respect to work
	public static final String WORK_ACCEPTED = "manage.work.accepted";
	public static final String WORK_DECLINED = "manage.work.declined";
	public static final String WORK_COMPLETED = "manage.work.completed";
	public static final String WORK_COMPLETED_BY_BUYER = "manage.work.completed_by_buyer";
	public static final String WORK_INCOMPLETE = "manage.work.incomplete";
	public static final String WORK_RESOURCE_NOT_CONFIRMED = "manage.work.resource.notconfirmed";
	public static final String WORK_RESOURCE_CONFIRMED = "manage.work.resource.confirmed";
	public static final String WORK_RESOURCE_CHECKEDIN = "manage.work.resource.checkedin";
	public static final String WORK_RESOURCE_NOT_CHECKEDIN = "manage.work.resource.notcheckedin";
	public static final String WORK_RESOURCE_CHECKEDOUT = "manage.work.resource.checkedout";
	public static final String WORK_RESOURCE_CANCELLED = "manage.work.resource.cancelled";
	public static final String WORK_CREATED = "manage.work.created";
	public static final String WORK_QUESTION = "manage.work.question";
	public static final String WORK_REPRICED = "manage.work.repriced";
	public static final String WORK_GENERIC = "manage.work.generic";
	public static final String WORK_UPDATED = "manage.work.updated";
	public static final String WORK_NEGOTIATION = "manage.work.negotiation";
	public static final String WORK_BONUS_REQUESTED = "manage.work.bonus.requested";
	public static final String WORK_BUDGET_REQUESTED = "manage.work.budget.requested";
	public static final String WORK_EXPENSE_REQUESTED = "manage.work.expense.requested";
	public static final String WORK_RESCHEDULE_REQUESTED = "manage.work.reschedule.requested";
	public static final String WORK_RESCHEDULE_DECISION = "manage.work.reschedule.decision";
	public static final String WORK_ATTACHMENT_ADDED = "manage.work.attachment";
	public static final String WORK_NOTE_ADDED = "manage.work.note";
	public static final String WORK_NOTE_ADDED_BY_EMPLOYEE = "manage.work.note.by.employee";
	public static final String WORK_MESSAGE_MENTION = "manage.work.mention";
	public static final String WORK_APPOINTMENT_SET = "manage.work.appointment";
	public static final String WORK_SUBSTATUS = "manage.work.substatus";
	public static final String WORK_SUBSTATUS_ALERT = "manage.work.exception";
	public static final String WORK_SUBSTATUS_NOT_CONFIRMED = "manage.work.substatus.notconfirmed";
	public static final String WORK_REMINDER_TO_COMPLETE = "manage.work.reminder_to_complete";
	public static final String WORK_DELEGATED = "manage.work.delegated";
	public static final String WORK_RATED = "rate.rated";
	public static final String WORK_SURVEY_COMPLETED = "tools.assessment.completed.invitee";
	public static final String WORK_DELIVERABLE_REQUIREMENTS_FULFILLED = "manage.work.deliverable.requirements.fulfilled";

	// Notifications sent to resources with respect to work
	public static final String RESOURCE_WORK_NOTE_ADDED = "resource.work.note";
	public static final String RESOURCE_WORK_ATTACHMENT_ADDED = "resource.work.attachment";
	public static final String RESOURCE_WORK_NEGOTIATION_DECISION = "resource.work.negotiation.decision";
	public static final String RESOURCE_WORK_BONUS_DECISION = "resource.work.bonus.decision";
	public static final String RESOURCE_WORK_BONUS_ADDED = "resource.work.bonus.added";
	public static final String RESOURCE_WORK_BUDGET_DECISION = "resource.work.budget.decision";
	public static final String RESOURCE_WORK_BUDGET_ADDED = "resource.work.budget.added";
	public static final String RESOURCE_WORK_EXPENSE_DECISION = "resource.work.expense.decision";
	public static final String RESOURCE_WORK_EXPENSE_ADDED = "resource.work.expense.added";
	public static final String RESOURCE_WORK_RESCHEDULE_REQUESTED = "resource.work.reschedule.requested";
	public static final String RESOURCE_WORK_RESCHEDULE_DECISION = "resource.work.reschedule.decision";
	public static final String RESOURCE_WORK_RATED = "resource.work.rated";
	public static final String RESOURCE_WORK_QUESTION = "resource.work.question";
	public static final String RESOURCE_WORK_INVITED = "manage.work.invited";
	public static final String RESOURCE_WORK_UPDATED = "resource.work.updated";
	public static final String RESOURCE_WORK_ACCEPTED_DETAILS = "manage.work.acceptedDetails";
	public static final String RESOURCE_WORK_CHECKIN = "manage.work.checkin";
	public static final String RESOURCE_WORK_CONFIRM = "manage.work.resource.confirm";
	public static final String RESOURCE_WORK_CANCELLED = "manage.work.cancelled";
	public static final String RESOURCE_WORK_DELEGATED = "resource.work.delegated";
	public static final String RESOURCE_WORK_CLOSED = "manage.work.closed";
	public static final String RESOURCE_WORK_SUBSTATUS = "resource.work.substatus";
	public static final String RESOURCE_WORK_SUBSTATUS_ALERT = "resource.work.substatus.alert";
	public static final String RESOURCE_WORK_APPOINTMENT_SET = "resource.work.appointment";
	public static final String RESOURCE_WORK_ON_BEHALF_OF = "manage.work.onbehalfof";
	public static final String RESOURCE_WORK_NOT_AVAILABLE = "manage.work.not_available";
	public static final String RESOURCE_WORK_STOP_PAYMENT = "manage.work.stop_payment";
	public static final String RESOURCE_WORK_DELIVERABLE_REJECTED = "resource.work.deliverable.rejected";
	public static final String RESOURCE_WORK_DELIVERABLE_LATE = "resource.work.deliverable.late";
	public static final String RESOURCE_WORK_DELIVERABLE_REMINDER = "resource.work.deliverable.reminder";

	public static final String PROFILE_APPROVED = "profile.approved";
	public static final String PROFILE_COMPLETENESS = "profile.completeness";
	
	public static final String WORKMARKET_MESSAGE = "workmarket.message";
	public static final String WORKMARKET_MARKETING = "workmarket.marketing";
	public static final String WORKMARKET_NEWSLETTER = "workmarket.newsletter";
	public static final String WORKMARKET_PROMOTIONS = "workmarket.promotions";
	
	public static final String LANE_23_CREATED = "find.lane23.created";		
	
	public static final String ASSESSMENT_INVITATION = "tools.assessment.invite";
	public static final String ASSESSMENT_ATTEMPT_GRADE_PENDING = "tools.assessment.gradepending";
	public static final String ASSESSMENT_ATTEMPT_GRADED = "tools.assessment.graded";
	public static final String ASSESSMENT_ATTEMPT_COMPLETED = "tools.assessment.completed";
	public static final String ASSESSMENT_ATTEMPT_UNGRADED = "tools.assessment.completed.ungraded";
	public static final String ASSESSMENT_INACTIVE = "tools.assessment.inactive";

	public static final String MONEY_DEPOSITED = "money.deposited";
	public static final String MONEY_WITHDRAWN = "money.withdrawn";
	public static final String MONEY_LOW_BALANCE = "money.lowbalance";
	public static final String MONEY_CREDIT_CARD_RECEIPT = "money.cc.receipt";
	public static final String MONEY_PROCESSED = "money.processed";

	// Asynchronous Bulk Uploader
	public static final String BULK_UPLOAD_COMPLETE = "bulk.upload.complete";
	public static final String BULK_UPLOAD_FAILED = "bulk.upload.failed";

	// Asynchronous Bulk Uploader
	public static final String BULK_USER_UPLOAD_COMPLETE = "bulk.user.upload.complete";
	public static final String BULK_USER_UPLOAD_FAILED = "bulk.user.upload.failed";

	public static final String BUNDLE_WORK_ACCEPT_FAILED = "bundle.work.accept.failed";

	public static final String MISC = "misc";

	public static final String SYSTEM_USER_NOTIFICATION = "system.notification";

	public static final String DAILY_BALANCE = "pay.balancedaily";
	public static final String WEEKLY_BALANCE = "pay.balanceweekly";
	public static final String MONTHLY_BALANCE = "pay.balancemonthly";

	public static final String IVR_WORK = "ivr.work";
	public static final String IVR_MOBILE = "ivr.mobile";
	public static final String IVR_SMS = "ivr.sms";
	
	public static final String PAYMENT_TERMS_DUE = "paymentterms.due";
	public static final String INVOICE_DUE_REMINDER_MY_COMPANY = "pay.invoice.due.mycompany";
	public static final String INVOICE_DUE_REMINDER_MY_ASSIGNMENTS = "pay.invoice.due.myassignments";
	public static final String STATEMENT_REMINDER = "pay.statement";
	
	public static final String ADD_FUNDS_WIRE = "add.funds.wired";

	public static final String ASSET_BUNDLE_AVAILABLE = "asset.bundle.available";

	public static final String BLOCK_CLIENT = "block.client";

	public static final String SURVEY_ATTEMPT_COMPLETED = "tools.survey.completed";
	public static final String SURVEY_INVITATION = "tools.survey.invite";

	public static final String TAX_VERIFICATION = "tax.verification";
	public static final String TAX_REPORT_CREATED_WM_INTERNAL_ONLY = "tax.report.created";
	public static final String TAX_REPORT_AVAILABLE = "tax.report.available";


	//Payment Notifications new
	public static final String LOCKED_INVOICE_DUE_REMINDER_MY_ACCOUNT = "locked.invoice.due.myaccount";
	public static final String INVOICE_DUE_REMINDER_MY_ACCOUNT = "pay.invoice.due.myaccount";
	public static final String SUBSCRIPTION_REMINDER = "pay.subscription";
	public static final String SUBSCRIPTION_THROUGHPUT_REACHED = "pay.subscription.throughput";

	public static final String INVOICE_CREATED_ON_ASSIGNMENT = "invoice.created.radio.only";
	public static final String INVOICE_DUE_3_DAYS = "invoice.due.3.days.radio.only";
	public static final String INVOICE_DUE_24_HOURS = "invoice.due.24.hours.radio.only";
	public static final String MY_INVOICES_DUE_3_DAYS= "invoice.due.3.days.mine";
	public static final String MY_INVOICES_DUE_24_HOURS = "invoice.due.24.hours.mine";

	//Autotask
	public static final String AUTOTASK_NOTES_ENABLED = "autotask.notes.enabled";
	public static final String AUTOTASK_NOTES_INTERNAL = "autotask.notes.internal";
	public static final String AUTOTASK_ATTACHMENTS_INTERNAL = "autotask.attachments.internal";

	//Assets
	public static final String ASSET_DOCUMENTATION_PACKAGE_DOWNLOAD = "asset.documentation-package";

	//Forums
	public static final String FORUM_POST_COMMENT_ADDED = "forums.comment.added";

	private Boolean configurableFlag = Boolean.FALSE;
	private Boolean isDefault = Boolean.FALSE;
	private Boolean isBullhornDefault = Boolean.FALSE;
	private Boolean emailFlag;
	private Boolean followFlag;
	private Boolean bullhornFlag;
	private Boolean pushFlag;
	private Boolean smsFlag;
	private Boolean userNotificationFlag = Boolean.FALSE;
	private Boolean voiceFlag = Boolean.FALSE;

	// dispatch notifications are defaulting to the same values as the "regular" notification flags
	private Boolean dispatchEmailFlag = emailFlag;
	private Boolean dispatchBullhornFlag = bullhornFlag;
	private Boolean dispatchPushFlag = pushFlag;
	private Boolean dispatchSmsFlag = smsFlag;
	private Boolean dispatchVoiceFlag = voiceFlag;


	public NotificationType() {}

	public NotificationType(String code){
		super(code);
	}
	
	@Column(name="configurable_flag", nullable=false, length=1)
	public Boolean isConfigurable() {
		return configurableFlag;
	}
	
	public void setConfigurable(Boolean configurableFlag) {
		this.configurableFlag = configurableFlag;
	}
	
	@Column(name="default_flag", nullable=false, length=1)
	public Boolean isDefault() {
		return isDefault;
	}
	
	public void setDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Column(name="default_bullhorn_flag", nullable=false, length=1)
	public Boolean isBullhornDefault() {
		return isBullhornDefault;
	}

	public void setBullhornDefault(Boolean isBullhornDefault) {
		this.isBullhornDefault = isBullhornDefault;
	}
	
	@Column(name="email_flag", nullable=false, length=1)
	public Boolean getEmailFlag() {
		return emailFlag;
	}
	
	public void setEmailFlag(Boolean emailFlag) {
		this.emailFlag = emailFlag;
	}

	@Column(name="follow_flag", nullable=false, length=1)
	public Boolean getFollowFlag() {
		return followFlag;
	}

	public void setFollowFlag(Boolean followFlag) {
		this.followFlag = followFlag;
	}

	@Column(name="bullhorn_flag", nullable=false, length=1)
	public Boolean getBullhornFlag() {
		return bullhornFlag;
	}

	public void setBullhornFlag(Boolean bullhornFlag) {
		this.bullhornFlag = bullhornFlag;
	}
	
	@Column(name="sms_flag", nullable=false, length=1)
	public Boolean getSmsFlag() {
		return smsFlag;
	}
	
	public void setSmsFlag(Boolean smsFlag) {
		this.smsFlag = smsFlag;
	}

	@Column(name="user_notification_flag", nullable=false)
	public Boolean getUserNotificationFlag() {
		return userNotificationFlag;
	}

	public void setUserNotificationFlag(Boolean userNotificationFlag) {
		this.userNotificationFlag = userNotificationFlag;
	}

	@Column(name="voice_flag", nullable=false)
	public Boolean getVoiceFlag() {
		return voiceFlag;
	}

	public void setVoiceFlag(Boolean voiceFlag) {
		this.voiceFlag = voiceFlag;
	}

	@Column(name="push_flag", nullable=false, length=1)
	public Boolean getPushFlag() {
		return pushFlag;
	}

	public void setPushFlag(Boolean pushFlag) {
		this.pushFlag = pushFlag;
	}

	@Column(name="dispatch_email_flag", nullable=false, length=1)
	public Boolean getDispatchEmailFlag() {
		return dispatchEmailFlag;
	}

	public void setDispatchEmailFlag(Boolean dispatchEmailFlag) {
		this.dispatchEmailFlag = dispatchEmailFlag;
	}

	@Column(name="dispatch_bullhorn_flag", nullable=false, length=1)
	public Boolean getDispatchBullhornFlag() {
		return dispatchBullhornFlag;
	}

	public void setDispatchBullhornFlag(Boolean dispatchBullhornFlag) {
		this.dispatchBullhornFlag = dispatchBullhornFlag;
	}

	@Column(name="dispatch_push_flag", nullable=false, length=1)
	public Boolean getDispatchPushFlag() {
		return dispatchPushFlag;
	}

	public void setDispatchPushFlag(Boolean dispatchPushFlag) {
		this.dispatchPushFlag = dispatchPushFlag;
	}

	@Column(name="dispatch_sms_flag", nullable=false, length=1)
	public Boolean getDispatchSmsFlag() {
		return dispatchSmsFlag;
	}

	public void setDispatchSmsFlag(Boolean dispatchSmsFlag) {
		this.dispatchSmsFlag = dispatchSmsFlag;
	}

	@Column(name="dispatch_voice_flag", nullable=false, length=1)
	public Boolean getDispatchVoiceFlag() {
		return dispatchVoiceFlag;
	}

	public void setDispatchVoiceFlag(Boolean dispatchVoiceFlag) {
		this.dispatchVoiceFlag = dispatchVoiceFlag;
	}

	public static NotificationType newNotificationType(String code) {
		return new NotificationType(code);
	}

	@Override
	public String toString() {
		return "NotificationType{" +
			"configurableFlag=" + configurableFlag +
			", isDefault=" + isDefault +
			", isBullhornDefault=" + isBullhornDefault +
			", emailFlag=" + emailFlag +
			", followFlag=" + followFlag +
			", bullhornFlag=" + bullhornFlag +
			", pushFlag=" + pushFlag +
			", smsFlag=" + smsFlag +
			", userNotificationFlag=" + userNotificationFlag +
			", voiceFlag=" + voiceFlag +
			", dispatchEmailFlag=" + dispatchEmailFlag +
			", dispatchBullhornFlag=" + dispatchBullhornFlag +
			", dispatchPushFlag=" + dispatchPushFlag +
			", dispatchSmsFlag=" + dispatchSmsFlag +
			", dispatchVoiceFlag=" + dispatchVoiceFlag +
			'}';
	}
}