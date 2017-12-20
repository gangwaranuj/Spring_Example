package com.workmarket.domains.work.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
public class WorkRecurrencePK implements Serializable {

	private static final long serialVersionUID = -4613725074806114069L;

	private Long workId;
	private String recurrenceUUID;

	public WorkRecurrencePK() {
	}

	public WorkRecurrencePK(Long workId, String recurrenceUUID) {
		this.workId = workId;
		this.recurrenceUUID = recurrenceUUID;
	}

	@Column(name = "work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "recurrence_uuid", nullable = false)
	public String getRecurrenceUUID() {
		return recurrenceUUID;
	}

	public void setRecurrenceUUID(String recurrenceUUID) {
		this.recurrenceUUID = recurrenceUUID;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final WorkRecurrencePK that = (WorkRecurrencePK) o;
		return workId != null ? workId.equals(that.workId) : that.workId == null &&
				(recurrenceUUID != null ? recurrenceUUID.equals(that.recurrenceUUID) : that.recurrenceUUID == null);
	}

	@Override
	public int hashCode() {
		int result = workId != null ? workId.hashCode() : 0;
		result = 31 * result + (recurrenceUUID != null ? recurrenceUUID.hashCode() : 0);
		return result;
	}
}
