package com.workmarket.domains.model.tool;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "userToolAssociation")
@Table(name = "user_tool_association")
@AuditChanges
public class UserToolAssociation extends VerifiableEntity {

	private static final long serialVersionUID = 1L;

	private User user;
	private Tool tool;
	private Integer proficiencyLevel = 0;

	public UserToolAssociation() {
	}

	public UserToolAssociation(User user, Tool tool) {
		this.user = user;
		this.tool = tool;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "tool_id", updatable = false)
	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

	@Column(name = "proficiency_level", nullable = false, unique = false)
	public Integer getProficiencyLevel() {
		return proficiencyLevel;
	}

	public void setProficiencyLevel(Integer proficiencyLevel) {
		this.proficiencyLevel = proficiencyLevel;
	}
}
