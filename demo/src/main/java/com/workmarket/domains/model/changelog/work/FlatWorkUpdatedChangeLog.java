package com.workmarket.domains.model.changelog.work;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="flatWorkChangelog")
@Table(name="work_changelog")
@AuditChanges
public class FlatWorkUpdatedChangeLog extends AuditedEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -5981124321353595741L;

	private Long workId;
	private Long actorId;
	private Long masqueradeActorId;
	private Long onBehalfOfActorId;
	private String type = WorkChangeLog.WORK_UPDATED;

	public FlatWorkUpdatedChangeLog() {}

	public FlatWorkUpdatedChangeLog(Long workId, Long actorId, Long masqueradeActorId, Long onBehalfOfActorId) {
		this.workId = workId;
		this.actorId = actorId;
		this.masqueradeActorId = masqueradeActorId;
		this.onBehalfOfActorId = onBehalfOfActorId;
	}

	@Column(name = "work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "actor_id", nullable = false)
	public Long getActorId() {
		return actorId;
	}

	public void setActorId(Long actorId) {
		this.actorId = actorId;
	}

	@Column(name = "masquerade_actor_id", nullable = true)
	public Long getMasqueradeActorId() {
		return masqueradeActorId;
	}

	public void setMasqueradeActorId(Long masqueradeActorId) {
		this.masqueradeActorId = masqueradeActorId;
	}

	@Column(name = "on_behalf_of_actor_id", nullable = true)
	public Long getOnBehalfOfActorId() {
		return onBehalfOfActorId;
	}

	public void setOnBehalfOfActorId(Long onBehalfOfActorId) {
		this.onBehalfOfActorId = onBehalfOfActorId;
	}

	@Column(name = "type", nullable = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
