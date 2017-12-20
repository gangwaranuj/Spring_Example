package com.workmarket.domains.model.note.concern;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="userGroupConcern")
@DiscriminatorValue("group")
@AuditChanges
public class UserGroupConcern extends Concern {

	private static final long serialVersionUID = 1L;

	private UserGroup group;

	public UserGroupConcern() {
		super();
	}

	public UserGroupConcern(String message, UserGroup group) {
		super(message);
		this.setGroup(group);
	}

	public void setGroup(UserGroup group) {
		this.group = group;
	}

	@ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="user_group_id", referencedColumnName="id")
	public UserGroup getGroup() {
		return group;
	}

	@Transient
	public String getType() {
		return "group";
	}

	@Override
	@Transient
	public Long getEntityId() {
		return group.getId();
	}

	@Override
	@Transient
	public String getEntityNumber() {
		return group.getId().toString();
	}
}
