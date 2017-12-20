package com.workmarket.domains.model.tag;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "userTagAssociation")
@Table(name = "user_tag_association")
@NamedQueries({
		@NamedQuery(name = "UserTagAssociation.findByIds",
				query = "select a from userTagAssociation a where a.user.id = :userId and a.tag.id = :tagId"),
		@NamedQuery(name = "UserTagAssociation.findAllTagAssociations",
				query = "select a from userTagAssociation a where a.user.id = :userId"),
		@NamedQuery(name = "UserTagAssociation.findAllActiveUserTagAssociationsOWNER",
				query = "select a from userTagAssociation a where a.tag.class = com.workmarket.domains.model.tag.UserTag and a.user.id = :userId and a.deleted = false and a.tag.deleted = false"),
		@NamedQuery(name = "UserTagAssociation.findAllActiveUserTagAssociationsPUBLIC",
				query = "select a from userTagAssociation a where a.tag.class = com.workmarket.domains.model.tag.UserTag and a.user.id = :userId and a.deleted = false and a.tag.deleted = false and a.tag.approvalStatus = 'APPROVED'"),
		@NamedQuery(name = "ProductTagAssociation.findAllActiveProductTagAssociationsOWNER",
				query = "select a from userTagAssociation a where a.tag.class = com.workmarket.domains.model.tag.ProductTag and a.user.id = :userId and a.deleted = false and a.tag.deleted = false"),
		@NamedQuery(name = "ProductTagAssociation.findAllActiveProductTagAssociationsPUBLIC",
				query = "select a from userTagAssociation a where a.tag.class = com.workmarket.domains.model.tag.ProductTag and a.user.id = :userId and a.deleted = false and a.tag.deleted = false and a.tag.approvalStatus = 'APPROVED'"),
		@NamedQuery(name = "UserTagAssociation.findAllActiveUserTagAssociations",
				query = "select a from userTagAssociation a where a.tag.class = com.workmarket.domains.model.tag.UserTag and a.user.id = :userId and a.tag.deleted = false"),
		@NamedQuery(name = "UserTagAssociation.findAllUserTags",
				query = "select a.tag from userTagAssociation a where a.tag.class = com.workmarket.domains.model.tag.UserTag and a.tag.deleted = false and a.tag.approvalStatus = 'APPROVED'") })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("UT")
@AuditChanges
public class UserTagAssociation extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private User user;

	private Tag tag;

	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "tag_id")
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
