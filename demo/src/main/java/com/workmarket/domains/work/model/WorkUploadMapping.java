package com.workmarket.domains.work.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.customfield.WorkCustomField;

@Entity(name="workUploadMapping")
@Table(name="work_upload_mapping")
public class WorkUploadMapping extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	
	private WorkUploadMappingGroup mappingGroup;
	private WorkUploadColumnType columnType;
	private String columnName;
	private Integer columnIndex;
	private WorkCustomField customField;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mapping_group_id")
	public WorkUploadMappingGroup getMappingGroup() {
		return mappingGroup;
	}
	public void setMappingGroup(WorkUploadMappingGroup mappingGroup) {
		this.mappingGroup = mappingGroup;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "column_type_code", referencedColumnName = "code")
	public WorkUploadColumnType getColumnType() {
		return columnType;
	}
	public void setColumnType(WorkUploadColumnType columnType) {
		this.columnType = columnType;
	}
	
	@Column(name="column_name")
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	@Column(name="column_index")
	public Integer getColumnIndex() {
		return columnIndex;
	}
	public void setColumnIndex(Integer columnIndex) {
		this.columnIndex = columnIndex;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "custom_field_id", updatable = false)
	public WorkCustomField getCustomField() {
		return customField;
	}
	public void setCustomField(WorkCustomField customField) {
		this.customField = customField;
	}
	
	@Transient
	public String getDerivedTypeCode() {
		if (customField != null)
			return columnType.getDerivedCode(customField.getId());
		return columnType.getCode();
	}
}