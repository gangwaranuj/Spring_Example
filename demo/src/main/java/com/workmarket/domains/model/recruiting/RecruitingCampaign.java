package com.workmarket.domains.model.recruiting;

import com.workmarket.configuration.ConfigurationService;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.ActiveDeletableEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="recruitingCampaign")
@Table(name="recruiting_campaign")
@AuditChanges
public class RecruitingCampaign extends ActiveDeletableEntity {

	private static final long serialVersionUID = 1L;

	private RecruitingVendor recruitingVendor;
	private String title;
	private String description;
	private Company company;
	private String companyOverview;
	private Asset companyLogo;
	private UserGroup UserGroup;
	private String shortUrl;
	private Integer clicks;
	private Integer users;
	private boolean privateCampaign = false;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "recruiting_vendor_code", referencedColumnName = "code")
	public RecruitingVendor getRecruitingVendor() {
		return recruitingVendor;
	}

	public void setRecruitingVendor(RecruitingVendor recruitingVendor) {
		this.recruitingVendor = recruitingVendor;
	}

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "description", nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name="company_overview", nullable=true)
	public String getCompanyOverview() {
		return companyOverview;
	}

	public void setCompanyOverview(String companyOverview) {
		this.companyOverview = companyOverview;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY) // this is one to on but I am using many to one because og Hibernate bug
	@JoinColumn(name="company_logo_asset_id", nullable=true)
	public Asset getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(Asset companyLogo) {
		this.companyLogo = companyLogo;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "company_user_group_id", nullable = true)
	public UserGroup getCompanyUserGroup() {
		return UserGroup;
	}

	public void setCompanyUserGroup(UserGroup UserGroup) {
		this.UserGroup = UserGroup;
	}

	@Column(name = "short_url", length = 20)
	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	@Column(name="private_campaign", nullable=false)
	public boolean isPrivateCampaign() {
		return privateCampaign;
	}

	public void setPrivateCampaign(boolean privateCampaign) {
		this.privateCampaign = privateCampaign;
	}

	@Transient
	public String getRelativeURI() {
		return ConfigurationService.CAMPAIGN_DETAILS_URL + getEncryptedId();
	}

	@Transient
	public Integer getClicks() {
		return clicks;
	}

	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}

	@Transient
	public Integer getUsers() {
		return users;
	}

	public void setUsers(Integer users) {
		this.users = users;
	}
}
