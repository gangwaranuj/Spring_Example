package com.workmarket.domains.model.changelog.work;


import com.google.common.collect.Maps;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.Map;

@Entity(name="workChangelog")
@Table(name="work_changelog")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType= DiscriminatorType.STRING)
@DiscriminatorValue(WorkChangeLog.WORK)
@AuditChanges
public class WorkChangeLog extends AuditedEntity {
	private static final long serialVersionUID = -133637185459806801L;

	public static final String WORK = "work";
	public static final String WORK_CREATED = "created";
	public static final String WORK_UPDATED = "updated";
	public static final String WORK_QUESTION_ASKED = "askQuestion";
	public static final String WORK_QUESTION_ANSWERED = "answerQuestion";
	public static final String WORK_STATUS_CHANGE = "statusChange";
	public static final String WORK_RESOURCE_STATUS_CHANGE = "resourceStatus";
	public static final String WORK_NOTE_CREATED = "noteCreated";
	public static final String WORK_PROPERTY = "propertyChanged";
	public static final String WORK_DELEGATED = "delegated";
	public static final String WORK_NEGOTIATION_REQUESTED = "negotiationReq";
	public static final String WORK_NEGOTIATION_STATUS_CHANGE = "negotiationStatus";
	public static final String WORK_RESCHEDULE_REQUESTED = "rescheduleReq";
	public static final String WORK_RESCHEDULE_AUTO_APPROVED = "rescheduleAutoApprvd";
	public static final String WORK_RESCHEDULE_STATUS_CHANGE = "rescheduleStatus";
	public static final String WORK_SUB_STATUS_CHANGE = "substatus";
	public static final String WORK_NEGOTIATION_EXPIRED = "negotiationExp";
	public static final String WORK_CLOSEOUT_ATTACHMENT_UPLOADED = "closeoutAttUploaded";
	public static final String WORK_UNASSIGN = "unassign";
	public static final String WORK_NOTIFY = "workNotify";
	public static final String WORK_SUPPORT_CONTACT_CHANGED = "supportContact";
	public static final String WORK_INTERNAL_OWNER_CHANGED = "internalOwner";

	private Long workId;
	private Long actorId;
	private Long masqueradeActorId;
	private Long onBehalfOfActorId;
	private String workTitle;
	private String actorFirstName;
	private String actorLastName;
	private String actorUserNumber;
	private String actorEmail;
	private String onBehalfOfActorFullName;
	private String type;
	private static final Map<String, String> DESCRIPTION_TYPE_MAP;
	public static final Map<String, Class<? extends WorkChangeLog>> CLASS_TYPE_MAP;

	static {
		DESCRIPTION_TYPE_MAP = Maps.newHashMapWithExpectedSize(21);
		CLASS_TYPE_MAP = Maps.newHashMapWithExpectedSize(21);

		DESCRIPTION_TYPE_MAP.put(WORK_CREATED, WorkCreatedChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_UPDATED, WorkUpdatedChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_QUESTION_ASKED, WorkQuestionAskedChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_QUESTION_ANSWERED, WorkQuestionAnsweredChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_STATUS_CHANGE, WorkStatusChangeChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_RESOURCE_STATUS_CHANGE, WorkResourceStatusChangeChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_NOTE_CREATED, WorkNoteCreatedChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_PROPERTY, WorkPropertyChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_NEGOTIATION_STATUS_CHANGE, WorkNegotiationStatusChangeChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_RESCHEDULE_REQUESTED, WorkRescheduleRequestedChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_RESCHEDULE_AUTO_APPROVED, WorkRescheduleAutoApprovedChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_RESCHEDULE_STATUS_CHANGE, WorkRescheduleStatusChangeChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_NEGOTIATION_EXPIRED, WorkNegotiationExpiredChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_CLOSEOUT_ATTACHMENT_UPLOADED, WorkCloseoutAttachmentUploadedChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_UNASSIGN, WorkUnassignChangeLog.getDescription());
		DESCRIPTION_TYPE_MAP.put(WORK_NOTIFY, WorkNotifyChangeLog.getDescription());

		CLASS_TYPE_MAP.put(WORK_CREATED, WorkCreatedChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_UPDATED, WorkUpdatedChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_QUESTION_ASKED, WorkQuestionAskedChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_QUESTION_ANSWERED, WorkQuestionAnsweredChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_STATUS_CHANGE, WorkStatusChangeChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_RESOURCE_STATUS_CHANGE, WorkResourceStatusChangeChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_NOTE_CREATED, WorkNoteCreatedChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_PROPERTY, WorkPropertyChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_NEGOTIATION_STATUS_CHANGE, WorkNegotiationStatusChangeChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_RESCHEDULE_REQUESTED, WorkRescheduleRequestedChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_RESCHEDULE_AUTO_APPROVED, WorkRescheduleAutoApprovedChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_RESCHEDULE_STATUS_CHANGE, WorkRescheduleStatusChangeChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_NEGOTIATION_EXPIRED, WorkNegotiationExpiredChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_CLOSEOUT_ATTACHMENT_UPLOADED, WorkCloseoutAttachmentUploadedChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_UNASSIGN, WorkUnassignChangeLog.class);
		CLASS_TYPE_MAP.put(WORK_NOTIFY, WorkNotifyChangeLog.class);
	}

	public WorkChangeLog() {}
	protected WorkChangeLog(Long workId, Long actorId, Long masqueradeActorId, Long onBehalfOfActorId) {
		this.workId = workId;
		this.actorId = actorId;
		this.masqueradeActorId = masqueradeActorId;
		this.onBehalfOfActorId = onBehalfOfActorId;
	}

	@Column(name = "work_id")
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "actor_id")
	public Long getActorId() {
		return actorId;
	}

	public void setActorId(Long actorId) {
		this.actorId = actorId;
	}

	@Column(name = "masquerade_actor_id")
	public Long getMasqueradeActorId() {
		return masqueradeActorId;
	}

	public void setMasqueradeActorId(Long masqueradeActorId) {
		this.masqueradeActorId = masqueradeActorId;
	}

	@Column(name = "on_behalf_of_actor_id")
	public Long getOnBehalfOfActorId() {
		return onBehalfOfActorId;
	}

	public void setOnBehalfOfActorId(Long onBehalfOfActorId) {
		this.onBehalfOfActorId = onBehalfOfActorId;
	}

	@Transient
	public String getChangeLogType() {
		return this.getClass().getSimpleName();
	}

	@Transient
	public String getWorkTitle() {
		return workTitle;
	}

	@Transient
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	@Transient
	public String getActorFullName() {
		return StringUtilities.fullName(actorFirstName, actorLastName);
	}

	@Transient
	public String getOnBehalfOfActorFullName() {
		return onBehalfOfActorFullName;
	}

	@Transient
	public void setOnBehalfOfActorFullName(String onBehalfOfActorFullName) {
		this.onBehalfOfActorFullName = onBehalfOfActorFullName;
	}

	@Transient
	public String getType() {
		return type;
	}

	@Transient
	public void setType(String type) {
		this.type = type;
	}

	@Transient
	public String getDescription(String type) {
		return MapUtils.getString(DESCRIPTION_TYPE_MAP, type, StringUtils.EMPTY);
	}

	@Transient
	public String getActorEmail() {
		return actorEmail;
	}

	public void setActorEmail(String actorEmail) {
		this.actorEmail = actorEmail;
	}

	@Transient
	public String getActorFirstName() {
		return actorFirstName;
	}

	public void setActorFirstName(String actorFirstName) {
		this.actorFirstName = actorFirstName;
	}

	@Transient
	public String getActorLastName() {
		return actorLastName;
	}

	public void setActorLastName(String actorLastName) {
		this.actorLastName = actorLastName;
	}

	@Transient
	public String getActorUserNumber() {
		return actorUserNumber;
	}

	public void setActorUserNumber(String actorUserNumber) {
		this.actorUserNumber = actorUserNumber;
	}
}
