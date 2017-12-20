package com.workmarket.common.template;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.customfield.SavedWorkCustomField;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.postalcode.PostalCodeUtilities;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Calendar;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public abstract class AbstractWorkNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = -8349622107512180930L;
	protected List<SavedWorkCustomField> customFields = Lists.newArrayList();
	private Work work;
	private double distanceInMilesToWork;
	private WorkNegotiation negotiation;
	private String encryptedWorkFollowId = null;
	private String workTitle;
	private Boolean priceNegotiation;
	private Boolean scheduleNegotiation;
	protected String noteCreatorFullName;
	protected Long activeWorkerId;

	protected AbstractWorkNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work) {
		super(fromId, toId, notificationType, replyToType);
		this.work = work;
		// Note: For IVR, work may not be initially set until further along in the dialplan.
		if (work != null) {
			this.setTimeZoneId(work.getTimeZone().getTimeZoneId());
			this.workTitle = StringEscapeUtils.unescapeHtml4(work.getTitle());
			try {
				customFields = work.getWorkCustomFieldsForEmailDisplay();
			} catch (Exception e) {
				customFields = null;
			}
		}
		this.setEnabledDeliveryMethods(true, true, true, false, true);
	}

	protected AbstractWorkNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work, String noteCreatorFullName) {
		this(fromId, toId, notificationType, replyToType, work);
		this.noteCreatorFullName = noteCreatorFullName;
		this.workTitle = StringEscapeUtils.unescapeHtml4(work.getTitle());
	}

	protected AbstractWorkNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work, Long activeWorkerId) {
		this(fromId, toId, notificationType, replyToType, work);
		this.activeWorkerId = activeWorkerId;
		this.workTitle = StringEscapeUtils.unescapeHtml4(work.getTitle());
	}

	protected AbstractWorkNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work, double distanceInMilesToWork) {
		super(fromId, toId, notificationType, replyToType);
		this.work = work;
		// Note: For IVR, work may not be initially set until further along in the dialplan.
		if (work != null) {
			this.setTimeZoneId(work.getTimeZone().getTimeZoneId());
			this.customFields = work.getWorkCustomFieldsForEmailDisplay();
		}
		this.setEnabledDeliveryMethods(true, true, true, false, true);
		this.distanceInMilesToWork = distanceInMilesToWork;
		this.workTitle = StringEscapeUtils.unescapeHtml4(work.getTitle());
	}

	protected AbstractWorkNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work, WorkNegotiation negotiation) {
		super(fromId, toId, notificationType, replyToType);
		this.work = work;
		// Note: For IVR, work may not be initially set until further along in the dialplan.
		if (work != null) {
			this.setTimeZoneId(work.getTimeZone().getTimeZoneId());
			this.customFields = work.getWorkCustomFieldsForEmailDisplay();
		}
		this.setEnabledDeliveryMethods(true, true, true, false, true);
		this.negotiation = negotiation;
		// Note: Be careful when removing/moving these as they are not available in the negotiation object.
		if (negotiation != null) {
			this.priceNegotiation = negotiation.isPriceNegotiation();
			this.scheduleNegotiation = negotiation.isScheduleNegotiation();
		}
		this.workTitle = StringEscapeUtils.unescapeHtml4(work.getTitle());
	}

	protected AbstractWorkNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work, WorkNegotiation negotiation, String noteCreatorFullName) {
		this(fromId, toId, notificationType, replyToType, work, negotiation);
		this.noteCreatorFullName = noteCreatorFullName;
		this.workTitle = StringEscapeUtils.unescapeHtml4(work.getTitle());
	}

	public String getNoteCreatorFullName() { return noteCreatorFullName; }

	public Boolean getPriceNegotiation() {
		return priceNegotiation;
	}

	public Boolean getScheduleNegotiation() {
		return scheduleNegotiation;
	}

	public Work getWork() {
		return work;
	}

	public String getDate() {
		return getAppointmentString(work.getSchedule());
	}

	public String getWorkTitle() {
		return workTitle;
	}

	protected String getAppointmentString(DateRange appointment) {
		StringBuilder dateString = new StringBuilder(DateUtilities.formatDateForEmail(appointment.getFrom(), getTimeZoneId()));
		if (appointment.getThrough() != null) {
			dateString.append(" to: ");
			dateString.append(DateUtilities.formatDateForEmail(appointment.getThrough(), getTimeZoneId()));
		}
		return dateString.toString();
	}

	public String getPrice() {
		return (work.getPricingStrategy() != null ? work.getPricingStrategy().toString() : "");
	}

	public String getAddress() {
		if (work.isSetOnsiteAddress()) {
			if (work.getIsOnsiteAddress()) {
				if (work.getAddress() != null) {
					return work.getAddress().getShortAddress();
				}
			} else {
				return "Offsite";
			}
		}
		return "";
	}

	public String getWorkDueDate() {
		if (work.getDueOn() != null) {
			return DateUtilities.formatDateForEmail(work.getDueOn(), this.getTimeZoneId());
		}

		return "";
	}

	public double getDistanceInMilesToWork() {
		return distanceInMilesToWork;
	}

	public List<SavedWorkCustomField> getCustomFields() {
		return customFields;
	}

	public Boolean getWorkHasAttachments() {
		return isNotEmpty(work.getAssetAssociations());
	}

	public Boolean getIsWorkInternal() {
		return PricingStrategyType.INTERNAL.equals(work.getPricingStrategyType());
	}

	/**
	 * Format address for use in email
	 */
	public String getFormattedAddressForEmail() {
		Address a = work.getAddress();

		return ((work.isSetOnsiteAddress() && a != null)
				? a.getAddress1() + ", " + a.getShortAddress()
				: "Off-site");
	}

	/**
	 * Generate a URL for locating the assignment in Google Maps
	 */
	public String getGoogleMapsURL() {
		Address a = work.getAddress();
		if (a == null) {
			return StringUtils.EMPTY;
		}
		String formattedAddress = PostalCodeUtilities.formatAddressForGeocoder(a.getAddress1(), a.getAddress2(), a.getCity(), a.getState().getName(), a.getPostalCode(), a.getCountry().getName());

		return "http://maps.google.com/?q=" + formattedAddress;
	}

	/**
	 * Generate a URL for scheduling an assignment in Google Calendar
	 */
	public String getGoogleCalendarURL() {
		final String dateFormat = "yyyyMMdd'T'HHmmss'Z'";
		DateRange schedule = work.getSchedule();
		if (schedule == null) {
			return StringUtils.EMPTY;
		}
		String fromDate = DateUtilities.format(dateFormat, schedule.getFrom());
		Calendar toDate = schedule.getThrough();
		String dates = fromDate + "/" + (toDate == null ? fromDate : DateUtilities.format(dateFormat, toDate));

		return "http://www.google.com/calendar/event?action=TEMPLATE"
				+ "&text=" + StringUtilities.urlEncode(work.getTitle())
				+ "&dates=" + dates
				+ "&location=" + StringUtilities.urlEncode(getFormattedAddressForEmail())
				+ "&trp=true"
				+ "&sprop=" + StringUtilities.urlEncode("http://www.workmarket.com")
				+ "&sprop:name=" + StringUtilities.urlEncode("Work Market");
	}

	/**
	 * Format assignment confirmation date
	 */
	public String getFormattedConfirmationDateForEmail() {
		if (work.isResourceConfirmationRequired()) {
			Calendar confirmationDate = (Calendar) work.getScheduleFrom().clone();

			return DateUtilities.formatDateForEmail(
					DateUtilities.subtractTime(confirmationDate, work.getResourceConfirmationHours(), Constants.HOUR),
					getTimeZoneId());
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Format assignment schedule time
	 */
	public String getFormattedScheduleTimeForEmail() {
		String timeZoneId = getTimeZoneId();

		if (work.getIsScheduleRange()) {
			String timeFormat = "hh:mm aaa z";
			String fromTime = DateUtilities.format(timeFormat, work.getScheduleFrom(), timeZoneId);
			String toTime = DateUtilities.format(timeFormat, work.getScheduleThrough(), timeZoneId);

			return DateUtilities.format("EEE, d MMM yyyy", work.getScheduleFrom(), timeZoneId) + "; arrive between " + fromTime + " and " + toTime;
		}
		return DateUtilities.formatDateForEmail(work.getScheduleFrom(), timeZoneId);
	}

	public void setWorkFollow(WorkFollow workFollow) {
		encryptedWorkFollowId = workFollow.getEncryptedId();
	}

	public String getEncryptedWorkFollowId() {
		return encryptedWorkFollowId;
	}

	public Long getActiveWorkerId() {
		return activeWorkerId;
	}

	public boolean isToActiveWorker() {
		return !(getWork() == null || getActiveWorkerId() == null) && getActiveWorkerId().equals(getToId());
	}

	@Override
	public String toString() {
		return "AbstractWorkNotificationTemplate{" +
			"customFields=" + customFields +
			", work=" + work +
			", distanceInMilesToWork=" + distanceInMilesToWork +
			", negotiation=" + negotiation +
			", encryptedWorkFollowId='" + encryptedWorkFollowId + '\'' +
			", priceNegotiation=" + priceNegotiation +
			", scheduleNegotiation=" + scheduleNegotiation +
			", noteCreatorFullName='" + noteCreatorFullName + '\'' +
			", activeWorkerId=" + activeWorkerId +
			super.toString() +
			'}';
	}
}
