package com.workmarket.domains.work.model;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.LookupEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity(name="workResourceLabelType")
@Table(name="work_resource_label_type")
@AttributeOverrides({
	@AttributeOverride(name = "code", column = @Column(length = 20)) })
public class WorkResourceLabelType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static String LATE = "late";
	public static String ABANDONED = "abandoned";
	public static String CANCELLED = "cancelled";
	public static String COMPLETED_ONTIME = "completed_ontime";
	public static String LATE_DELIVERABLE = "late_deliverable";

	public final static List<String> IGNORE_DELIVERABLE_LATE_STATUSES = ImmutableList.of(
			ABANDONED,
			CANCELLED
	);

	private boolean visible = true;

	public WorkResourceLabelType() {}
	public WorkResourceLabelType(String code) {
		super(code);
	}

	@Column(name="visible")
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}