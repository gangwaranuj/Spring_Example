package com.workmarket.domains.forums.model;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.springframework.util.Assert;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity(name = "forumPostEditHistory")
@Table(name = "forum_post_edit_history")
@AuditChanges
@Access(AccessType.PROPERTY)
public class ForumPostEditHistory extends DeletableEntity {
	private String title;
	private String comment;
	private Long postId;
	private Long parentId;
	private Long categoryId;

	public ForumPostEditHistory(ForumPost post, String oldTitle, String oldComment) {
		Assert.notNull(post);
		this.title = oldTitle;
		this.comment = oldComment;
		this.postId = post.getId();
		this.parentId = post.getParentId();
		this.categoryId = post.getCategoryId();
	}

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

	@Column(name = "parent_id")
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Column(name = "category_id", nullable = false)
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Column(name = "post_id")
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long parentId) {
		this.postId = parentId;
	}

}
