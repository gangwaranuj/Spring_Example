package com.workmarket.domains.work.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.workmarket.domains.model.AbstractEntity;

@Entity(name = "work_realtime_working_on_it")
public class RealtimeWorkingOnIt extends AbstractEntity {

	private static final long serialVersionUID = -2763446003134007242L;

	private Long workId;
	private String onBehalfOfUserNumber;	
	private String masqueradeUserNumber;
	private Calendar closedOn;
	private Calendar createdOn;
	
	@Column(name="work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}
	public void setWorkId(Long workId) {
		this.workId = workId;
	}
	@Column(name="working_on_user_number", nullable = false)
	public String getOnBehalfOfUserNumber() {
		return onBehalfOfUserNumber;
	}
	public void setOnBehalfOfUserNumber(String onBehalfOfUserNumber) {
		this.onBehalfOfUserNumber = onBehalfOfUserNumber;
	}
	@Column(name="masquerade_user_number", nullable = true)
	public String getMasqueradeUserNumber() {
		return masqueradeUserNumber;
	}
	public void setMasqueradeUserNumber(String masqueradeUserNumber) {
		this.masqueradeUserNumber = masqueradeUserNumber;
	}
	@Column(name="created_on", nullable = false)
	public Calendar getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}
	@Column(name = "closed_on", nullable = true)
	public Calendar getClosedOn() {
		return closedOn;
	}
	public void setClosedOn(Calendar closedOn) {
		this.closedOn = closedOn;
	}
	@Column(name = "is_open", nullable = false)
	public Boolean getIsOpen() {
		return closedOn == null;
	}
	public void setIsOpen(Boolean isOpen) {
		if (isOpen == false) {
			this.closedOn = Calendar.getInstance();
		}
	}
	@Override
	public String toString() {
		return "RealtimeWorkingOnIt [workId=" + workId
				+ ", onBehalfOfUserNumber=" + onBehalfOfUserNumber
				+ ", masqueradeUserNumber=" + masqueradeUserNumber
				+ ", closedOn=" + closedOn + ", createdOn=" + createdOn + "]";
	}
	
	

}
