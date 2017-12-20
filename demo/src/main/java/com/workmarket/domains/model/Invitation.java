package com.workmarket.domains.model;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.utility.DateUtilities;

@Entity(name = "invitation")
@Table(name = "invitation")
@NamedQueries({
	@NamedQuery(name = "invitation.findbyinviter", query = "from invitation where inviting_user_id = :inviting_user_id"),
	@NamedQuery(name = "invitation.findbyemail", query = "from invitation where email = :email")
})
public class Invitation extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private String email;
	private User invitingUser;
	private User invitedUser;
	private Calendar invitationDate;
	private InvitationType invitationType;
	private InvitationStatusType invitationStatusType;
	private String message;
	private Company company;
	private String companyOverview;
	private Asset companyLogo;
	private Boolean showCompanyLogo = Boolean.FALSE;
	private Boolean showCompanyDescription = Boolean.FALSE;
	private Calendar lastReminderDate = DateUtilities.getCalendarNow();
	private RecruitingCampaign recruitingCampaign;

	public Invitation() {}
	public Invitation(Company company) {
		this.company = company;
	}

	@Column(name = "first_name", nullable = true, length = 50)
	public String getFirstName() {
		return firstName;
	}

	@Column(name = "last_name", nullable = true, length = 50)
	public String getLastName() {
		return lastName;
	}

	@Column(name = "email", nullable = false, length = 255)
	public String getEmail() {
		return email;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inviting_user_id", referencedColumnName = "id")
	public User getInvitingUser() {
		return invitingUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invited_user_id", referencedColumnName = "id")
	public User getInvitedUser() {
		return invitedUser;
	}

	@Column(name = "invitation_date", nullable = false)
	public Calendar getInvitationDate() {
		return invitationDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invitation_status_type_code", nullable = true)
	public InvitationStatusType getInvitationStatusType() {
		return invitationStatusType;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "invitation_type_code", nullable = true)
	public InvitationType getInvitationType() {
		return invitationType;
	}

	@Column(name = "message", nullable = true)
	public String getMessage() {
		return message;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "recruiting_campaign_id", referencedColumnName = "id", updatable = false)
	public RecruitingCampaign getRecruitingCampaign() {
		return recruitingCampaign;
	}

	@Column(name = "company_overview", nullable = true)
	public String getCompanyOverview() {
		return companyOverview;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	// this is one to one but using many to one because of Hibernate bug
	@JoinColumn(name = "company_logo_asset_id", nullable = true)
	public Asset getCompanyLogo() {
		return companyLogo;
	}

	@Column(name = "show_company_logo", nullable = false)
	public Boolean getShowCompanyLogo()
	{
		return showCompanyLogo;
	}

	@Column(name = "show_company_description", nullable = false)
	public Boolean getShowCompanyDescription()
	{
		return showCompanyDescription;
	}

	@Column(name = "last_reminder_date")
	public Calendar getLastReminderDate() {
		return lastReminderDate;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setInvitingUser(User invitingUser) {
		this.invitingUser = invitingUser;
	}

	public void setInvitedUser(User invitedUser) {
		this.invitedUser = invitedUser;
	}

	public void setInvitationDate(Calendar invitationDate) {
		this.invitationDate = invitationDate;
	}

	public void setInvitationStatusType(InvitationStatusType invitationStatusType) {
		this.invitationStatusType = invitationStatusType;
	}

	public void setInvitationType(InvitationType invitationType) {
		this.invitationType = invitationType;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setRecruitingCampaign(RecruitingCampaign recruitingCampaign) {
		this.recruitingCampaign = recruitingCampaign;
	}

	public void setCompanyOverview(String companyOverview) {
		this.companyOverview = companyOverview;
	}

	public void setCompanyLogo(Asset companyLogo) {
		this.companyLogo = companyLogo;
	}

	public void setShowCompanyLogo(Boolean showCompanyLogo) {
		this.showCompanyLogo = showCompanyLogo;
	}

	public void setShowCompanyDescription(Boolean showCompanyDescription) {
		this.showCompanyDescription = showCompanyDescription;
	}

	public void setLastReminderDate(Calendar lastReminderDate) {
		this.lastReminderDate = lastReminderDate;
	}

	@Transient
	public boolean isReminderBlocked() {
		if (lastReminderDate == null)
			return false;

		return (DateUtilities.getHoursBetween(lastReminderDate, DateUtilities.getCalendarNow()) < 72);
	}
}
