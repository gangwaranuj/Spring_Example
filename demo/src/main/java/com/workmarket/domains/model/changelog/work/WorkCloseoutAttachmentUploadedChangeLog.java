package com.workmarket.domains.model.changelog.work;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by nick on 4/18/13 6:03 PM
 */

@Entity
@AuditChanges
@DiscriminatorValue(WorkChangeLog.WORK_CLOSEOUT_ATTACHMENT_UPLOADED)
public class WorkCloseoutAttachmentUploadedChangeLog extends WorkChangeLog {
	private static final long serialVersionUID = 1L;

	// NOTE: this reuses columns from WorkChangeProperty to avoid adding new ones
	@NotNull
	@Size(min = 0, max = Constants.NAME_MAX_LENGTH)
	private String attachmentName;
	@Size(min = 0, max = Constants.TEXT_MAX_LENGTH)
	private String attachmentUrl;

	public WorkCloseoutAttachmentUploadedChangeLog() {}

	public WorkCloseoutAttachmentUploadedChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor, String attachmentName, String attachmentUrl) {
		super(work, actor, onBehalfOfActor, masqueradeActor);
		this.attachmentName = attachmentName;
		this.attachmentUrl = attachmentUrl;
	}

	// NOTE: this reuses columns from WorkChangeProperty to avoid adding new ones
	@Column(name = "property_name", length = Constants.NAME_MAX_LENGTH, nullable = true)
	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	// NOTE: this reuses columns from WorkChangeProperty to avoid adding new ones
	@Column(name = "new_value", length = Constants.TEXT_MAX_LENGTH, nullable = true)
	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	@Transient
	public static String getDescription() {
		return "Closeout attachment uploaded";
	}
}
