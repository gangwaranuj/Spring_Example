package com.workmarket.domains.authentication.model;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.CompanyUtilities;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * {@link org.springframework.security.core.userdetails.UserDetails} implementation with
 * an extended set of user attributes for use on the Work Market website.
 */
public class ExtendedUserDetails extends User {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String uuid;
	private String userNumber;
	private String firstName;
	private String lastName;
	private String fullName;
	private String smallAvatarUri;
	private String largeAvatarUri;
	private String email;
	private Boolean emailConfirmed;
	private String timeZoneId;
	private String postalCode;
	private String country;
	private String userStatusType;
	private Country taxCountry;

	private Long companyId;
	private String companyNumber;
	private String companyUuid;
	private String companyName;
	private String companyEffectiveName;
	private Boolean companyIsIndividual;
	private Boolean companyIsLocked;
	private Boolean companyIsSuspended;
	private Calendar lockWarningSentOn;
	private Calendar overdueWarningSentOn;

	// custom settings for payments, funds and more
	private Boolean userPaymentAccessBlocked;
	private Boolean userFundsAccessBlocked;

	private Boolean approveWorkCustomAuth;
	private Boolean editPricingCustomAuth;
	private Boolean counterOfferCustomAuth;

	//This is required for weird requirements of bypassing Admin rights
	// and to avoid breaking for existing users without these settings set
	private Boolean hasCustomAccessSettingsSet;

	private boolean manageWork;
	private boolean statementsEnabled;
	private boolean subscriptionEnabled;
	private boolean findWork;
	private boolean inSearch;

	private ExtendedUserDetails masqueradeUser;

	private PersonaPreference personaPreference;
	private String backgroundImageUri;

	// the unix timestamp of when these details were last fetched
	private Long retrievedOn;
	private BigDecimal maxTravelDistance;

	private MboProfile mboProfile;
	private boolean mboServiceType;
	private boolean companyHidesPricing;
	private boolean ssoUser;

	public ExtendedUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		retrievedOn = System.currentTimeMillis();
	}

	public ExtendedUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		retrievedOn = System.currentTimeMillis();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSmallAvatarUri() {
		return smallAvatarUri;
	}

	public void setSmallAvatarUri(String smallAvatarUri) {
		this.smallAvatarUri = smallAvatarUri;
	}

	public String getLargeAvatarUri() {
		return largeAvatarUri;
	}

	public void setLargeAvatarUri(String largeAvatarUri) {
		this.largeAvatarUri = largeAvatarUri;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(Boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	public String getCompanyUuid() {
		return companyUuid;
	}

	public void setCompanyUuid(final String companyUuid) {
		this.companyUuid = companyUuid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyEffectiveName() {
		return companyEffectiveName;
	}

	public void setCompanyEffectiveName(String companyEffectiveName) {
		this.companyEffectiveName = companyEffectiveName;
	}

	public Boolean getCompanyIsIndividual() {
		return companyIsIndividual;
	}

	public void setCompanyIsIndividual(Boolean companyIsIndividual) {
		this.companyIsIndividual = companyIsIndividual;
	}

	public Boolean getCompanyIsLocked() {
		return companyIsLocked;
	}

	public void setCompanyIsLocked(Boolean companyIsLocked) {
		this.companyIsLocked = companyIsLocked;
	}

	public Boolean getCompanyHasLockWarning() {
		return CompanyUtilities.hasLockWarning(getLockWarningSentOn());
	}

	public Calendar getLockWarningSentOn() {
		return lockWarningSentOn;
	}

	public void setLockWarningSentOn(Calendar lockWarningSentOn) {
		this.lockWarningSentOn = lockWarningSentOn;
	}

	public Calendar getOverdueWarningSentOn() {
		return overdueWarningSentOn;
	}

	public void setOverdueWarningSentOn(Calendar overdueWarningSentOn) {
		this.overdueWarningSentOn = overdueWarningSentOn;
	}

	public Boolean getCompanyIsSuspended() {
		return companyIsSuspended;
	}

	public void setCompanyIsSuspended(Boolean companyIsSuspended) {
		this.companyIsSuspended = companyIsSuspended;
	}

	public Boolean getUserPaymentAccessBlocked() {
		return userPaymentAccessBlocked;
	}

	public void setUserPaymentAccessBlocked(Boolean userPaymentAccessBlocked) {
		this.userPaymentAccessBlocked = userPaymentAccessBlocked;
	}

	public Boolean getUserFundsAccessBlocked() {
		return userFundsAccessBlocked;
	}

	public void setUserFundsAccessBlocked(Boolean userFundsAccessBlocked) {
		this.userFundsAccessBlocked = userFundsAccessBlocked;
	}

	public Boolean getApproveWorkCustomAuth() {
		return approveWorkCustomAuth;
	}

	public void setApproveWorkCustomAuth(Boolean approveWorkCustomAuth) {
		this.approveWorkCustomAuth = approveWorkCustomAuth;
	}

	public Boolean getEditPricingCustomAuth() {
		return editPricingCustomAuth;
	}

	public void setEditPricingCustomAuth(Boolean editPricingCustomAuth) {
		this.editPricingCustomAuth = editPricingCustomAuth;
	}

	public Boolean getCounterOfferCustomAuth() {
		return counterOfferCustomAuth;
	}

	public void setCounterOfferCustomAuth(Boolean counterOfferCustomAuth) {
		this.counterOfferCustomAuth = counterOfferCustomAuth;
	}

	public Boolean getHasCustomAccessSettingsSet() {
		return hasCustomAccessSettingsSet;
	}

	public void setHasCustomAccessSettingsSet(Boolean hasCustomAccessSettingsSet) {
		this.hasCustomAccessSettingsSet = hasCustomAccessSettingsSet;
	}

	public boolean isManageWork() {
		return manageWork;
	}

	public void setManageWork(boolean manageWork) {
		this.manageWork = manageWork;
	}

	public boolean isStatementsEnabled() {
		return statementsEnabled;
	}

	public void setStatementsEnabled(boolean statementsEnabled) {
		this.statementsEnabled = statementsEnabled;
	}

	public boolean isSubscriptionEnabled() {
		return subscriptionEnabled;
	}

	public void setSubscriptionEnabled(boolean subscriptionEnabled) {
		this.subscriptionEnabled = subscriptionEnabled;
	}

	public boolean isFindWork() {
		return findWork;
	}

	public void setFindWork(boolean findWork) {
		this.findWork = findWork;
	}

	public boolean isInSearch() {
		return inSearch;
	}

	public void setInSearch(boolean inSearch) {
		this.inSearch = inSearch;
	}

	public boolean isMasquerading() {
		return masqueradeUser != null;
	}

	public ExtendedUserDetails getMasqueradeUser() {
		return masqueradeUser;
	}

	public void setMasqueradeUser(ExtendedUserDetails masqueradeUser) {
		this.masqueradeUser = masqueradeUser;
	}

	public PersonaPreference getPersonaPreference() {
		return personaPreference;
	}

	public void setPersonaPreference(PersonaPreference personaPreference) {
		this.personaPreference = personaPreference;
	}

	public boolean hasAnyRoles(String... roles) {
		return CollectionUtilities.containsAny(getAuthorityNames(), roles);
	}

	public boolean hasAllRoles(String... roles) {
		return CollectionUtilities.containsAll(getAuthorityNames(), roles);
	}

	public boolean isBuyer() {
		if (personaPreference != null) {
			return personaPreference.isBuyer();
		}
		return manageWork || CollectionUtilities.contains(getAuthorityNames(), "ACL_ADMIN", "ACL_MANAGER", "ACL_CONTROLLER", "ACL_USER");
	}

	public boolean isSeller() {
		if (personaPreference != null) {
			return personaPreference.isSeller();
		}

		if (isEmployeeWorker()) {
			return true;
		}

		return !isBuyer() && (findWork || CollectionUtilities.contains(getAuthorityNames(), "ACL_WORKER", "ACL_SHARED_WORKER"));
	}

	public boolean isDispatcher() {
		if (personaPreference != null) {
			return personaPreference.isDispatcher();
		}
		return false;
	}

	public boolean isHybrid() {
		return isBuyer() && isSeller();
	}

	public boolean isEmployeeWorker() {
		return findWork && CollectionUtilities.contains(getAuthorityNames(), "ACL_EMPLOYEE_WORKER");
	}

	public boolean isInternal() {
		return getAuthorityNames().contains("ROLE_INTERNAL");
	}

	public WorkSearchRequestUserType getWorkSearchRequestUserType() {
		return isBuyer() ? WorkSearchRequestUserType.CLIENT : WorkSearchRequestUserType.RESOURCE;
	}

	public Long getMasqueradeUserId() {
		ExtendedUserDetails user = getMasqueradeUser();
		if (user != null) {
			return user.getId();
		}
		return null;
	}

	private List<String> getAuthorityNames() {
		return CollectionUtilities.newListPropertyProjection(getAuthorities(), "authority");
	}

	public Integer getCompanyOverdueWarningDaysBetweenFromNow() {
		return CompanyUtilities.getOverdueWarningDaysBetweenFromNow(getOverdueWarningSentOn());
	}

	public Boolean getCompanyHasOverdueWarning() {
		return CompanyUtilities.hasOverdueWarning(getCompanyOverdueWarningDaysBetweenFromNow());
	}

	public void setBackgroundImageUri(String backgroundImageUri) {
		this.backgroundImageUri = backgroundImageUri;
	}

	public String getBackgroundImageUri() {
		return backgroundImageUri;
	}

	public Long getRetrievedOn() {
		return retrievedOn;
	}

	public void setRetrievedOn(Long retrievedOn) {
		this.retrievedOn = retrievedOn;
	}

	public String getUserStatusType() {
		return userStatusType;
	}

	public void setUserStatusType(String userStatusType) {
		this.userStatusType = userStatusType;
	}

	public boolean isSuspended() {
		return UserStatusType.SUSPENDED.equals(userStatusType);
	}

	public boolean isDeactivated() {
		return UserStatusType.DEACTIVATED.equals(userStatusType);
	}

	public void setMaxTravelDistance(BigDecimal maxTravelDistance) {
		this.maxTravelDistance = maxTravelDistance;
	}

	public BigDecimal getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public boolean isMbo() { return mboProfile != null; }

	public MboProfile getMboProfile() { return mboProfile; }

	public void setMboProfile(MboProfile mboProfile) { this.mboProfile = mboProfile; }

	public boolean isMboServiceType() { return mboServiceType; }

	public void setMboServiceType(boolean mboServiceType) { this.mboServiceType = mboServiceType; }

	public Country getTaxCountry() { return taxCountry;}

	public void setTaxCountry(Country taxCountry) { this.taxCountry = taxCountry;}

	public void setCompanyHidesPricing(boolean companyHidesPricing) {
		this.companyHidesPricing = companyHidesPricing;
	}

	public boolean isCompanyHidesPricing() {
		return companyHidesPricing;
	}

	public boolean isSsoUser() {
		return ssoUser;
	}

	public void setSsoUser(boolean ssoUser) {
		this.ssoUser = ssoUser;
	}

	public boolean isSystemUser() {
		return Constants.WORKMARKET_SYSTEM_USER_ID == id;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}

}
