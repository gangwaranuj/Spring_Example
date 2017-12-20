package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.model.LocationDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.google.common.collect.Lists;

import java.util.List;

@ApiModel("CompanyProfile")
@JsonDeserialize(builder = CompanyProfileDTO.Builder.class)
public class CompanyProfileDTO {
	private final String name;
	private final String overview;
	private final String website;
	private final String avatar;
	private final String avatarSmall;
	private final LocationDTO location;
	private final Integer yearFounded;
	private final String workInviteSentToUserId;
	private final Boolean inVendorSearch;
	private final ScorecardDTO vendorScorecard;
	private final ScorecardDTO companyVendorScorecard;
	private final String createdOn;
	private final Integer employees;
	private final BuyerScorecardDTO buyerScorecard;
	private final List<TalentPoolDTO> talentPools;
	private final List<TalentPoolDTO> talentPoolMemberships;
	private final Boolean backgroundCheck;
	private final Boolean drugTest;
	private final List<LocationDTO> locationsServiced;
	private final List<SkillDTO> skills;
	private final Boolean followedByUser;
	private final String avatarUUID;

	public CompanyProfileDTO(Builder builder) {
		this.name = builder.name;
		this.overview = builder.overview;
		this.website = builder.website;
		this.avatar = builder.avatar;
		this.avatarSmall = builder.avatarSmall;
		this.location = builder.location.build();
		this.yearFounded = builder.yearFounded;
		this.workInviteSentToUserId = builder.workInviteSentToUserId;
		this.inVendorSearch = builder.inVendorSearch;
		this.createdOn = builder.createdOn;
		this.employees = builder.employees;
		this.talentPools = builder.talentPools;
		this.followedByUser = builder.followedByUser;
		this.talentPoolMemberships = builder.talentPoolMemberships;

		if (builder.vendorScorecard != null) {
			this.vendorScorecard = builder.vendorScorecard.build();
		} else {
			this.vendorScorecard = null;
		}

		if (builder.companyVendorScorecard != null) {
			this.companyVendorScorecard = builder.companyVendorScorecard.build();
		} else {
			this.companyVendorScorecard = null;
		}

		this.buyerScorecard = builder.buyerScorecard != null ? builder.buyerScorecard.build() : null;
		this.backgroundCheck = builder.backgroundCheck;
		this.drugTest = builder.drugTest;

		this.locationsServiced = Lists.newArrayList();
		if(builder.locationsServiced != null) {
			for(LocationDTO.Builder location : builder.locationsServiced) {
				this.locationsServiced.add(location.build());
			}
		}
		this.skills = Lists.newArrayList();
		if(builder.skills != null) {
			for(SkillDTO.Builder skill : builder.skills) {
				this.skills.add(new SkillDTO(skill));
			}
		}

		this.avatarUUID = builder.avatarUUID;
	}


	@ApiModelProperty(name = "name")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(name = "overview")
	@JsonProperty("overview")
	public String getOverview() {
		return overview;
	}

	@ApiModelProperty(name = "website")
	@JsonProperty("website")
	public String getWebsite() {
		return website;
	}

	@ApiModelProperty(name = "avatar")
	@JsonProperty("avatar")
	public String getAvatar() {
		return avatar;
	}

	@ApiModelProperty(name = "avatarSmall")
	@JsonProperty("avatarSmall")
	public String getAvatarSmall() {
		return avatarSmall;
	}

	@ApiModelProperty(name = "location")
	@JsonProperty("location")
	public LocationDTO getLocation() {
		return location;
	}

	@ApiModelProperty(name = "yearFounded")
	@JsonProperty("yearFounded")
	public Integer getYearFounded() {
		return yearFounded;
	}

	@ApiModelProperty(name = "workInviteSentToUserId")
	@JsonProperty("workInviteSentToUserId")
	public String getWorkInviteSentToUserId() {
		return workInviteSentToUserId;
	}

	@ApiModelProperty(name = "inVendorSearch")
	@JsonProperty("inVendorSearch")
	public Boolean getInVendorSearch() {
		return inVendorSearch;
	}

	@ApiModelProperty(name = "vendorScorecard")
	@JsonProperty("vendorScorecard")
	public ScorecardDTO getVendorScorecard() {
		return vendorScorecard;
	}

	@ApiModelProperty(name = "companyVendorScorecard")
	@JsonProperty("companyVendorScorecard")
	public ScorecardDTO getCompanyVendorScorecard() {
		return companyVendorScorecard;
	}

	@ApiModelProperty(name = "createdOn")
	@JsonProperty("createdOn")
	public String getCreatedOn() {
		return createdOn;
	}

	@ApiModelProperty(name = "employees")
	@JsonProperty("employees")
	public Integer getEmployees() {
		return employees;
	}

	@ApiModelProperty(name = "buyerScorecard")
	@JsonProperty("buyerScorecard")
	public BuyerScorecardDTO getBuyerScorecard() {
		return buyerScorecard;
	}

	@ApiModelProperty(name = "talentPools")
	@JsonProperty("talentPools")
	public List<TalentPoolDTO> getTalentPools() { return talentPools; }

	@ApiModelProperty(name = "talentPoolMemberships")
	@JsonProperty("talentPoolMemberships")
	public List<TalentPoolDTO> getTalentPoolMemberships() { return talentPoolMemberships; }

	public Boolean getBackgroundCheck() {
		return backgroundCheck;
	}

	public Boolean getDrugTest() {
		return drugTest;
	}

	public List<LocationDTO> getLocationsServiced() {
		return locationsServiced;
	}

	public List<SkillDTO> getSkills() {
		return skills;
	}

	public Boolean isFollowedByUser() {
		return followedByUser;
	}

	@ApiModelProperty(name = "avatarUUID")
	@JsonProperty("avatarUUID")
	public String getAvatarUUID() {
		return avatarUUID;
	}

	public static class Builder {
		private String overview;
		private String website;
		private String avatar;
		private String avatarSmall;
		private LocationDTO.Builder location = new LocationDTO.Builder();
		private Integer yearFounded;
		private String workInviteSentToUserId;
		private Boolean inVendorSearch;
		private String name;
		private ScorecardDTO.Builder vendorScorecard;
		private ScorecardDTO.Builder companyVendorScorecard;
		private String createdOn;
		private Integer employees;
		private BuyerScorecardDTO.Builder buyerScorecard;
		private List<TalentPoolDTO> talentPools;
		private List<TalentPoolDTO> talentPoolMemberships;
		private Boolean backgroundCheck;
		private Boolean drugTest;
		private List<LocationDTO.Builder> locationsServiced;
		private List<SkillDTO.Builder> skills;
		private String avatarUUID;
		private Boolean followedByUser;

		public Builder() {}

		public Builder(CompanyProfileDTO companyProfileDTO) {
			this.name = companyProfileDTO.name;
			this.overview = companyProfileDTO.overview;
			this.website = companyProfileDTO.website;
			this.avatar = companyProfileDTO.avatar;
			this.avatarSmall = companyProfileDTO.avatarSmall;
			this.location = new LocationDTO.Builder(companyProfileDTO.location);
			this.yearFounded = companyProfileDTO.yearFounded;
			this.workInviteSentToUserId = companyProfileDTO.workInviteSentToUserId;
			this.inVendorSearch = companyProfileDTO.inVendorSearch;
			this.createdOn = companyProfileDTO.createdOn;
			this.employees = companyProfileDTO.employees;
			this.talentPools = companyProfileDTO.talentPools;
			this.talentPoolMemberships = companyProfileDTO.talentPoolMemberships;
			this.followedByUser = companyProfileDTO.followedByUser;

			if (companyProfileDTO.vendorScorecard != null) {
				this.vendorScorecard = new ScorecardDTO.Builder(companyProfileDTO.vendorScorecard);
			}

			if (companyProfileDTO.companyVendorScorecard != null) {
				this.companyVendorScorecard = new ScorecardDTO.Builder(companyProfileDTO.companyVendorScorecard);
			}

			if(companyProfileDTO.buyerScorecard != null) {
				this.buyerScorecard = new BuyerScorecardDTO.Builder(companyProfileDTO.buyerScorecard);
			}

			this.backgroundCheck = companyProfileDTO.getBackgroundCheck();
			this.drugTest = companyProfileDTO.getDrugTest();

			this.locationsServiced = Lists.newArrayList();
			if(companyProfileDTO.getLocationsServiced() != null) {
				for(LocationDTO location : companyProfileDTO.getLocationsServiced()) {
					this.locationsServiced.add(new LocationDTO.Builder(location));
				}
			}
			this.skills = Lists.newArrayList();
			if(companyProfileDTO.getSkills() != null) {
				for(SkillDTO skill : companyProfileDTO.getSkills()) {
					this.skills.add(new SkillDTO.Builder(skill));
				}
			}
		}

		@JsonProperty("name") public Builder setName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("overview") public Builder setOverview(String overview) {
			this.overview = overview;
			return this;
		}

		@JsonProperty("website") public Builder setWebsite(String website) {
			this.website = website;
			return this;
		}

		@JsonProperty("avatar") public Builder setAvatar(String avatar) {
			this.avatar = avatar;
			return this;
		}

		@JsonProperty("avatarSmall") public Builder setAvatarSmall(String avatarSmall) {
			this.avatarSmall = avatarSmall;
			return this;
		}

		@JsonProperty("location") public Builder setLocation(LocationDTO.Builder location) {
			this.location = location;
			return this;
		}

		@JsonProperty("yearFounded") public Builder setYearFounded(Integer yearFounded) {
			this.yearFounded = yearFounded;
			return this;
		}

		@JsonProperty("workInviteSentToUserId") public Builder setWorkInviteSentToUserId(String workInviteSentToUserId) {
			this.workInviteSentToUserId = workInviteSentToUserId;
			return this;
		}

		@JsonProperty("inVendorSearch") public Builder setInVendorSearch(Boolean inVendorSearch) {
			this.inVendorSearch = inVendorSearch;
			return this;
		}

		@JsonProperty("vendorScorecard") public Builder setVendorScorecard(ScorecardDTO.Builder vendorScorecard) {
			this.vendorScorecard = vendorScorecard;
			return this;
		}

		@JsonProperty("companyVendorScorecard") public Builder setCompanyVendorScorecard(ScorecardDTO.Builder companyVendorScorecard) {
			this.companyVendorScorecard = companyVendorScorecard;
			return this;
		}

		@JsonProperty("createdOn") public Builder setCreatedOn(String createdOn) {
			this.createdOn = createdOn;
			return this;
		}

		@JsonProperty("employees") public Builder setEmployees(Integer employees) {
			this.employees = employees;
			return this;
		}

		@JsonProperty("buyerScorecard") public Builder setBuyerScorecard(BuyerScorecardDTO.Builder buyerScorecard) {
			this.buyerScorecard = buyerScorecard;
			return this;
		}

		@JsonProperty("talentPools") public Builder setTalentPools(List<TalentPoolDTO> talentPools) {
			this.talentPools = talentPools;
			return this;
		}

		@JsonProperty("talentPoolMemberships") public Builder setTalentPoolMemberships(List<TalentPoolDTO> talentPoolMemberships) {
			this.talentPoolMemberships = talentPoolMemberships;
			return this;
		}

		@JsonProperty("backgroundCheck") public Builder setBackgroundCheck(Boolean backgroundCheck) {
			this.backgroundCheck = backgroundCheck;
			return this;
		}

		@JsonProperty("drugTest") public Builder setDrugTest(Boolean drugTest) {
			this.drugTest = drugTest;
			return this;
		}

		@JsonProperty("locationsServiced") public Builder setLocationsServiced(List<LocationDTO.Builder> locations) {
			this.locationsServiced = locations;
			return this;
		}

		@JsonProperty("skills") public Builder setSkills(List<SkillDTO.Builder> skills) {
			this.skills = skills;
			return this;
		}

		@JsonProperty("followedByUser") public Builder setFollowedByUser(Boolean followedByUser) {
			this.followedByUser = followedByUser;
			return this;
		}

		@JsonProperty("avatarUUID") public void setAvatarUUID(String avatarUUID) {
			this.avatarUUID = avatarUUID;
		}

		public CompanyProfileDTO build() {
			return new CompanyProfileDTO(this);
		}
	}
}
