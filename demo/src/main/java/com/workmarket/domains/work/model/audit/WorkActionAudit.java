package com.workmarket.domains.work.model.audit;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "work_action_audit")
@Table(name = "work_action_audit")
public class WorkActionAudit {

	private Long workId;
	private Calendar lastActionOn;
	private Long modifierId;
	private Long masqueradeId;
	private Long onBehalfOfId;
	
	@Id
	@Column(name = "work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}
	public void setWorkId(Long workId) {
		this.workId = workId;
	}
	
	@Column(name = "last_action_on", nullable = false)
	public Calendar getLastActionOn() {
		return lastActionOn;
	}
	public void setLastActionOn(Calendar lastActionOn) {
		this.lastActionOn = lastActionOn;
	}
	
	@Column(name = "modifier_id", nullable = false)
	public Long getModifierId() {
		return modifierId;
	}
	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}
	
	@Column(name = "masquerade_id", nullable = true)
	public Long getMasqueradeId() {
		return masqueradeId;
	}
	public void setMasqueradeId(Long masqueradeId) {
		this.masqueradeId = masqueradeId;
	}
	
	@Column(name = "on_behalf_of_id", nullable = true)
	public Long getOnBehalfOfId() {
		return onBehalfOfId;
	}
	public void setOnBehalfOfId(Long onBehalfOfId) {
		this.onBehalfOfId = onBehalfOfId;
	}
	@Override
	public String toString() {
		return "WorkActionAudit [workId=" + workId + ", lastActionOn="
				+ lastActionOn + ", modifierId=" + modifierId
				+ ", masqueradeId=" + masqueradeId + ", onBehalfOfId="
				+ onBehalfOfId + "]";
	}
	
	
}
