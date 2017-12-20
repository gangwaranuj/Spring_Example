package com.workmarket.domains.model.note;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="note")
@Table(name="note")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="note_type", discriminatorType= DiscriminatorType.STRING)
@DiscriminatorValue("baseNote")
@AuditChanges
public class Note extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String content;
	private PrivacyType privacy = PrivacyType.PUBLIC;
	private Long replyToId;

	//Transient members
	private Long parentId;
	private boolean parent;
	private String onBehalfUserNumber;
	private String onBehalfFirstName;
	private String onBehalfLastName;


	public Note() { }
	public Note(String content) {
		this.content = content;
	}

	@Column(name = "note_content", nullable = false, length = Constants.TEXT_LONG)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "privacy_type", nullable = false)
	public PrivacyType getPrivacy() {
		return privacy;
	}

	public void setPrivacy(PrivacyType privacy) {
		this.privacy = privacy;
	}

	//The reply to id, in the case of work notes is the active resource at the time when the note ws sent.
	@Column(name = "reply_to_id")
	public Long getReplyToId() {
		return replyToId;
	}

	public void setReplyToId(Long replyToId) {
		this.replyToId = replyToId;
	}

	@Transient
	public Boolean getIsPrivate() {
		return privacy.isPrivate();
	}

	@Transient
	public Boolean getIsPrivileged() {
		return privacy.isPrivileged();
	}

	@Transient
	public Boolean getIsPublic() {
		return privacy.isPublic();
	}

	@Transient
	public void setIsPrivate(Boolean isPrivate) {
		privacy = (isPrivate) ? PrivacyType.PRIVATE : PrivacyType.PUBLIC;
	}

	@Transient
	public Long getParentId() {
		return parentId;
	}

	@Transient
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Transient
	public boolean isParent() {
		return parent;
	}

	@Transient
	public void setParent(boolean parent) {
		this.parent = parent;
	}

	@Transient
	public String getOnBehalfFirstName() {
		return onBehalfFirstName;
	}

	@Transient
	public void setOnBehalfFirstName(String onBehalfFirstName) {
		this.onBehalfFirstName = onBehalfFirstName;
	}

	@Transient
	public String getOnBehalfLastName() {
		return onBehalfLastName;
	}

	@Transient
	public void setOnBehalfLastName(String onBehalfLastName) {
		this.onBehalfLastName = onBehalfLastName;
	}

	@Transient
	public String getOnBehalfUserNumber() {
		return onBehalfUserNumber;
	}

	@Transient
	public void setOnBehalfUserNumber(String onBehalfUserNumber) {
		this.onBehalfUserNumber = onBehalfUserNumber;
	}
}
