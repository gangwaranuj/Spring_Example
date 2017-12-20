package com.workmarket.service.business.dto;


import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.utility.BeanUtilities;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class WorkCustomFieldDTO implements Serializable {

	private static final long serialVersionUID = -5851639854172962153L;

	private Long id;
	@NotEmpty
	private String name;
	private String defaultValue;
	private String value;
	private Boolean requiredFlag = Boolean.FALSE;
	private Boolean visibleToResourceFlag = Boolean.FALSE;
	private Boolean visibleToOwnerFlag = Boolean.TRUE;
	private String workCustomFieldTypeCode;
	private Boolean showOnInvoice = Boolean.FALSE;
	private Boolean showOnDashboard = Boolean.FALSE;
	private Boolean showOnPrintout = Boolean.FALSE;
	private Boolean showOnSentStatus = Boolean.FALSE;
	private Boolean showInAssignmentHeader = Boolean.FALSE;
	private Boolean showInAssignmentEmail = Boolean.FALSE;
	private Integer position;
	private Boolean deleted = Boolean.FALSE;

	public WorkCustomFieldDTO() {}

	public WorkCustomFieldDTO (Long fieldId, String value) {
		this.id = fieldId;
		this.value = value;
	}

	public static WorkCustomFieldDTO toDTO(WorkCustomField workCustomField) {
		WorkCustomFieldDTO dto = BeanUtilities.newBean(WorkCustomFieldDTO.class, workCustomField);
		dto.setWorkCustomFieldTypeCode(workCustomField.getWorkCustomFieldType().getCode());
		dto.setShowOnInvoice(workCustomField.getShowOnInvoice());
		return dto;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getRequiredFlag() {
		return requiredFlag;
	}

	public void setRequiredFlag(Boolean requiredFlag) {
		this.requiredFlag = requiredFlag;
	}

	public Boolean getVisibleToResourceFlag() {
		return visibleToResourceFlag;
	}

	public void setVisibleToResourceFlag(Boolean visibleToResourceFlag) {
		this.visibleToResourceFlag = visibleToResourceFlag;
	}

	public Boolean getVisibleToOwnerFlag() {
		return visibleToOwnerFlag;
	}

	public void setVisibleToOwnerFlag(Boolean visibleToOwnerFlag) {
		this.visibleToOwnerFlag = visibleToOwnerFlag;
	}

	public String getWorkCustomFieldTypeCode() {
		return workCustomFieldTypeCode;
	}

	public void setWorkCustomFieldTypeCode(String workCustomFieldTypeCode) {
		this.workCustomFieldTypeCode = workCustomFieldTypeCode;
	}

	public Boolean getShowOnInvoice() {
		return showOnInvoice;
	}

	public void setShowOnInvoice(Boolean showInInvoice) {
		this.showOnInvoice = showInInvoice;
	}

	public Boolean getShowOnDashboard() {
		return showOnDashboard;
	}

	public void setShowOnDashboard(Boolean showOnDashboard) {
		this.showOnDashboard = showOnDashboard;
	}

	public Boolean getShowOnPrintout() {
		return showOnPrintout;
	}

	public void setShowOnPrintout(Boolean showOnPrintout) {
		this.showOnPrintout = showOnPrintout;
	}

	public Boolean getShowOnSentStatus() {
		return showOnSentStatus;
	}

	public void setShowOnSentStatus(Boolean showOnSentStatus) {
		this.showOnSentStatus = showOnSentStatus;
	}

	public Boolean getShowInAssignmentHeader() {
		return showInAssignmentHeader;
	}

	public void setShowInAssignmentHeader(Boolean showInAssignmentHeader) {
		this.showInAssignmentHeader = showInAssignmentHeader;
	}

	public Boolean getShowInAssignmentEmail() {
		return showInAssignmentEmail;
	}

	public void setShowInAssignmentEmail(Boolean showInAssignmentEmail) {
		this.showInAssignmentEmail = showInAssignmentEmail;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
}
