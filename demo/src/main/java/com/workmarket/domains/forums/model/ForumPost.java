package com.workmarket.domains.forums.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.springframework.util.Assert;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;


@Entity(name = "forumPost")
@Table(name = "forum_post")
@AuditChanges
@Access(AccessType.PROPERTY)
public class ForumPost extends DeletableEntity {
	private static final long serialVersionUID = 1103318382114394498L;
	private String title;
	private String comment;
	private Long parentId;
	private Long rootId;
	private Long categoryId;
	private Set<ForumPostFollower> forumPostFollowers;
	private Set<ForumPostTagAssociation> forumPostTags;
	private boolean isFlagged;
	private boolean isEdited;
	private Calendar lastPastOn;
	private boolean isAdmin;

	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "comment", nullable = false)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "category_id", nullable = false)
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "parent_id")
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Column(name = "root_id")
	public Long getRootId() {
		return rootId;
	}

	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}

	@Column(name = "is_flagged", nullable = false)
	public boolean getFlagged() {
		return isFlagged;
	}

	public void setFlagged(boolean flagged) {
		isFlagged = flagged;
	}

	@Column(name = "is_edited", nullable = false)
	public boolean getEdited() {
		return isEdited;
	}

	public void setEdited(boolean edited) {
		isEdited = edited;
	}

	@Transient
	public boolean isReply() {
		return parentId != null;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "post_id", referencedColumnName = "id")
	public Set<ForumPostFollower> getForumPostFollowers() { return forumPostFollowers; }

	public void setForumPostFollowers(Set<ForumPostFollower> forumPostFollowers) { this.forumPostFollowers = forumPostFollowers; }

	@OneToMany(fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "post_id", referencedColumnName = "id")
	public Set<ForumPostTagAssociation> getForumPostTags() { return forumPostTags; }

	public void setForumPostTags(Set<ForumPostTagAssociation> forumPostTags) { this.forumPostTags = forumPostTags; }

	@Column(name = "last_post_on")
	public Calendar getLastPostOn() {
		return this.lastPastOn;
	}

	public void setLastPostOn(Calendar lastPostOn) {
		this.lastPastOn = lastPostOn;
	}

	@Column(name = "is_admin")
	public boolean isAdmin() {
		return this.isAdmin;
	}

	public void setAdmin(boolean admin) {
		this.isAdmin = admin;
	}

	/*RULES:
	A reply to the main post will be referred to as a comment. A reply to a comment will be referred to as a reply.
	//1. If both posts have the same root and parent that means they are either both comments to the same post, or replies to the same comment. Then, ordering is just by determining which was posted first, comparing ID's.
	//2 + 3. If one is a reply (root is the parent), and the other is a comment (root is not the parent), then return the eldest of the comment vs the reply's parent.
	//4. Else, then both are replies, and whichever one goes first is determined by whoever has the eldest parent.
	*/
	public static final Ordering<ForumPost> THREAD_ORDER = new Ordering<ForumPost>() {
		@Override
		public int compare(ForumPost f1, ForumPost f2){
			Long root1 = f1.getRootId();
			Long root2 = f2.getRootId();
			Long parent1 = f1.getParentId();
			Long parent2 = f2.getParentId();
			Long id1 = f1.getId();
			Long id2 = f2.getId();

			if (root1.equals(root2) && parent1.equals(parent2)) {
				return (id1 < id2 ? -1 : 1);
			}
			if (root1.equals(parent1) && (!root2.equals(parent2))) {
				return (id1 < parent2 ? -1 : 1);
			}
			if ((!root1.equals(parent1)) && root2.equals(parent2)) {
				return (parent1 < id2 ? -1 : 1);
			}
			return (parent1 < parent2 ? -1 : 1);
		}
	};

	/**
	 * Convert this object into a Map<String, Object>. This is intended for sending as JSON,
	 * for example for notifications.
	 * @return
	 */
	public Map<String, Object> toStringObjectMap() {
		Assert.isTrue(
			(this.getRootId() == null) != (this.getTitle() == null),
			"For the initial forum post, forumPost.getRootId() should be null, and for replies to a post, " +
				"forumPost.getTitle() should be null. So because each ForumPost represents either a " +
				"initial post or a reply, precisely one of the rootId and Title should be null.");
		return ImmutableMap.<String, Object>of(
			"id", this.getId(),
			"rootId", this.getRootId() == null ? "": this.getRootId(),
			"title", this.getTitle() == null ? "" : this.getTitle(),
			"comment", this.getComment());
	}
}
