package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name = "workVendorInvitation")
@Table(name = "work_to_company_association")
@AuditChanges
public class WorkVendorInvitation extends DeletableEntity {
	private static final long serialVersionUID = 8054714860290310154L;

	private Long workId;
	private Long companyId;
	private boolean isDeclined;
	private Calendar declinedOn;
	private Long declinedById;
	private boolean assignToFirstToAccept;

	public WorkVendorInvitation() {

	}

	public WorkVendorInvitation(Long workId, Long companyId) {
		this.workId = workId;
		this.companyId = companyId;
		this.assignToFirstToAccept = false;
	}

	public WorkVendorInvitation(Long workId, Long companyId, boolean assignToFirstToAccept) {
		this.workId = workId;
		this.companyId = companyId;
		this.assignToFirstToAccept = assignToFirstToAccept;
	}

	@Column(name = "work_id")
	public Long getWorkId() {
		return workId;
	}

	@Column(name = "company_id")
	public Long getCompanyId() {
		return companyId;
	}

	@Column(name = "is_declined")
	public boolean getIsDeclined() {
		return isDeclined;
	}

	@Column(name = "declined_on")
	public Calendar getDeclinedOn() {
		return declinedOn;
	}

	@Column(name = "declined_by_id")
	public Long getDeclinedById() {
		return declinedById;
	}

	@Column(name = "assign_to_first_resource")
	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	public void setWorkId(Long work) { this.workId = work; }

	public void setCompanyId(Long companyId) { this.companyId = companyId; }

	public void setIsDeclined(boolean isDeclined) {
		this.isDeclined = isDeclined;
	}

	public void setDeclinedOn(Calendar declinedOn) {
		this.declinedOn = declinedOn;
	}

	public void setDeclinedById(Long declinedById) { this.declinedById = declinedById; }

	public void setAssignToFirstToAccept(boolean assignToFirstToAccept) {
		this.assignToFirstToAccept = assignToFirstToAccept;
	}
}
