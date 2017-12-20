package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.DocumentationManager;
import com.workmarket.domains.model.DocumentationVisitor;
import com.workmarket.domains.model.VisitableDocumentation;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;

@Entity(name="userUserGroupDocumentReference")
@Table(name="user_user_group_asset_reference")
@AuditChanges
public class UserUserGroupDocumentReference extends AbstractEntity implements VisitableDocumentation {
	private User user;
	private UserGroup userGroup;
	private Asset requiredDocument;
	private Asset referencedDocument;
	private Calendar expirationDate;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_group_id")
	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}


	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "required_asset_id")
	public Asset getRequiredDocument() {
		return requiredDocument;
	}

	public void setRequiredDocument(Asset requiredDocument) {
		this.requiredDocument = requiredDocument;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "referenced_asset_id")
	public Asset getReferencedDocument() {
		return referencedDocument;
	}

	public void setReferencedDocument(Asset referencedDocument) {
		this.referencedDocument = referencedDocument;
	}

	@Column(name = "expiration_date")
	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Transient
	public boolean hasExpired() {
		return DateUtilities.isInPast(expirationDate);
	}

	@Transient
	public String getExpirationDateString() {
		return DateUtilities.getISO8601(expirationDate);
	}

	@Transient
	@Override
	public void accept(DocumentationManager documentationManager, DocumentationVisitor documentationVisitor) {
		documentationVisitor.visit(documentationManager, this);
	}
}
