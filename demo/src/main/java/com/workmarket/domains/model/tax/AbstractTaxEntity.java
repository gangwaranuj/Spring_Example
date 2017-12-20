package com.workmarket.domains.model.tax;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.TaxEntityAssetAssociation;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.models.Securable;
import com.workmarket.vault.models.Secured;
import com.workmarket.vault.models.Vaultable;
import com.workmarket.vault.models.Vaulted;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Set;

@Entity(name = "tax_entity")
@Table(name = "tax_entity")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "country", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(AbstractTaxEntity.COUNTRY_BASE)
@Vaultable
@Securable
public abstract class AbstractTaxEntity extends AbstractEntity {
	public static final String COUNTRY_USA = "usa";
	public static final String COUNTRY_CANADA = "canada";
	public static final String COUNTRY_OTHER = "other";
	public static final String COUNTRY_BASE = "base";

	protected String taxName;
	protected Boolean businessFlag;
	protected String firstName;
	protected String middleName;
	protected String lastName;
	@Vaulted @Secured
	protected String taxNumber;             // this can be an EIN, SSN, SIN, or other foreign id

	// denormalized to allow foreign
	private String address;
	private String city;
	private String state;
	private String postalCode;
	private String country;

	protected TaxEntityType taxEntityType;

	protected Boolean activeFlag;
	protected Calendar activeDate;
	protected Calendar inactiveDate;
	protected Company company;
	protected Set<TaxEntityAssetAssociation> assetAssociations = Sets.newLinkedHashSet();

	protected Calendar signedOn;
	protected User signedBy;

	protected TaxVerificationStatusType status = new TaxVerificationStatusType(TaxVerificationStatusType.UNVERIFIED);

	protected Boolean verificationPending = Boolean.FALSE;
	protected Boolean deliveryPolicyFlag = Boolean.TRUE;
	protected boolean businessNameFlag = false;
	protected String businessName;
	protected String phoneNumber;

	private static final long serialVersionUID = 1479365233652897615L;

	@Transient
	public abstract Calendar getEffectiveDate();

	@Column(name = "business_flag", nullable = false)
	public Boolean getBusinessFlag() {
		return businessFlag;
	}

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "tax_entity_type_code", referencedColumnName = "code", nullable = false)
	public TaxEntityType getTaxEntityType() {
		return taxEntityType;
	}

	@Column(name = "tax_name", nullable = false, length = 100)
	public String getTaxName() {
		return taxName;
	}

	@Column(name = "tax_number", nullable = false, length = 9)
	public String getTaxNumber() {
		return taxNumber;
	}

	@Column(name = "address", nullable = false, length = 100)
	public String getAddress() {
		return address;
	}

	@Column(name = "city", nullable = true, length = 64)
	public String getCity() {
		return city;
	}

	@Column(name = "state", nullable = true, length = 64)
	public String getState() {
		return state;
	}

	@Column(name = "postal_code", nullable = true, length = 9)
	public String getPostalCode() {
		return postalCode;
	}

	@Column(name = "active_flag", nullable = false, length = 1)
	@Type(type = "yes_no")
	public Boolean getActiveFlag() {
		return activeFlag;
	}

	@Column(name = "active_date", nullable = false)
	public Calendar getActiveDate() {
		return activeDate;
	}

	@Column(name = "inactive_date", nullable = true)
	public Calendar getInactiveDate() {
		return inactiveDate;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setBusinessFlag(Boolean businessFlag) {
		this.businessFlag = businessFlag;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setTaxEntityType(TaxEntityType taxEntityType) {
		this.taxEntityType = taxEntityType;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public void setActiveDate(Calendar activeDate) {
		this.activeDate = activeDate;
	}

	public void setInactiveDate(Calendar inactiveDate) {
		this.inactiveDate = inactiveDate;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToMany
	@JoinColumn(name = "entity_id")
	public Set<TaxEntityAssetAssociation> getAssetAssociations() {
		return assetAssociations;
	}

	public void setAssetAssociations(Set<TaxEntityAssetAssociation> assetAssociations) {
		this.assetAssociations = assetAssociations;
	}

	@Column(name = "signed_on", nullable = true)
	public Calendar getSignedOn() {
		return signedOn;
	}

	public void setSignedOn(Calendar signedOn) {
		this.signedOn = signedOn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "signed_by", referencedColumnName = "id")
	public User getSignedBy() {
		return signedBy;
	}

	public void setSignedBy(User signedBy) {
		this.signedBy = signedBy;
	}

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "status", referencedColumnName = "code", nullable = false)
	public TaxVerificationStatusType getStatus() {
		return status;
	}

	public void setStatus(TaxVerificationStatusType status) {
		this.status = status;
	}

	@Column(name = "verification_pending", nullable = false)
	public Boolean getVerificationPending() {
		return verificationPending;
	}

	public void setVerificationPending(Boolean verificationPending) {
		this.verificationPending = verificationPending;
	}

	@Column(name = "delivery_policy_flag", nullable = false)
	public Boolean getDeliveryPolicyFlag() {
		return deliveryPolicyFlag;
	}

	public void setDeliveryPolicyFlag(Boolean deliveryPolicyFlag) {
		this.deliveryPolicyFlag = deliveryPolicyFlag;
	}

	@Column(name = "first_name")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "middle_name")
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Column(name = "last_name")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "business_name_flag")
	public boolean isBusinessNameFlag() {
		return businessNameFlag;
	}

	public void setBusinessNameFlag(boolean businessNameFlag) {
		this.businessNameFlag = businessNameFlag;
	}

	@Column(name = "business_name", nullable = true)
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	@Column(name = "phone_number", nullable = true)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Transient
	public static String getCountryFromCountryId(String iso) {
		if (StringUtilities.equalsAnyIgnoreCase(iso, Country.USA, Country.US)) {
			return COUNTRY_USA;
		}
		// normalize US territory country codes to USA for tax purposes
		if (StringUtilities.equalsAnyIgnoreCase(iso, UsaTaxEntity.US_TAX_COUNTRY_CODES)) {
			return COUNTRY_USA;
		}
		if (Country.CANADA.equals(iso)) {
			return COUNTRY_CANADA;
		}
		return COUNTRY_OTHER;
	}

	@Transient
	public abstract String getCountry();

	@Transient
	public abstract Country getIsoCountry();

	@Transient
	public abstract String getFormattedTaxNumber();

	@Transient
	public String getFormattedTaxNumberForForm1099() {
		//Only USA needs a special format
		return getFormattedTaxNumber();
	}

	@Transient
	public String getSecureFormattedTaxNumber() {
		return getFormattedTaxNumber();
	}

	@Transient
	public abstract String getSecureTaxNumber();

	@Transient
	public abstract String getRawTaxNumber();

	/**
	 * Return the tax number without the prefix obfuscation value, if there is one. The prefix is added as a temporary
	 * obfuscation marker before full obfuscation, so this method will only be needed temporarily.
	 *
	 * @return tax number without the obfuscation prefix
	 */
	@Transient
	public String getTaxNumberSanitized() {
		return StringUtilities.removePrepend(taxNumber);
	}
}
