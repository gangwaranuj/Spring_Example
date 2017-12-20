package com.workmarket.domains.model.customfield;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@AuditChanges
@Entity(name="savedWorkCustomField")
@Table(name="work_custom_field_saved")
public class SavedWorkCustomField extends AuditedEntity implements Comparable<SavedWorkCustomField> {

	private static final long serialVersionUID = 1L;

	private String value;
	private WorkCustomField workCustomField;
	private WorkCustomFieldGroupAssociation workCustomFieldGroupAssociation;

	public SavedWorkCustomField() {}
	public SavedWorkCustomField(WorkCustomField workCustomField, String value) {
		this.workCustomField = workCustomField;
		this.value = value;
	}

	@Column(name="value", nullable=true, length=255)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="work_custom_field_id", nullable=false)
	public WorkCustomField getWorkCustomField() {
		return workCustomField;
	}
	public void setWorkCustomField(WorkCustomField workCustomField) {
		this.workCustomField = workCustomField;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="work_custom_field_group_association_id", nullable=false)
	public WorkCustomFieldGroupAssociation getWorkCustomFieldGroupAssociation() {
		return workCustomFieldGroupAssociation;
	}
	public void setWorkCustomFieldGroupAssociation(WorkCustomFieldGroupAssociation workCustomFieldGroupAssociation) {
		this.workCustomFieldGroupAssociation = workCustomFieldGroupAssociation;
	}

	/**
	 * Saved custom fields are "equal" if they are saved for the same custom field.
	 * Relevant when adding to a set of saved custom fields.
	 * Does this break any Hibernate requirements?
	 */
	// public boolean equals(Object obj) {
	// 	if (!(obj instanceof SavedWorkCustomField))
	// 		return false;
	// 	SavedWorkCustomField e = (SavedWorkCustomField)obj;
	// 	return workCustomField.equals(e.workCustomField);
	// }

	public int compareTo(SavedWorkCustomField field) {
		return this.getWorkCustomField().getPosition().compareTo(field.getWorkCustomField().getPosition());
	}
}
