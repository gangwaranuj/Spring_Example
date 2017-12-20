package com.workmarket.domains.model.publicinfo;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * Created by rahul on 12/15/13
 */
@Entity(name = "publicPageProfile")
@Table(name = "public_page_profile")
@NamedQueries({
		@NamedQuery(name = "PublicPageProfile.getAllPublicPageProfilesDataByIndustry", query = "select user.firstName as firstName, user.lastName as lastName, " +
				"user.userNumber as userNumber, pp.gender as gender, pp.shortDescription as shortDescription " +
				"from publicPageProfile pp inner join pp.user as user inner join pp.asset as asset " +
				"where pp.industryName = :industry")
})
public class PublicPageProfile extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	private User user;
	private String gender;
	private String industryName;
	private Asset asset;
	private String shortDescription;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User getUser() {
		return user;
	}

	@Column(name = "gender", nullable = false, length = 1)
	public String getGender() {
		return gender;
	}

	@Column(name = "industry_name", nullable = false, length = 100)
	public String getIndustryName() {
		return industryName;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "asset_id", referencedColumnName = "id")
	public Asset getAsset() {
		return asset;
	}

	@Column(name = "short_description", nullable = false)
	public String getShortDescription() {
		return shortDescription;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
}
