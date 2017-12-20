package com.workmarket.domains.work.model;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Calendar;

@Entity(name = "workUniqueId")
@Table(name = "work_unique_id", uniqueConstraints = @UniqueConstraint(columnNames = {"id_value", "company_id", "version"}))
@AuditChanges
public class WorkUniqueId implements Serializable {

	private Long workId;
	private Company company;
	private String displayName;
	private String idValue;
	private Integer version;

	private Calendar createdOn;
	private Calendar modifiedOn;
	private Long modifierId;
	private Long creatorId;

	@Id
	@Column(name = "work_id")
	public Long getWorkId() {
		return workId;
	}

	public WorkUniqueId setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}

	@Column(name = "display_name")
	public String getDisplayName() {
		return displayName;
	}

	public WorkUniqueId setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	@Column(name = "id_value", nullable = false)
	public String getIdValue() {
		return idValue;
	}

	public WorkUniqueId setIdValue(String idValue) {
		this.idValue = idValue;
		return this;
	}

	@Column(name = "version")
	public Integer getVersion() {
		return version;
	}

	public WorkUniqueId setVersion(Integer version) {
		this.version = version;
		return this;
	}

	@ManyToOne
	@JoinColumn(name = "company_id")
	public Company getCompany() {
		return company;
	}

	public WorkUniqueId setCompany(Company company) {
		this.company = company;
		return this;
	}

	@Column(name = "created_on", nullable = false, updatable = false)
	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "modified_on", nullable = false)
	public Calendar getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Calendar modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	@Column(name = "modifier_id")
	public Long getModifierId() {
		return modifierId;
	}

	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	@Column(name = "creator_id")
	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

}
