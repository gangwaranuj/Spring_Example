package com.workmarket.domains.forums.model;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "forumPostFollower")
@Table(name = "forum_post_follower")
@Access(AccessType.PROPERTY)
@AuditChanges
public class ForumPostFollower extends DeletableEntity {

	private ForumPost post;
	private User followerUser;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", updatable = false, nullable = false)
	public ForumPost getPost() {
		return post;
	}

	public void setPost(ForumPost post) {
		this.post = post;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "follower_id", referencedColumnName = "id", updatable = false, nullable = false)
	public User getFollowerUser() {
		return followerUser;
	}

	public void setFollowerUser(User follower) {
		this.followerUser = follower;
	}

	@Transient
	public boolean isFollowing(){
		return !this.getDeleted();
	}
}
