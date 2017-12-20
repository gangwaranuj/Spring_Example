package com.workmarket.domains.model.insurance;

import java.util.Calendar;
import java.util.HashSet;
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

import com.workmarket.domains.model.Expirable;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.DocumentationManager;
import com.workmarket.domains.model.DocumentationVisitor;
import com.workmarket.domains.model.VisitableDocumentation;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="userInsuranceAssociation")
@Table(name="user_insurance_association")
@NamedQueries({
	@NamedQuery(name="userInsuranceAssociation.findAssociationByUserAndInsurance", query="from userInsuranceAssociation a where a.insurance.id = :insurance_id and a.user.id = :user_id")
})
@AuditChanges
public class UserInsuranceAssociation extends VerifiableEntity implements Expirable, VisitableDocumentation {

	private static final long serialVersionUID = 1L;

	private User user;
	private Insurance insurance;
	private String provider;
	private String policyNumber;
	private String coverage;

	private Calendar issueDate;
	private Calendar expirationDate;
	private Set<Asset> assets = new HashSet<Asset>();
	private Calendar lastActivityOn;

	private int buyerNotifiedOnExpiry;
	private int resourceNotifiedOnExpiry;
	private boolean notApplicableOverride;

	public UserInsuranceAssociation() {}

	public UserInsuranceAssociation(User user, Insurance insurance, String provider) {
		this.user = user;
		this.insurance = insurance;
		this.provider = provider;
	}

	public UserInsuranceAssociation(User user, Insurance insurance) {
		this.user = user;
		this.insurance = insurance;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="user_id", updatable = false)
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="insurance_id", updatable = false)
	public Insurance getInsurance() {
		return insurance;
	}
	public void setInsurance(Insurance insurance) {
		this.insurance = insurance;
	}

	@OneToMany
	@JoinTable(name="user_insurance_asset_association",
	           joinColumns=@JoinColumn(name="user_insurance_association_id"),
	           inverseJoinColumns=@JoinColumn(name="asset_id"))
	public Set<Asset> getAssets() {
		return assets;
	}
	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}

	@Column(name="provider", nullable=false, length=50)
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}

	@Column(name="policy_number", nullable=false, length=50)
	public String getPolicyNumber() {
		return policyNumber;
	}
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	@Column(name="coverage", nullable=false, length=50)
	public String getCoverage() {
		return coverage;
	}
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	@Column(name="issue_date", nullable=true)
	public Calendar getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
	}

	@Column(name="expiration_date", nullable=true)
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

	@Column(name="resource_notified_on_expiry")
	public int getResourceNotifiedOnExpiry() {
		return resourceNotifiedOnExpiry;
	}

	public void setResourceNotifiedOnExpiry(int resourceNotifiedOnExpiry) {
		this.resourceNotifiedOnExpiry = resourceNotifiedOnExpiry;
	}

	@Column(name="buyer_notified_on_expiry")
	public int getBuyerNotifiedOnExpiry() {
		return buyerNotifiedOnExpiry;
	}

	public void setBuyerNotifiedOnExpiry(int buyerNotifiedOnExpiry) {
		this.buyerNotifiedOnExpiry = buyerNotifiedOnExpiry;
	}

	@Column(name="not_applicable_override")
	public boolean isNotApplicableOverride() {
		return notApplicableOverride;
	}

	public void setNotApplicableOverride(boolean notApplicableOverride) {
		this.notApplicableOverride = notApplicableOverride;
	}

	@Transient
	@Override
	public void accept(DocumentationManager documentationManager, DocumentationVisitor documentationVisitor) {
		documentationVisitor.visit(documentationManager, this);
	}
}
