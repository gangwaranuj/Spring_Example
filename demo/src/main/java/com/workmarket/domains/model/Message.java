package com.workmarket.domains.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="message")
@Table(name="message")/*
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="note_type", discriminatorType= DiscriminatorType.INTEGER)
@DiscriminatorValue("1")*/
@NamedQueries({

})
@AuditChanges
public class Message extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String content;
	private String subject;
	private User sender;
	private Set<UserGroup> userGroups = new HashSet<UserGroup>();

	@Column(name = "content", nullable = false, length = Constants.TEXT_MAX_LENGTH)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "subject", nullable = false, length = Constants.TEXT_MAX_LENGTH)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_user_id", referencedColumnName = "id")
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_group_message_association",
	           joinColumns = { @JoinColumn(name = "message_id") },
	    inverseJoinColumns = { @JoinColumn(name = "user_group_id") })
	public Set<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
}


