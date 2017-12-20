package com.workmarket.domains.model.summary.work;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class WorkStatusPK implements Serializable {

	private Long workId;
	private String workStatusTypeCode;

	public WorkStatusPK() {
	}

	public WorkStatusPK(Long workId, String workStatusTypeCode) {
		this.workId = workId;
		this.workStatusTypeCode = workStatusTypeCode;
	}

	@Column(name = "work_id", nullable = false, length = 11)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "work_status_type_code", nullable = true, length = 15)
	public String getWorkStatusTypeCode() {
		return workStatusTypeCode;
	}

	public void setWorkStatusTypeCode(String workStatusTypeCode) {
		this.workStatusTypeCode = workStatusTypeCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WorkStatusPK)) return false;

		WorkStatusPK that = (WorkStatusPK) o;

		if (workId != null ? !workId.equals(that.workId) : that.workId != null) return false;
		if (workStatusTypeCode != null ? !workStatusTypeCode.equals(that.workStatusTypeCode) : that.workStatusTypeCode != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = workId != null ? workId.hashCode() : 0;
		result = 31 * result + (workStatusTypeCode != null ? workStatusTypeCode.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "WorkStatusPK{" +
				"workId=" + workId +
				", workStatusTypeCode='" + workStatusTypeCode + '\'' +
				'}';
	}
}