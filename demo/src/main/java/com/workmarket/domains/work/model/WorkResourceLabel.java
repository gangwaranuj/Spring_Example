package com.workmarket.domains.work.model;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.summary.TimeDimension;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name = "workResourceLabel")
@Table(name = "work_resource_label")
@NamedQueries({
		@NamedQuery(name = "workResourceLabel.findForWork", query = "from workResourceLabel l join fetch l.workResourceLabelType where l.workId = :workId and l.workResourceLabelType.visible = true"),
		@NamedQuery(name = "workResourceLabel.findByWorkResourceId", query = "from workResourceLabel l join fetch l.workResourceLabelType where l.workResourceId = :workResourceId"),
		@NamedQuery(name = "workResourceLabel.findByWorkResourceLabelAndResourceId", query = "from workResourceLabel wrl where wrl.workResourceId = :workResourceId AND wrl.workResourceLabelType.code = :workResourceLabelTypeCode"),
		@NamedQuery(name = "workResourceLabel.countConfirmedWorkResourceLabelByUserId", query = WorkResourceLabel.LABEL_COUNT),
		@NamedQuery(name = "workResourceLabel.countConfirmedWorkResourceLabelByUserIdAndLessThan24HrsFlag", query = WorkResourceLabel.LABEL_COUNT + " AND wrl.lessThan24HoursFromAppointmentTime = :lessThan24HoursFromAppointmentTime"),
		@NamedQuery(name = "workResourceLabel.countConfirmedWorkResourceLabelByUserIdAndCompany", query = WorkResourceLabel.LABEL_COUNT + " AND wrl.workCompanyId = :companyId"),
		@NamedQuery(name = "workResourceLabel.countConfirmedWorkResourceLabelByUserIdAndCompanyAndLessThan24HrsFlag", query = WorkResourceLabel.LABEL_COUNT + " AND wrl.workCompanyId = :companyId AND wrl.lessThan24HoursFromAppointmentTime = :lessThan24HoursFromAppointmentTime")
})
@AuditChanges
public class WorkResourceLabel extends AuditedEntity {

	protected static final String LABEL_COUNT = "SELECT count(wrl) FROM workResourceLabel wrl where wrl.workResourceUserId IN :userIds AND wrl.workResourceLabelType.code = :workResourceLabelTypeCode AND wrl.createdOn >= :createdOn AND wrl.confirmed = TRUE AND wrl.ignored = FALSE";

	private static final long serialVersionUID = 1L;

	private Long workResourceId;
	private WorkResourceLabelType workResourceLabelType;
	private Long workResourceUserId;
	private Long workId;
	private Long workCompanyId;
	private boolean confirmed = false;
	private User confirmedBy;
	private Calendar confirmedOn;
	private boolean ignored = false;
	private User ignoredBy;
	private Calendar ignoredOn;
	private boolean lessThan24HoursFromAppointmentTime = false;
	private TimeDimension date;

	public WorkResourceLabel() {
	}

	public WorkResourceLabel(WorkResource workResource, WorkResourceLabelType workResourceLabelType) {
		if (workResource != null) {
			this.workResourceId = workResource.getId();
			this.workId = workResource.getWork().getId();
			this.workCompanyId = workResource.getWork().getCompany().getId();
			this.workResourceUserId = workResource.getUser().getId();
		}
		this.workResourceLabelType = workResourceLabelType;
	}

	@Column(name = "confirmed")
	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "confirmed_by", referencedColumnName = "id")
	public User getConfirmedBy() {
		return confirmedBy;
	}

	public void setConfirmedBy(User confirmedBy) {
		this.confirmedBy = confirmedBy;
	}

	@Column(name = "confirmed_on")
	public Calendar getConfirmedOn() {
		return confirmedOn;
	}

	public void setConfirmedOn(Calendar confirmedOn) {
		this.confirmedOn = confirmedOn;
	}

	@Column(name = "work_id", updatable = false, nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "work_company_id", updatable = false, nullable = false)
	public Long getWorkCompanyId() {
		return workCompanyId;
	}

	public void setWorkCompanyId(Long workCompanyId) {
		this.workCompanyId = workCompanyId;
	}

	@Column(name = "work_resource_id", updatable = false, nullable = false)
	public Long getWorkResourceId() {
		return workResourceId;
	}

	public void setWorkResourceId(Long workResourceId) {
		this.workResourceId = workResourceId;
	}

	@Column(name = "work_resource_user_id", updatable = false, nullable = false)
	public Long getWorkResourceUserId() {
		return workResourceUserId;
	}

	public void setWorkResourceUserId(Long workResourceUserId) {
		this.workResourceUserId = workResourceUserId;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "work_resource_label_type_code", referencedColumnName = "code", nullable = false)
	public WorkResourceLabelType getWorkResourceLabelType() {
		return workResourceLabelType;
	}

	public void setWorkResourceLabelType(WorkResourceLabelType workResourceLabelType) {
		this.workResourceLabelType = workResourceLabelType;
	}

	@Column(name = "ignored", nullable = false)
	public boolean isIgnored() {
		return ignored;
	}

	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ignored_by", referencedColumnName = "id")
	public User getIgnoredBy() {
		return ignoredBy;
	}

	public void setIgnoredBy(User ignoredBy) {
		this.ignoredBy = ignoredBy;
	}

	@Column(name = "ignored_on")
	public Calendar getIgnoredOn() {
		return ignoredOn;
	}

	public void setIgnoredOn(Calendar ignoredOn) {
		this.ignoredOn = ignoredOn;
	}

	public void ignore(User ignoredBy) {
		this.ignored = true;
		this.ignoredBy = ignoredBy;
		this.ignoredOn = Calendar.getInstance();
	}

	public void unIgnore() {
		this.ignored = false;
		this.ignoredBy = null;
		this.ignoredOn = null;
	}

	@Column(name = "less_than_24_hours_from_appointment", nullable = false)
	public boolean isLessThan24HoursFromAppointmentTime() {
		return lessThan24HoursFromAppointmentTime;
	}

	public void setLessThan24HoursFromAppointmentTime(boolean lessThan24HoursFromAppointmentTime) {
		this.lessThan24HoursFromAppointmentTime = lessThan24HoursFromAppointmentTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "date_id", referencedColumnName = "id")
	public TimeDimension getDate() {
		return date;
	}

	public void setDate(TimeDimension date) {
		this.date = date;
	}

	@Transient
	public String getCode() {
		return workResourceLabelType != null ? workResourceLabelType.getCode() : StringUtils.EMPTY;
	}

	@Transient
	public String getDescription() {
		return workResourceLabelType != null ? workResourceLabelType.getDescription() : StringUtils.EMPTY;
	}
}
