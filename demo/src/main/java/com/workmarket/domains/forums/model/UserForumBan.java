package com.workmarket.domains.forums.model;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name = "userForumBan")
@Table(name = "user_forum_ban")
@NamedQueries({
		@NamedQuery(name = "userForumBan.getBannedUsersOnHeadPost",
				query = "SELECT ban.bannedUser.id " +
						"FROM forumPost post, userForumBan ban " +
						"WHERE post.id = :postId " +
						"AND ban.bannedUser.id = post.creatorId AND ban.deleted = 0"),
		@NamedQuery(name = "userForumBan.getBannedUsersOnPostReplies",
				query = "SELECT ban.bannedUser.id " +
						"FROM forumPost post, userForumBan ban " +
						"WHERE post.parentId = :postId " +
						"AND ban.bannedUser.id = post.creatorId AND ban.deleted = 0")
})
@AuditChanges
public class UserForumBan extends DeletableEntity {
	private User bannedUser;
	private String reason;

	@Column(name = "reason", nullable = false)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@ManyToOne
	@JoinColumn(name = "banned_user_id", nullable = false)
	public User getBannedUser() {
		return bannedUser;
	}

	public void setBannedUser(User bannedUser) {
		this.bannedUser = bannedUser;
	}
}
