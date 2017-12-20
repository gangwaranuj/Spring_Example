package com.workmarket.domains.model;

import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.UserAclRoleAssociation;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.utility.EncryptionUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

@Entity(name = "user")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Table(name = "user")
@NamedQueries({
	/// FIXME HANDLE THESE QUERIES WHERE THEY'RE USED
// Appear to be unused
//	@NamedQuery(name = "user.auth", query = "from user where email = :email and password = :password and userStatusType.code <> 'deleted'"),
//	@NamedQuery(name = "user.findAllActiveInternal", query = "from user u join fetch u.roles where 'internal' in elements(u.roles) and u.userStatusType.code not in ('deleted', 'deactivate')"),
	@NamedQuery(name = "user.findbyemail", query = "from user where email = :email and userStatusType.code IN ('pending', 'approved', 'suspended', 'deactivate', 'hold', 'locked') "),
	@NamedQuery(name = "user.findDeletedUserByEmail", query = "from user where email = :email and userStatusType.code IN ('deleted') "),
	@NamedQuery(name = "user.findallinternal", query = "from user u join fetch u.roles where 'internal' in elements(u.roles) order by u.lastName"),
	@NamedQuery(name = "user.findAllInternalContractors", query = "from user u join fetch u.roles where 'internal' in elements(u.roles) and u.email not like '%workmarket.com' and u.userStatusType.code not in ('deleted', 'deactivate') order by u.lastName"),
	@NamedQuery(name = "user.findAllWorkmarket", query = "from user u join fetch u.roles where u.company.id=1 and u.userStatusType.code not in ('deleted', 'deactivate') order by u.lastName"),
	@NamedQuery(name = "user.findByAssetCreatorId", query = "select u from user u, asset a where a.creatorId=u.id and a.id = :assetId"),
})
@AuditChanges
public class User extends AuditedEntity {

	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;

	private String firstNameOldValue;
	private String lastNameOldValue;

	private String email;
	private String changedEmail;
	private String replyToEmail;
	private String secondaryEmail;
	private String password;
	private String salt = "obsolete";
	private Boolean emailConfirmed = Boolean.FALSE;
	private Calendar emailConfirmedOn;
	private Set<RoleType> roles = Sets.newLinkedHashSet();
	private Company company;
	private UserStatusType userStatusType;
	private Calendar userStatusTypeModifiedOn;

	private Boolean apiEnabled = Boolean.FALSE;

	private Set<UserUserGroupAssociation> userGroupAssociations = Sets.newLinkedHashSet();
	private Set<UserAclRoleAssociation> userRoleAssociations = Sets.newLinkedHashSet();
	private Profile profile;
	private ApprovalStatus lane3ApprovalStatus = ApprovalStatus.OPT_OUT;
	private ScreeningStatusType screeningStatusType = new ScreeningStatusType(ScreeningStatusType.NOT_REQUESTED);

	private RecruitingCampaign recruitingCampaign;
	private BigDecimal spendLimit = Constants.DEFAULT_SPEND_LIMIT; // per assignment spend limit
	private BigDecimal salary;
	private Integer stockOptions;
	private Calendar startDate;
	private Integer version;
	private String userNumber;
	private Invitation invitation;
	private Integer promoDismissed;
	private Integer warpRequisitionId;
	private String uuid;

	@Column(name = "first_name", nullable = false, length = 50)
	public String getFirstName() {
		return firstName;
	}

	@Column(name = "last_name", nullable = false, length = 50)
	public String getLastName() {
		return lastName;
	}

	@Column(name = "first_name_old_value", length = 50)
	public String getFirstNameOldValue() {
		return firstNameOldValue;
	}

	@Column(name = "last_name_old_value", length = 50)
	public String getLastNameOldValue() {
		return lastNameOldValue;
	}

	@Column(name = "email", nullable = false, length = 255)
	public String getEmail() {
		return email;
	}

	@Column(name = "reply_to_email", nullable = true, length = 255)
	public String getReplyToEmail() {
		return replyToEmail;
	}

	@Column(name = "secondary_email", nullable = true, length = 255)
	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	@Column(name = "password", nullable = false, length = 64)
	public String getHashedPassword() {
		return password;
	}

	@Column(name = "password")
	public void setHashedPassword(String password) {
		this.password = password;
	}

	@Column(name = "email_confirmed", nullable = false, length = 1)
	@Type(type = "yes_no")
	@Deprecated // use AuthenticationService.getEmailConfirmed
	public Boolean getEmailConfirmed() {
		return emailConfirmed;
	}

	@Deprecated // use AuthenticationService.getEmailConfirmedOn
	@Column(name = "email_confirmed_on")
	public Calendar getEmailConfirmedOn() {
		return emailConfirmedOn;
	}

	@Deprecated // Use UserRoleService
	@ManyToMany
	@JoinTable(name = "user_role",
		joinColumns = {@JoinColumn(name = "user_id")},
		inverseJoinColumns = {@JoinColumn(name = "role_type_code")})
	public Set<RoleType> getRoles() {
		return roles;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	public Company getCompany() {
		return company;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_status_type_code", referencedColumnName = "code")
	@Deprecated // use AuthenticationService.getUserStatusType
	public UserStatusType getUserStatusType() {
		return userStatusType;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstNameOldValue(String oldFirstName) {
		this.firstNameOldValue = oldFirstName;
	}

	public void setLastNameOldValue(String oldLastName) {
		this.lastNameOldValue = oldLastName;
	}

	/**
	 * Not really deprecated, but if you're using this to change the email address of a user, make sure to
	 * notify the auth service via {@link AuthenticationClient#changeUsername}.
	 */
	@Deprecated
	public void setEmail(String email) {
		this.email = email;
	}

	public void setReplyToEmail(String replyToEmail) {
		this.replyToEmail = replyToEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	@Deprecated // use AuthenticationService.setEmailConfirmed
	public void setEmailConfirmed(Boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	@Deprecated // use AuthenticationService.setEmailConfirmedOn
	public void setEmailConfirmedOn(Calendar emailConfirmedOn) {
		this.emailConfirmedOn = emailConfirmedOn;
	}

	@Deprecated // use UserRoleService
	public void setRoles(Set<RoleType> roles) {
		this.roles = roles;
	}

	/**
	 * If you are using this to change a user's existing company, use AuthenticationClient.changeCompany where you
	 * do.  Otherwise, carry on.
	 */
	@Deprecated
	public void setCompany(Company company) {
		this.company = company;
	}

	@Deprecated // use AuthenticationService.setUserStatusType
	public void setUserStatusType(UserStatusType userStatusType) {
		this.userStatusType = userStatusType;
		this.userStatusTypeModifiedOn = Calendar.getInstance();
	}

	@Deprecated // Use UserRoleService
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public Set<UserAclRoleAssociation> getUserRoleAssociations() {
		return userRoleAssociations;
	}

	@Deprecated // Use UserRoleService -- but is not called anywhere I can find.
	public void setUserRoleAssociations(Set<UserAclRoleAssociation> userRoleAssociations) {
		this.userRoleAssociations = userRoleAssociations;
	}

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = {})
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@Column(name = "lane3_approval_status", nullable = false)
	public ApprovalStatus getLane3ApprovalStatus() {
		return lane3ApprovalStatus;
	}

	public void setLane3ApprovalStatus(ApprovalStatus lane3ApprovalStatus) {
		this.lane3ApprovalStatus = lane3ApprovalStatus;
	}

	@Column(name = "promo_dismissed", nullable = false)
	public Integer getPromoDismissed() {
		return promoDismissed;
	}

	public void setPromoDismissed(Integer dismissed) {
		this.promoDismissed = dismissed;
	}


	@Column(name = "uuid", updatable = false)
	public String getUuid() {
		if (uuid == null) {
			setUuid(UUID.randomUUID().toString());
		}
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	// ----- Transient
	@Transient
	public String getFullName() {
		return StringUtilities.fullName(getFirstName(), getLastName());
	}

	@Transient
	public boolean isEmailConfirmed() {
		return BooleanUtils.isTrue(emailConfirmed);
	}

	@Deprecated //use UserRoleService
	@Transient
	public boolean isInternalUser() {
		return hasRole(RoleType.INTERNAL);
	}

	@Transient
	public boolean isUserExclusive() {
		return !this.getLane3ApprovalStatus().equals(ApprovalStatus.APPROVED) && this.getCompany().getOperatingAsIndividualFlag();
	}

	@Transient
	public boolean hasFailedScreening() {
		return this.screeningStatusType.getCode().equals(ScreeningStatusType.FAILED);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append("User: ").append("id", getId()).append("email", email).toString();
	}

	@Transient
	public boolean isLane3Approved() {
		return (lane3ApprovalStatus.equals(ApprovalStatus.APPROVED));
	}

	@Transient
	public boolean isLane3Pending() {
		return (lane3ApprovalStatus.equals(ApprovalStatus.PENDING));
	}

	@Deprecated // use UserRoleService
	@Transient
	public boolean hasRole(String code) {
		for (RoleType role : getRoles()) {
			if (role.getCode().equals(code)) {
				return true;
			}
		}
		return false;
	}

	@Deprecated // use UserRoleService
	@Transient
	public boolean hasAclRole(long aclRoleId) {
		for (UserAclRoleAssociation association : getUserRoleAssociations()) {
			if (association.getRole().getId().equals(aclRoleId) && !association.getDeleted()) {
				return true;
			}
		}
		return false;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "recruiting_campaign_id", referencedColumnName = "id")
	public RecruitingCampaign getRecruitingCampaign() {
		return recruitingCampaign;
	}

	public void setRecruitingCampaign(RecruitingCampaign recruitingCampaign) {
		this.recruitingCampaign = recruitingCampaign;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public Set<UserUserGroupAssociation> getUserGroupAssociations() {
		return userGroupAssociations;
	}

	public void setUserGroupAssociations(Set<UserUserGroupAssociation> userGroupAssociations) {
		this.userGroupAssociations = userGroupAssociations;
	}

	@Column(name = "spend_limit", nullable = false)
	public BigDecimal getSpendLimit() {
		return spendLimit;
	}

	public void setSpendLimit(BigDecimal spendLimit) {
		this.spendLimit = spendLimit;
	}

	@Column(name = "salary")
	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	@Column(name = "stock_options")
	public Integer getStockOptions() {
		return stockOptions;
	}

	public void setStockOptions(Integer stockOptions) {
		this.stockOptions = stockOptions;
	}

	@Column(name = "start_date")
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Version
	@Column(name = "version")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Transient
	public boolean hasSpendLimit() {
		return (this.spendLimit != null);
	}

	@Column(name = "salt", nullable = false)
	public String getObsoleteSaltDoNotUse() {
		return salt;
	}

	protected void setObsoleteSaltDoNotUse(String salt) {
		this.salt = salt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "screening_status_type_code", referencedColumnName = "code", nullable = true)
	public ScreeningStatusType getScreeningStatusType() {
		return screeningStatusType;
	}

	public void setScreeningStatusType(ScreeningStatusType screeningStatusType) {
		this.screeningStatusType = screeningStatusType;
	}

	@Deprecated // use AuthenticationService.getUserStatusTypeModifiedOn
	@Column(name = "user_status_type_modified_on")
	public Calendar getUserStatusTypeModifiedOn() {
		return userStatusTypeModifiedOn;
	}

	@Deprecated // should only be called by magical bean stuff
	public void setUserStatusTypeModifiedOn(Calendar userStatusTypeModifiedOn) {
		this.userStatusTypeModifiedOn = userStatusTypeModifiedOn;
	}

	@Column(name = "api_enabled", nullable = false)
	public Boolean isApiEnabled() {
		return apiEnabled;
	}

	public void setApiEnabled(Boolean apiEnabled) {
		this.apiEnabled = apiEnabled;
	}

	@Column(name = "user_number", nullable = false, unique = true)
	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	@Column(name = "changed_email", nullable = true, length = 255)
	public String getChangedEmail() {
		return changedEmail;
	}

	public void setChangedEmail(String changedEmail) {
		this.changedEmail = changedEmail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invitation_id", referencedColumnName = "id")
	public Invitation getInvitation() {
		return invitation;
	}

	public void setInvitation(Invitation invitation) {
		this.invitation = invitation;
	}

	@Deprecated //use UserRoleService
	@Transient
	public boolean isDispatcher() {
		return hasAclRole(AclRole.ACL_DISPATCHER);
	}

	@Deprecated //use UserRoleService
	@Transient
	public boolean isAdminOrManager() {
		return hasAclRole(AclRole.ACL_ADMIN) || hasAclRole(AclRole.ACL_MANAGER);
	}

	@Deprecated //use UserRoleService
	@Transient
	public boolean isAdmin() {
		return hasAclRole(AclRole.ACL_ADMIN);
	}

	@Deprecated //use UserRoleService
	@Transient
	public boolean isController() {
		return hasAclRole(AclRole.ACL_CONTROLLER);
	}

	@Transient
	public Asset getAvatarSmall() {
		return null;
	}

	@Deprecated //use UserRoleService
	@Transient
	public WorkStatus getWorkStatus() {
		if (hasAclRole(AclRole.ACL_SHARED_WORKER)) {
			return WorkStatus.PUBLIC;
		} else if (hasAclRole(AclRole.ACL_WORKER)) {
			return WorkStatus.UNLISTED;
		} else {
			return WorkStatus.UNAVAILABLE;
		}
	}

	public enum WorkStatus {PUBLIC, UNLISTED, UNAVAILABLE}

	@Column(name = "warp_requisition_id", nullable = true)
	public Integer getWarpRequisitionId() { return warpRequisitionId; }

	public void setWarpRequisitionId(Integer warpRequisitionId){
		this.warpRequisitionId = warpRequisitionId;
	}
}
