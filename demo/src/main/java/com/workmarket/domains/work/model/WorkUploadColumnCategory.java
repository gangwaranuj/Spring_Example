package com.workmarket.domains.work.model;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.LookupEntity;

import javax.persistence.*;
import java.util.List;

@Entity(name = "work_upload_column_category")
@Table(name = "work_upload_column_category")
public class WorkUploadColumnCategory extends LookupEntity {

	private static final long serialVersionUID = 1L;

	private Integer order;
	private List<WorkUploadColumnType> fieldTypes = Lists.newArrayList();

	@Column(name="order", nullable=false)
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	@OneToMany(mappedBy = "category")
	@OrderColumn(name = "order")
	public List<WorkUploadColumnType> getFieldTypes() {
		return fieldTypes;
	}
	
	public void setFieldTypes(List<WorkUploadColumnType> fieldTypes) {
		this.fieldTypes = fieldTypes;
	}
}
