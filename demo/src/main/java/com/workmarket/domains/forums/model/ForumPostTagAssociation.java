package com.workmarket.domains.forums.model;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "forumTagAssociation")
@Table(name = "forum_post_tag_association")

@AuditChanges
public class ForumPostTagAssociation extends DeletableEntity {
	private static final long serialVersionUID = 1L;
	private ForumPost post;
	private ForumTag tag;

	public ForumPostTagAssociation() {
		super();
	}

	public ForumPostTagAssociation(ForumPost post, ForumTag tag) {
		this.post = post;
		this.tag = tag;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "post_id", nullable = false)
	public ForumPost getForumPost() {
		return post;
	}

	public void setForumPost(ForumPost post) {
		this.post = post;
	}
	@Enumerated(EnumType.STRING)
	@Column(name = "tag", nullable=false)
	public ForumTag getTag() {
		return tag;
	}

	public void setTag(ForumTag tag) {
		this.tag = tag;
	}
}
