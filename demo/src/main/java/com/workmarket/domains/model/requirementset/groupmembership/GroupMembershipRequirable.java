package com.workmarket.domains.model.requirementset.groupmembership;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.requirementset.Requirable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ianha on 12/20/13
 */
@Entity
@Table(name = "user_group")
public class GroupMembershipRequirable extends AbstractEntity implements Requirable {
	private String name;

	@Override
	@Column(name = "name", insertable = false, updatable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
