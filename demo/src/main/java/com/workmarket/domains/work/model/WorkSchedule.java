package com.workmarket.domains.work.model;

import com.workmarket.domains.model.DateRange;

import java.io.Serializable;
import java.util.List;

/**
 * Author: rocio
 */
public class WorkSchedule implements Serializable {

	private static final long serialVersionUID = 4838457666739132382L;

	private long workId;
	private long companyId;
	private String workNumber;
	private final DateRange dateRange;

	public WorkSchedule(DateRange dateRange) {
		this.dateRange = dateRange;
	}

	public long getWorkId() {
		return workId;
	}

	public WorkSchedule setWorkId(long workId) {
		this.workId = workId;
		return this;
	}

	public long getCompanyId() {
		return companyId;
	}

	public WorkSchedule setCompanyId(long companyId) {
		this.companyId = companyId;
		return this;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public WorkSchedule setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	@Override
	public String toString() {
		return "WorkSchedule{" +
				"workId=" + workId +
				", companyId=" + companyId +
				", workNumber='" + workNumber + '\'' +
				", dateRange=" + dateRange +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof WorkSchedule)) {
			return false;
		}

		WorkSchedule that = (WorkSchedule) o;

		if (companyId != that.companyId) return false;
		if (workId != that.workId) return false;
		if (dateRange != null ? !dateRange.equals(that.dateRange) : that.dateRange != null) return false;
		if (workNumber != null ? !workNumber.equals(that.workNumber) : that.workNumber != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (workId ^ (workId >>> 32));
		result = 31 * result + (int) (companyId ^ (companyId >>> 32));
		result = 31 * result + (workNumber != null ? workNumber.hashCode() : 0);
		result = 31 * result + (dateRange != null ? dateRange.hashCode() : 0);
		return result;
	}

	public boolean overlaps(List<WorkSchedule> workSchedules) {
		if (workSchedules != null) {
			for (WorkSchedule workSchedule : workSchedules) {
				if (overlaps(workSchedule) || workSchedule.overlaps(this)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean overlaps(WorkSchedule workSchedule) {
		if (workSchedule != null && workSchedule.getDateRange() != null && dateRange != null) {
			if (dateRange.isRange()) {
				return dateRange.overlaps(workSchedule.getDateRange());
			}
			if (workSchedule.getDateRange().isRange()) {
				return workSchedule.getDateRange().contains(dateRange.getFrom());
			}
			if (dateRange.getFrom() != null) {
				return dateRange.getFrom().compareTo(workSchedule.getDateRange().getFrom()) == 0;
			}
		}
		return false;
	}

	public boolean contains(WorkSchedule workSchedule) {
		if (workSchedule != null && workSchedule.getDateRange() != null && dateRange != null) {
			if (dateRange.isRange()) {
				return dateRange.contains(workSchedule.getDateRange());
			}
			if (workSchedule.getDateRange().isRange()) {
				return workSchedule.getDateRange().contains(dateRange.getFrom());
			}
			if (dateRange.getFrom() != null) {
				return dateRange.getFrom().compareTo(workSchedule.getDateRange().getFrom()) == 0;
			}
		}
		return false;
	}
}
