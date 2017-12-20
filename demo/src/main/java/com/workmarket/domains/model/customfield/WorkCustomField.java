package com.workmarket.domains.model.customfield;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Entity(name = "work_custom_field")
@Table(name = "work_custom_field")
@AuditChanges
public class WorkCustomField extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private String name;
	private String defaultValue;
	private Boolean visibleToResourceFlag = Boolean.FALSE;
	private Boolean visibleToOwnerFlag = Boolean.TRUE;
	private Boolean requiredFlag = Boolean.FALSE;
	private WorkCustomFieldGroup workCustomFieldGroup;
	private WorkCustomFieldType workCustomFieldType;
	private Boolean showOnInvoice = Boolean.FALSE;
	private Boolean showOnDashboard = Boolean.FALSE;
	private Boolean showOnPrintout = Boolean.FALSE;
	private Boolean showOnSentStatus = Boolean.FALSE;
	private Boolean showInAssignmentHeader = Boolean.FALSE;
	private Boolean showInAssignmentEmail = Boolean.FALSE;
	private Integer position = 0;

	public static WorkCustomField copy(WorkCustomField fromField) {
		WorkCustomField newField = new WorkCustomField();
		// copy everything except properties managed by Hibernate
		BeanUtils.copyProperties(fromField, newField, "id", "createdOn", "creatorId", "modifiedOn", "modifier", "deleted", "workCustomFieldGroup");
		return newField;
	}

	@Column(name = "name", nullable = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "default_value", nullable = true)
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Column(name = "visible_to_resource_flag")
	public Boolean getVisibleToResourceFlag() {
		return visibleToResourceFlag;
	}

	public void setVisibleToResourceFlag(Boolean visibleToResourceFlag) {
		this.visibleToResourceFlag = visibleToResourceFlag;
	}

	@Column(name = "visible_to_Owner_flag")
	public Boolean getVisibleToOwnerFlag() {
		return visibleToOwnerFlag;
	}

	public void setVisibleToOwnerFlag(Boolean visibleToOwnerFlag) {
		this.visibleToOwnerFlag = visibleToOwnerFlag;
	}

	@Column(name = "required_flag")
	public Boolean getRequiredFlag() {
		return requiredFlag;
	}

	public void setRequiredFlag(Boolean requiredFlag) {
		this.requiredFlag = requiredFlag;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_custom_field_group_id", referencedColumnName = "id", insertable = false, updatable = false)
	public WorkCustomFieldGroup getWorkCustomFieldGroup() {
		return workCustomFieldGroup;
	}

	public void setWorkCustomFieldGroup(WorkCustomFieldGroup workCustomFieldGroup) {
		this.workCustomFieldGroup = workCustomFieldGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_custom_field_type_code", referencedColumnName = "code", nullable = false)
	public WorkCustomFieldType getWorkCustomFieldType() {
		return workCustomFieldType;
	}

	public void setWorkCustomFieldType(WorkCustomFieldType workCustomFieldType) {
		this.workCustomFieldType = workCustomFieldType;
	}

	// Visibility configuration

	@Column(name = "show_on_invoice", nullable = false)
	public Boolean getShowOnInvoice() {
		return showOnInvoice;
	}

	public void setShowOnInvoice(Boolean showInInvoice) {
		this.showOnInvoice = showInInvoice;
	}

	@Column(name = "show_on_dashboard", nullable = false)
	public Boolean getShowOnDashboard() {
		return showOnDashboard;
	}

	public void setShowOnDashboard(Boolean showOnDashboard) {
		this.showOnDashboard = showOnDashboard;
	}

	@Column(name = "show_on_printout", nullable = false)
	public Boolean getShowOnPrintout() {
		return showOnPrintout;
	}

	public void setShowOnPrintout(Boolean showOnPrintout) {
		this.showOnPrintout = showOnPrintout;
	}

	@Column(name = "show_on_sent_status", nullable = false)
	public Boolean getShowOnSentStatus() {
		return showOnSentStatus;
	}

	public void setShowOnSentStatus(Boolean showOnSentStatus) {
		this.showOnSentStatus = showOnSentStatus;
	}

	@Column(name = "show_in_assignment_header", nullable = false)
	public Boolean getShowInAssignmentHeader() {
		return showInAssignmentHeader;
	}

	public void setShowInAssignmentHeader(Boolean showInAssignmentHeader) {
		this.showInAssignmentHeader = showInAssignmentHeader;
	}

	@Column(name = "show_in_assignment_email", nullable = false)
	public Boolean getShowInAssignmentEmail() {
		return showInAssignmentEmail;
	}

	public void setShowInAssignmentEmail(Boolean showInAssignmentEmail) {
		this.showInAssignmentEmail = showInAssignmentEmail;
	}

	@Column(name = "position", nullable = false)
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Transient
	public boolean isDropdown() {
		return WorkCustomField.isDropdown(defaultValue);
	}

	@Transient
	public List<String> getDropdownValues() {
		return WorkCustomField.getDropdownValues(defaultValue);
	}

	@Transient
	public boolean isOwnerType() {
		return WorkCustomFieldType.OWNER.equals(workCustomFieldType.getCode());
	}

	@Transient
	public boolean isResourceType() {
		return WorkCustomFieldType.RESOURCE.equals(workCustomFieldType.getCode());
	}

	@Transient
	public static boolean isDropdown(String value) {
		return StringUtils.contains(value, ",");
	}

	@Transient
	public static List<String> getDropdownValues(String value) {
		return Lists.newArrayList(Splitter.onPattern("\\s*,\\s*").split(value));
	}

	public int compareTo(WorkCustomField value) {
		return this.getPosition().compareTo(value.getPosition());
	}
}
