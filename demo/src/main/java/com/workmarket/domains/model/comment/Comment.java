package com.workmarket.domains.model.comment;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "comment")
@Table(name = "comment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("C")
@AuditChanges
public class Comment extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = Constants.TEXT_MIN_LENGTH, max = Constants.TEXT_MAX_LENGTH)
	private String comment;

	@Column(name = "comment", nullable = false, length = Constants.TYPE_MAX_LENGTH)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
