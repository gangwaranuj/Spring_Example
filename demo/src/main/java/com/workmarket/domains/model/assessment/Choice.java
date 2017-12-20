package com.workmarket.domains.model.assessment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="assessmentItemChoice")
@Table(name="assessment_item_choice")
@AuditChanges
public class Choice extends DeletableEntity {

	private static final long serialVersionUID = 1L;

//	private Integer position;
	private String value;
	private Boolean isCorrect;

//	@Column(name="position", nullable=false)
//	public Integer getPosition() {
//		return position;
//	}
//	public void setPosition(Integer position) {
//		this.position = position;
//	}

	@Column(name="value", nullable=true, length=2048)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Column(name="is_correct", nullable=false, length=1)
	@Type(type="yes_no")
	public Boolean getIsCorrect() {
		return isCorrect;
	}
	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
}
