package com.workmarket.common.template;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.customfield.SavedWorkCustomField;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.DateUtilities;

import java.util.List;

public class AbstractWorkResourceNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = -7567408200800610013L;
	private WorkResource workResource;
	private Work work;
	protected List<SavedWorkCustomField> customFields = Lists.newArrayList();

	protected AbstractWorkResourceNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, WorkResource workResource) {
		super(fromId, toId, notificationType, replyToType);
		this.workResource = workResource;
		this.work = workResource.getWork();
		if (work != null)
			customFields = work.getWorkCustomFieldsForEmailDisplay();
	}

	protected AbstractWorkResourceNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work) {
		super(fromId, toId, notificationType, replyToType);
		this.work = work;
		if (work != null)
			customFields = work.getWorkCustomFieldsForEmailDisplay();
	}

	protected AbstractWorkResourceNotificationTemplate(Long fromId, Long toId, NotificationType notificationType, ReplyToType replyToType, Work work, WorkResource workResource) {
		super(fromId, toId, notificationType, replyToType);
		this.work = work;
		this.workResource = workResource;
		if (work != null)
			customFields = work.getWorkCustomFieldsForEmailDisplay();
	}

	public WorkResource getWorkResource() {
		return workResource;
	}

	public void setWorkResource(WorkResource workResource) {
		this.workResource = workResource;
	}

	public Work getWork() {
		return work;
	}

	public String getDate() {
		return getAppointmentString(work.getSchedule());
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
			return (work.getIsOnsiteAddress() ? work.getAddress().getShortAddress() : "Offsite");
		}
		return "";
	}

	public List<SavedWorkCustomField> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<SavedWorkCustomField> customFields) {
		this.customFields = customFields;
	}
}
