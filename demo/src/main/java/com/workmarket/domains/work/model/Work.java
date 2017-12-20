package com.workmarket.domains.work.model;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.RequirementSetable;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Collection;

@Entity(name = "work")
@DiscriminatorValue("W")
@NamedQueries({
	@NamedQuery(name = "work.countActive", query = "select count(*) FROM work w WHERE w.workStatusType.code = 'active'"),
	@NamedQuery(
		name = "work.findAllResourcesWhereWorkStatusTypeNotIn",
		query =
			"SELECT wr FROM work_resource wr " +
			" WHERE wr.work.workStatusType not in (:statuses) " +
			" AND wr.work.company.id = :companyId AND wr.user.id = :userId " +
			" AND wr.assignedToWork = true AND wr.workResourceStatusType.code = 'active'"),
	@NamedQuery(
		name = "work.findAllWorkWhereWorkStatusTypeNotInAndWorkSubStatusTypeIn",
		query =
			"FROM work w WHERE w.deleted = 0 AND w.company.id = :companyId AND w.workStatusType.code not in (:statusCodes) " +
			" AND EXISTS " +
			" (SELECT a.id FROM workSubStatusTypeAssociation a WHERE work.id = w.id AND workSubStatusType.id = :subStatusId AND resolved=false AND deleted=false)"),
	@NamedQuery(
		name = "work.findAllWorkWhereTemplatesNotInAndWorkSubStatusTypeIn",
		query = "FROM work w WHERE w.deleted = 0 AND w.company.id = :companyId AND w.template.id not in (:templateIds) " +
			" AND EXISTS " +
			" (SELECT a.id FROM workSubStatusTypeAssociation a WHERE work.id = w.id AND workSubStatusType.id = :subStatusId AND resolved=false AND deleted=false)"),
	@NamedQuery(name = "work.getWorkWithSupportContactByWorkId", query = "from work w join fetch w.buyerSupportUser where w.id = :workId")
})
@AuditChanges
@Access(AccessType.FIELD)
public class Work extends AbstractWork implements RequirementSetable {

	private static final long serialVersionUID = 1L;

	@Column(name = "due_on")
	private Calendar dueOn;

	@Column(name = "statement_id")
	private Long statementId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", referencedColumnName = "id")
	private WorkBundle parent;

	@Fetch(FetchMode.JOIN)
	@OneToOne(cascade= CascadeType.ALL)
	@JoinColumn(name = "id", referencedColumnName = "work_id")
	private WorkUniqueId workUniqueId;

	@Transient
	private boolean partOfBulk;

	public Work() {
		super();
	}

	public Work(User buyer) {
		super(buyer);
	}

	public WorkBundle getParent() {
		return parent;
	}

	public void setParent(WorkBundle parent) {
		this.parent = parent;
	}

	public Calendar getDueOn() {
		return dueOn;
	}

	public void setDueOn(Calendar dueOn) {
		this.dueOn = dueOn;
	}

	public Long getStatementId() {
		return statementId;
	}

	public void setStatementId(Long statementId) {
		this.statementId = statementId;
	}

	@Transient
	public boolean hasStatement() {
		return statementId != null;
	}

	@Transient
	public boolean isInBundle() {
		return parent != null;
	}

	@Transient
	public boolean isBundleOrInBundle() {
		return this.isWorkBundle() || this.isInBundle();
	}

	@Transient
	public boolean isOpenable() {
		return isDraft() || isAbandoned() || isDeclined();
	}

	@Transient
	public boolean isRoutable() {
		return isDraft() || isAbandoned() || isDeclined() || isSent();
	}

	@Override
	@Transient
	public Collection<RequirementSet> getRequirementSetCollection() {
		return getRequirementSets();
	}

	@Override
	@Transient
	public TimeZone getRequirementSetableTimeZone() {
		return getTimeZone();
	}

	public boolean isPartOfBulk() {
		return partOfBulk;
	}

	public void setPartOfBulk(boolean partOfBulk) {
		this.partOfBulk = partOfBulk;
	}

	public WorkUniqueId getWorkUniqueId() {
		return workUniqueId;
	}

	public void setWorkUniqueId(WorkUniqueId workUniqueId) {
		this.workUniqueId = workUniqueId;
	}

}
