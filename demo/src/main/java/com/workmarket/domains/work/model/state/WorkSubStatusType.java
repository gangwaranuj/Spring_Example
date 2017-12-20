package com.workmarket.domains.work.model.state;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableLookupEntity;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkTemplate;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

@Entity(name = "work_sub_status_type")
@Table(name = "work_sub_status_type")
@AuditChanges
@AttributeOverrides({
	@AttributeOverride(name = "code", column = @Column(length = 35)),
	@AttributeOverride(name = "description", column = @Column(length = 35)) })
@NamedQueries({
	@NamedQuery(
		name = "work_sub_status_type.findByCodeAndCompany",
		query = "from work_sub_status_type ss " +
				"where ss.code = :code and ss.company.id = :companyId"
	)
})
public class WorkSubStatusType extends DeletableLookupEntity {

	private static final long serialVersionUID = 1L;

	public enum SubStatusType {
		ASSIGNMENT,
		RESOURCE,
		PARTS
	}

	public enum TriggeredBy {
		CLIENT,
		CLIENT_OR_RESOURCE,
		SYSTEM
	}

	private Set<WorkSubStatusTypeTemplateAssociation> workSubStatusTypeTemplateAssociations = Sets.newLinkedHashSet();
	private Set<WorkSubStatusTypeWorkStatusScope>	workSubStatusTypeWorkStatusScopes = Sets.newLinkedHashSet();
	private WorkSubStatusDescriptor subStatusDescriptor = new WorkSubStatusDescriptor();
	private Company company;
	private boolean active = true;

	@Transient
	private String customColorRgb;

	@Transient
	private WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType = WorkSubStatusTypeCompanySetting.DashboardDisplayType.SHOW;

	public static final String GENERAL_PROBLEM = "general_problem";
	public static final String INCOMPLETE_WORK = "incomplete_work";
	public static final String RESCHEDULE_REQUEST = "reschedule_request";
	public static final String RESOURCE_CANCELLED = "resource_cancelled";
	public static final String RESOURCE_CHECKED_IN = "resource_checked_in";
	public static final String RESOURCE_CHECKED_OUT = "resource_checked_out";
	public static final String RESOURCE_CONFIRMED = "resource_confirmed";
	public static final String RESOURCE_NOT_CONFIRMED = "resource_not_confirmed";
	public static final String RESOURCE_NO_SHOW = "resource_no_show";
	public static final String EXPENSE_REIMBURSEMENT = "spend_limit_increase";
	public static final String BUDGET_INCREASE = "budget_increase";
	public static final String BONUS = "bonus";
	public static final String STOP_PAYMENT_PENDING = "stop_payment_work";
	public static final String DELIVERABLE_REJECTED = "deliverable_rejected";
	public static final String DELIVERABLE_LATE = "deliverable_late";

	public WorkSubStatusType() {
		super();
	}

	public WorkSubStatusType(String code) {
		super(code);
	}

	public WorkSubStatusType(Company company) {
		super();
		this.subStatusDescriptor.setCustom(true);
		this.subStatusDescriptor.setClientVisible(true);
		this.subStatusDescriptor.setUserResolvable(true);
		this.setCompany(company);
	}

	public static WorkSubStatusType newWorkSubStatusType(String code) {
		return new WorkSubStatusType(code);
	}

	@Embedded
	public WorkSubStatusDescriptor getSubStatusDescriptor() {
		return subStatusDescriptor;
	}

	public void setSubStatusDescriptor(WorkSubStatusDescriptor subStatusDescriptor) {
		this.subStatusDescriptor = subStatusDescriptor;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany(mappedBy = "strong", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<WorkSubStatusTypeTemplateAssociation> getWorkSubStatusTypeTemplateAssociations() {
		return workSubStatusTypeTemplateAssociations;
	}

	@SuppressWarnings("unchecked")
	@Transient
	public void setWorkTemplates(Set<WorkTemplate> templates) {
		this.workSubStatusTypeTemplateAssociations = (Set<WorkSubStatusTypeTemplateAssociation>) WorkSubStatusTypeTemplateAssociation.updateAssociations(this, templates,
				getWorkSubStatusTypeTemplateAssociations(), WorkSubStatusTypeTemplateAssociation.class);
	}

	@Transient
	public Set<WorkTemplate> getWorkTemplates(){
		return WorkSubStatusTypeTemplateAssociation.getUndeletedWeak(this.getWorkSubStatusTypeTemplateAssociations());
	}

	@OneToMany(mappedBy = "strong", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<WorkSubStatusTypeWorkStatusScope> getWorkSubStatusTypeWorkStatusScopes() {
		return workSubStatusTypeWorkStatusScopes;
	}

	@SuppressWarnings("unchecked")
	@Transient
	public void setWorkScope(Set<WorkStatusType> workStatusTypes) {
		this.workSubStatusTypeWorkStatusScopes = (Set<WorkSubStatusTypeWorkStatusScope>) WorkSubStatusTypeWorkStatusScope.updateAssociations(this, workStatusTypes, getWorkSubStatusTypeWorkStatusScopes(),
				WorkSubStatusTypeWorkStatusScope.class);
	}

	@Transient
	public Set<WorkStatusType> getWorkScopes(){
		return WorkSubStatusTypeWorkStatusScope.getUndeletedWeak(this.getWorkSubStatusTypeWorkStatusScopes());
	}

	public void setWorkSubStatusTypeWorkStatusScopes(Set<WorkSubStatusTypeWorkStatusScope> workSubStatusTypeWorkStatusScopes) {
		this.workSubStatusTypeWorkStatusScopes = workSubStatusTypeWorkStatusScopes;
	}

	public void setWorkSubStatusTypeTemplateAssociations(Set<WorkSubStatusTypeTemplateAssociation> workSubStatusTypeTemplateAssociations) {
		this.workSubStatusTypeTemplateAssociations = workSubStatusTypeTemplateAssociations;
	}

	@Override
	public String toString() {
		return "WorkSubStatusType [" + "code=" + getCode() + " { " + subStatusDescriptor + " }]";
	}

	@Transient
	public String getCustomColorRgb() {
		return customColorRgb;
	}

	@Transient
	public void setCustomColorRgb(String customColorRgb) {
		this.customColorRgb = customColorRgb;
	}

	@Transient
	public boolean isResourceEditable() {
		return (subStatusDescriptor.getTriggeredBy().equals(TriggeredBy.CLIENT_OR_RESOURCE));
	}

	@Transient
	public Boolean getClientVisible() {
		return subStatusDescriptor.getClientVisible();
	}

	@Transient
	public void setTriggeredBy(TriggeredBy triggeredBy) {
		subStatusDescriptor.setTriggeredBy(triggeredBy);
	}

	@Transient
	public void setIncludeInstructions(boolean includeInstructions) {
		subStatusDescriptor.setIncludeInstructions(includeInstructions);
	}

	@Transient
	public Boolean getResourceVisible() {
		return subStatusDescriptor.getResourceVisible();
	}

	@Transient
	public void setScheduleRequired(boolean scheduleRequired) {
		subStatusDescriptor.setScheduleRequired(scheduleRequired);
	}

	@Transient
	public SubStatusType getSubStatusType() {
		return subStatusDescriptor.getSubStatusType();
	}

	@Transient
	public boolean isRemoveAfterReschedule() {
		return subStatusDescriptor.isRemoveAfterReschedule();
	}

	@Transient
	public void setCustom(boolean custom) {
		subStatusDescriptor.setCustom(custom);
	}

	@Transient
	public void setNotifyResourceEnabled(boolean notifyResourceEnabled) {
		subStatusDescriptor.setNotifyResourceEnabled(notifyResourceEnabled);
	}

	@Transient
	public boolean isActionResolvable() {
		return subStatusDescriptor.isActionResolvable();
	}

	@Transient
	public void setUserResolvable(boolean userResolvable) {
		subStatusDescriptor.setUserResolvable(userResolvable);
	}

	@Transient
	public String getInstructions() {
		return subStatusDescriptor.getInstructions();
	}

	@Transient
	public void setNoteRequired(boolean noteRequired) {
		subStatusDescriptor.setNoteRequired(noteRequired);
	}

	@Transient
	public boolean isAlert() {
		return subStatusDescriptor.isAlert();
	}

	@Transient
	public boolean isCustom() {
		return subStatusDescriptor.isCustom();
	}

	@Transient
	public boolean isSystem() {
		return !isCustom();
	}

	@Transient
	public void setResourceVisible(Boolean resourceVisible) {
		subStatusDescriptor.setResourceVisible(resourceVisible);
	}

	@Transient
	public void setRemoveAfterReschedule(boolean removeAfterReschedule) {
		subStatusDescriptor.setRemoveAfterReschedule(removeAfterReschedule);
	}

	@Transient
	public void setClientVisible(Boolean clientVisible) {
		subStatusDescriptor.setClientVisible(clientVisible);
	}

	@Transient
	public boolean isNoteRequired() {
		return subStatusDescriptor.isNoteRequired();
	}

	@Transient
	public TriggeredBy getTriggeredBy() {
		return subStatusDescriptor.getTriggeredBy();
	}

	@Transient
	public void setActionResolvable(boolean actionResolvable) {
		subStatusDescriptor.setActionResolvable(actionResolvable);
	}

	@Transient
	public void setSubStatusType(SubStatusType subStatusType) {
		subStatusDescriptor.setSubStatusType(subStatusType);
	}

	@Transient
	public boolean isScheduleRequired() {
		return subStatusDescriptor.isScheduleRequired();
	}

	@Transient
	public boolean isIncludeInstructions() {
		return subStatusDescriptor.isIncludeInstructions();
	}

	@Transient
	public boolean isNotifyClientEnabled() {
		return subStatusDescriptor.isNotifyClientEnabled();
	}

	@Transient
	public void setNotifyClientEnabled(boolean notifyClientEnabled) {
		subStatusDescriptor.setNotifyClientEnabled(notifyClientEnabled);
	}

	@Transient
	public void setAlert(boolean alert) {
		subStatusDescriptor.setAlert(alert);
	}

	@Transient
	public boolean isNotifyResourceEnabled() {
		return subStatusDescriptor.isNotifyResourceEnabled();
	}

	@Transient
	public void setInstructions(String instructions) {
		subStatusDescriptor.setInstructions(instructions);
	}

	@Transient
	public boolean isUserResolvable() {
		return subStatusDescriptor.isUserResolvable();
	}

	@Transient
	public PrivacyType getNotePrivacy() {
		return subStatusDescriptor.getNotePrivacy();
	}

	@Transient
	public void setNotePrivacy(PrivacyType notePrivacy) {
		subStatusDescriptor.setNotePrivacy(notePrivacy);
	}

	@Transient
	public boolean isRemoveOnVoidOrCancelled() {
		return subStatusDescriptor.isRemoveOnVoidOrCancelled();
	}

	@Transient
	public void setRemoveOnVoidOrCancelled(boolean removeOnVoidOrCancelled) {
		subStatusDescriptor.setRemoveOnVoidOrCancelled(removeOnVoidOrCancelled);
	}

	@Transient
	public boolean isRemoveOnPaid() {
		return subStatusDescriptor.isRemoveOnPaid();
	}

	@Transient
	public void setRemoveOnPaid(boolean removeOnPaid) {
		subStatusDescriptor.setRemoveOnPaid(removeOnPaid);
	}

	@Transient
	public WorkSubStatusTypeCompanySetting.DashboardDisplayType getDashboardDisplayType() {
		return dashboardDisplayType;
	}

	@Transient
	public void setDashboardDisplayType(WorkSubStatusTypeCompanySetting.DashboardDisplayType dashboardDisplayType) {
		this.dashboardDisplayType = dashboardDisplayType;
	}

	public boolean isWorkSubStatusApplicableForWorkStatusType(WorkStatusType workStatusType) {
		if (isRemoveOnPaid() && workStatusType.isPaid()) {
			return false;
		}

		/* Is work void and cancelled and label should be removed on void or cancelled? */
		if (isRemoveOnVoidOrCancelled() && workStatusType.isVoidOrCancelled()) {
			return false;
		}

		/* Are work status types applicable for this work? */
		Set<WorkStatusType> statusTypes = getWorkScopes();
		if (CollectionUtils.isNotEmpty(statusTypes) && !statusTypes.contains(workStatusType)) {
			return false;
		}

		return true;
	}
}
