package com.workmarket.domains.work.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.LookupEntity;

@Entity(name = "work_upload_column_type")
@Table(name = "work_upload_column_type")
public class WorkUploadColumnType extends LookupEntity {
	
	public static final String IGNORE_TYPE = "ignore";
	public static final String CUSTOM_FIELD_TYPE = "custom_field";

	private static final long serialVersionUID = 1L;

	private WorkUploadColumnCategory category;
	private Integer order;
	private Boolean visible;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category")
	public WorkUploadColumnCategory getCategory() {
		return category;
	}
	
	public void setCategory(WorkUploadColumnCategory category) {
		this.category = category;
	}
	
	@Column(name="order", nullable=false, insertable=false, updatable=false)
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	@Column(name="visible", nullable=false)
	public Boolean isVisible() {
		return visible;
	}
	
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	
	@Transient
	public String getDerivedCode(Long customFieldId) {
		if (customFieldId != null)
			return String.format("%s:%d", getCode(), customFieldId);
		return getCode();
	}
	
	public static WorkUploadColumnType newInstance(String code) {
		WorkUploadColumnType type = new WorkUploadColumnType();
		type.setCode(code);
		return type;
	}
}
