package com.workmarket.domains.model.license;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Expirable;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.DocumentationManager;
import com.workmarket.domains.model.DocumentationVisitor;
import com.workmarket.domains.model.VisitableDocumentation;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="userLicenseAssociation")
@Table(name="user_license_association")
@NamedQueries({
		@NamedQuery(name="userLicenseAssociation.findAssociationByLicenseIdAndUserId", query="from userLicenseAssociation e where e.license.id = :licenseId and e.user.id = :userId")
})
@AuditChanges
public class UserLicenseAssociation extends VerifiableEntity implements Expirable, VisitableDocumentation {

	private static final long serialVersionUID = 1L;

	private User user;
	private License license;
	private String licenseNumber;
	private Calendar issueDate;
	private Calendar expirationDate;
	private Set<Asset> assets = Sets.newLinkedHashSet();
	private Calendar lastActivityOn;
	private int buyerNotifiedOnExpiry;
	private int resourceNotifiedOnExpiry;

	public UserLicenseAssociation() { }

	public UserLicenseAssociation(User user, License license, String licenseNumber) {
		this.user = user;
		this.license = license;
		this.licenseNumber = licenseNumber;
	}

	public UserLicenseAssociation(User user, License license) {
		this.user = user;
		this.license = license;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "license_id")
	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	@OneToMany
	@JoinTable(name = "user_license_asset_association",
			joinColumns = @JoinColumn(name = "user_license_association_id"),
			inverseJoinColumns = @JoinColumn(name = "asset_id"))
	public Set<Asset> getAssets() {
		return assets;
	}

	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}

	@Column(name = "license_number", length = 50)
	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	@Column(name = "issue_date", nullable = true)
	public Calendar getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
	}

	@Column(name = "expiration_date", nullable = true)
	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Column(name="last_activity_on")
	public Calendar getLastActivityOn() {
		return lastActivityOn;
	}

	public void setLastActivityOn(Calendar lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}

	@Column(name="buyer_notified_on_expiry")
	public int getBuyerNotifiedOnExpiry() {
		return buyerNotifiedOnExpiry;
	}

	public void setBuyerNotifiedOnExpiry(int buyerNotifiedOnExpiry) {
		this.buyerNotifiedOnExpiry = buyerNotifiedOnExpiry;
	}

	@Column(name="resource_notified_on_expiry")
	public int getResourceNotifiedOnExpiry() {
		return resourceNotifiedOnExpiry;
	}

	public void setResourceNotifiedOnExpiry(int resourceNotifiedOnExpiry) {
		this.resourceNotifiedOnExpiry = resourceNotifiedOnExpiry;
	}

	@Transient
	@Override
	public void accept(DocumentationManager documentationManager, DocumentationVisitor documentationVisitor) {
		documentationVisitor.visit(documentationManager, this);
	}
}
