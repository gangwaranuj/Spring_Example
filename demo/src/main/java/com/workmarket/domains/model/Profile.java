package com.workmarket.domains.model;

import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Set;

@Entity(name = "profile")
@Table(name = "profile")
@NamedQueries({
	@NamedQuery(name = "profile.byuser", query = "from profile where user.id = :userid")
})
public class Profile extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	private User user;
	private String jobTitle;

	private CallingCode workPhoneInternationalCode;
	private String workPhone;
	private String workPhoneExtension;
	private CallingCode mobilePhoneInternationalCode;
	private String mobilePhone;
	private CallingCode smsPhoneInternationalCode;
	private String smsPhone;

	// The purpose of the old value columns is to be able to highlight the updated values while approving/rejecting a Profile by svc
	private String workPhoneOldValue;
	private String workPhoneExtensionOldValue;
	private String mobilePhoneOldValue;
	private String smsPhoneOldValue;

	private MobileProvider mobileProvider = MobileProvider.ATT;
	private String smsPhoneVerificationCode;
	private Calendar smsPhoneVerificationCodeExpiration;
	private Boolean smsPhoneVerified = Boolean.FALSE;

	private Long addressId;

	private BigDecimal maxTravelDistance = Constants.DEFAULT_MAX_TRAVEL_DISTANCE;
	private BigDecimal minOnsiteHourlyRate = BigDecimal.ZERO;
	private BigDecimal minOnsiteWorkPrice = BigDecimal.ZERO;
	private BigDecimal minOffsiteHourlyRate = BigDecimal.ZERO;
	private BigDecimal minOffsiteWorkPrice = BigDecimal.ZERO;

	private String linkedInId;
	private String overview;
	private String overviewOldValue;
	private Boolean onboardCompleted = Boolean.TRUE;
	private PostalCode profilePostalCode;
	private Boolean manageWork = Boolean.FALSE;
	private Boolean findWork = Boolean.TRUE;
	private Boolean csrOpen = Boolean.TRUE;
	private Integer sessionDuration = Constants.DEFAULT_SESSION_DURATION_IN_MINUTES;
	private TimeZone timeZone;
	private Set<PostalCode> blacklistedPostalCodes = Sets.newLinkedHashSet();
	private YearsOfExperienceEnum yearsOfExperienceEnum;
	private Gender gender;

	private Calendar onboardCompletedOn;
	private Calendar onboardVideoWatchedOn;

	@Column(name = "onboard_video_watched_on")
	public Calendar getOnboardVideoWatchedOn() {
		return onboardVideoWatchedOn;
	}

	public void setOnboardVideoWatchedOn(Calendar onboardVideoWatchedOn) {
		this.onboardVideoWatchedOn = onboardVideoWatchedOn;
	}

	@Column(name = "onboard_completed_on")
	public Calendar getOnboardCompletedOn() {
		return onboardCompletedOn;
	}

	public void setOnboardCompletedOn(Calendar onboardCompletedOn) {
		this.onboardCompletedOn = onboardCompletedOn;
	}

	@Transient
	public boolean isWorkPhoneInternationalCodeSet() {
		return workPhoneInternationalCode != null;
	}

	@Transient
	public boolean isMobilePhoneInternationalCodeSet() {
		return mobilePhoneInternationalCode != null;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "smsPhone_international_id", referencedColumnName = "id", nullable = true)
	public CallingCode getSmsPhoneInternationalCode() {
		return smsPhoneInternationalCode;
	}

	public void setSmsPhoneInternationalCode(CallingCode smsPhoneInternationalCode) {
		this.smsPhoneInternationalCode = smsPhoneInternationalCode;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "workPhone_international_id", referencedColumnName = "id", nullable = true)
	public CallingCode getWorkPhoneInternationalCode() {
		return workPhoneInternationalCode;
	}

	public void setWorkPhoneInternationalCode(CallingCode workPhoneInternationalCode) {
		this.workPhoneInternationalCode = workPhoneInternationalCode;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "mobilePhone_international_id", referencedColumnName = "id", nullable = true)
	public CallingCode getMobilePhoneInternationalCode() {
		return mobilePhoneInternationalCode;
	}

	public void setMobilePhoneInternationalCode(CallingCode mobilePhoneInternationalCode) {
		this.mobilePhoneInternationalCode = mobilePhoneInternationalCode;
	}

	@Fetch(FetchMode.JOIN)
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "job_title", nullable = true, length = 45)
	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", nullable = true)
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Column(name = "work_phone", nullable = true, length = 15)
	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	@Column(name = "work_phone_extension", nullable = true, length = 15)
	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	@Column(name = "mobile_phone", nullable = true, length = 15)
	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	@Column(name = "address_id", nullable = true, length = 11)
	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	@Column(name = "work_phone_old_value", nullable = true, length = 15)
	public String getWorkPhoneOldValue() {
		return workPhoneOldValue;
	}

	public void setWorkPhoneOldValue(String workPhoneOldValue) {
		this.workPhoneOldValue = workPhoneOldValue;
	}

	@Column(name = "work_phone_extension_old_value", nullable = true, length = 15)
	public String getWorkPhoneExtensionOldValue() {
		return workPhoneExtensionOldValue;
	}

	public void setWorkPhoneExtensionOldValue(String workPhoneExtensionOldValue) {
		this.workPhoneExtensionOldValue = workPhoneExtensionOldValue;
	}

	@Column(name = "mobile_phone_old_value", nullable = true, length = 15)
	public String getMobilePhoneOldValue() {
		return mobilePhoneOldValue;
	}

	public void setMobilePhoneOldValue(String mobilePhoneOldValue) {
		this.mobilePhoneOldValue = mobilePhoneOldValue;
	}

	@Column(name = "sms_phone_old_value", nullable = true, length = 15)
	public String getSmsPhoneOldValue() {
		return smsPhoneOldValue;
	}

	public void setSmsPhoneOldValue(String smsPhoneOldValue) {
		this.smsPhoneOldValue = smsPhoneOldValue;
	}

	@Column(name = "max_travel_distance", nullable = false)
	public BigDecimal getMaxTravelDistance() {
		return maxTravelDistance;
	}

	public void setMaxTravelDistance(BigDecimal maxTravelDistance) {
		this.maxTravelDistance = maxTravelDistance;
	}

	@Column(name = "min_onsite_hourly_rate", nullable = false)
	public BigDecimal getMinOnsiteHourlyRate() {
		return minOnsiteHourlyRate;
	}

	public void setMinOnsiteHourlyRate(BigDecimal minOnsiteHourlyRate) {
		this.minOnsiteHourlyRate = minOnsiteHourlyRate;
	}

	@Column(name = "min_onsite_work_price", nullable = false)
	public BigDecimal getMinOnsiteWorkPrice() {
		return minOnsiteWorkPrice;
	}

	public void setMinOnsiteWorkPrice(BigDecimal minOnsiteWorkPrice) {
		this.minOnsiteWorkPrice = minOnsiteWorkPrice;
	}

	@Column(name = "min_offsite_hourly_rate", nullable = false)
	public BigDecimal getMinOffsiteHourlyRate() {
		return minOffsiteHourlyRate;
	}

	public void setMinOffsiteHourlyRate(BigDecimal minOffsiteHourlyRate) {
		this.minOffsiteHourlyRate = minOffsiteHourlyRate;
	}

	@Column(name = "min_offsite_work_price", nullable = false)
	public BigDecimal getMinOffsiteWorkPrice() {
		return minOffsiteWorkPrice;
	}

	public void setMinOffsiteWorkPrice(BigDecimal minOffsiteWorkPrice) {
		this.minOffsiteWorkPrice = minOffsiteWorkPrice;
	}

	@ManyToMany
	@JoinTable(name = "blacklisted_zipcode",
		joinColumns = {@JoinColumn(name = "profile_id")},
		inverseJoinColumns = {@JoinColumn(name = "postal_code_id")})
	public Set<PostalCode> getBlacklistedPostalCodes() {
		return blacklistedPostalCodes;
	}

	public void setBlacklistedPostalCodes(Set<PostalCode> blacklistedPostalCodes) {
		this.blacklistedPostalCodes = blacklistedPostalCodes;
	}

	@ManyToOne
	@JoinColumn(name = "mobile_provider_id", nullable = false)
	public MobileProvider getMobileProvider() {
		return mobileProvider;
	}

	@Transient
	public Long getMobileProviderId() {
		return mobileProvider == null ? null : mobileProvider.getId();
	}

	public void setMobileProvider(MobileProvider mobileProvider) {
		this.mobileProvider = mobileProvider;
	}

	@Column(name = "sms_phone_verification_code", nullable = true)
	public String getSmsPhoneVerificationCode() {
		return smsPhoneVerificationCode;
	}

	public void setSmsPhoneVerificationCode(String smsPhoneVerificationCode) {
		this.smsPhoneVerificationCode = smsPhoneVerificationCode;
	}

	@Column(name = "sms_phone_verification_code_expiration")
	public Calendar getSmsPhoneVerificationCodeExpiration() {
		return smsPhoneVerificationCodeExpiration;
	}

	public void setSmsPhoneVerificationCodeExpiration(Calendar smsPhoneVerificationCodeExpiration) {
		this.smsPhoneVerificationCodeExpiration = smsPhoneVerificationCodeExpiration;
	}

	@Column(name = "sms_phone_verified")
	public Boolean getSmsPhoneVerified() {
		return smsPhoneVerified;
	}

	public void setSmsPhoneVerified(Boolean smsPhoneVerified) {
		this.smsPhoneVerified = smsPhoneVerified;
	}

	@Column(name = "linkedin_id", nullable = true)
	public String getLinkedInId() {
		return linkedInId;
	}

	public void setLinkedInId(String linkedInId) {
		this.linkedInId = linkedInId;
	}

	@Column(name = "sms_phone", nullable = true, length = 15)
	public String getSmsPhone() {
		return smsPhone;
	}

	public void setSmsPhone(String smsPhone) {
		if (!StringUtilities.same(this.smsPhone, smsPhone)) {
			this.smsPhoneVerified = false;
		}
		this.smsPhone = smsPhone;
	}

	@Column(name = "overview", length = 4000)
	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	@Column(name = "overview_old_value", length = 4000)
	public String getOverviewOldValue() {
		return overviewOldValue;
	}

	public void setOverviewOldValue(String overviewOldValue) {
		this.overviewOldValue = overviewOldValue;
	}

	@Column(name = "onboard_complete_flag", nullable = false)
	public Boolean getOnboardCompleted() {
		return onboardCompleted;
	}

	public void setOnboardCompleted(Boolean onboardCompleted) {
		this.onboardCompleted = onboardCompleted;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "years_of_experience")
	public YearsOfExperienceEnum getYearsOfExperienceEnum() {
		return yearsOfExperienceEnum;
	}

	public void setYearsOfExperienceEnum(YearsOfExperienceEnum yearsOfExperienceEnum) {
		this.yearsOfExperienceEnum = yearsOfExperienceEnum;
	}

	@Column(name = "manage_work")
	public Boolean getManageWork() {
		return manageWork;
	}

	public void setManageWork(Boolean manageWork) {
		this.manageWork = manageWork;
	}

	@Column(name = "find_work")
	public Boolean getFindWork() {
		return findWork;
	}

	public void setFindWork(Boolean findWork) {
		this.findWork = findWork;
	}

	@Column(name = "csr_open")
	public Boolean getIsCsrOpen() {
		return csrOpen;
	}

	public void setIsCsrOpen(Boolean csrOpen) {
		this.csrOpen = csrOpen;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "postal_code_id")
	public PostalCode getProfilePostalCode() {
		return profilePostalCode;
	}

	public void setProfilePostalCode(PostalCode profilePostalCode) {
		this.profilePostalCode = profilePostalCode;
	}

	@Column(name = "session_duration", nullable = false)
	public Integer getSessionDuration() {
		return sessionDuration;
	}

	public void setSessionDuration(Integer sessionTimeout) {
		this.sessionDuration = sessionTimeout;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "time_zone_id", nullable = false)
	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	@Transient
	public boolean isOnboarding() {
		return !onboardCompleted;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append("Profile: ").append("id", getId()).toString();
	}

}
