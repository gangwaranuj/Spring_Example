package com.workmarket.domains.model.feedback;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity(name="FeedbackUserGroupAssociation")
@Table(name="feedback_user_group_association")
public class FeedbackUserGroupAssociation extends LookupEntity {

	private static final long serialVersionUID = 1818612635087787909L;

	private Long userGroupId;

	public FeedbackUserGroupAssociation () {}

	private FeedbackUserGroupAssociation(String code) {
		super(code);
	}

	private static FeedbackUserGroupAssociation newInstance(String code) {
		return new FeedbackUserGroupAssociation(code);
	}

	@Column(name = "user_group_id")
	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}
}
