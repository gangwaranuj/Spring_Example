package com.workmarket.thrift.core;

import com.workmarket.domains.model.lane.LaneType;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String userNumber;
	private String email;
	private Name name;
	private Company company;
	private Profile profile;
	private LaneType laneType;
	private RatingSummary ratingSummary;
	private Asset avatarOriginal;
	private Asset avatarLarge;
	private Asset avatarSmall;
	private boolean isWorkMarketEmployee;

	public User() {
		this.isWorkMarketEmployee = false;
	}

	public User(
			long id,
			String userNumber,
			String email,
			Name name,
			Company company,
			Profile profile,
			LaneType laneType,
			RatingSummary ratingSummary,
			Asset avatarOriginal,
			Asset avatarLarge,
			Asset avatarSmall) {
		this();
		this.id = id;
		this.userNumber = userNumber;
		this.email = email;
		this.name = name;
		this.company = company;
		this.profile = profile;
		this.laneType = laneType;
		this.ratingSummary = ratingSummary;
		this.avatarOriginal = avatarOriginal;
		this.avatarLarge = avatarLarge;
		this.avatarSmall = avatarSmall;
	}

	public long getId() {
		return this.id;
	}

	public User setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getUserNumber() {
		return this.userNumber;
	}

	public User setUserNumber(String userNumber) {
		this.userNumber = userNumber;
		return this;
	}

	public boolean isSetUserNumber() {
		return this.userNumber != null;
	}

	public String getEmail() {
		return this.email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public boolean isSetEmail() {
		return this.email != null;
	}

	public Name getName() {
		return this.name;
	}

	public User setName(Name name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public Company getCompany() {
		return this.company;
	}

	public User setCompany(Company company) {
		this.company = company;
		return this;
	}

	public boolean isSetCompany() {
		return this.company != null;
	}

	public Profile getProfile() {
		return this.profile;
	}

	public User setProfile(Profile profile) {
		this.profile = profile;
		return this;
	}

	public boolean isSetProfile() {
		return this.profile != null;
	}

	public LaneType getLaneType() {
		return this.laneType;
	}

	public User setLaneType(LaneType laneType) {
		this.laneType = laneType;
		return this;
	}

	public boolean isSetLaneType() {
		return this.laneType != null;
	}

	public RatingSummary getRatingSummary() {
		return this.ratingSummary;
	}

	public User setRatingSummary(RatingSummary ratingSummary) {
		this.ratingSummary = ratingSummary;
		return this;
	}

	public boolean isSetRatingSummary() {
		return this.ratingSummary != null;
	}

	public Asset getAvatarOriginal() {
		return this.avatarOriginal;
	}

	public User setAvatarOriginal(Asset avatarOriginal) {
		this.avatarOriginal = avatarOriginal;
		return this;
	}

	public boolean isSetAvatarOriginal() {
		return this.avatarOriginal != null;
	}

	public Asset getAvatarLarge() {
		return this.avatarLarge;
	}

	public User setAvatarLarge(Asset avatarLarge) {
		this.avatarLarge = avatarLarge;
		return this;
	}

	public boolean isSetAvatarLarge() {
		return this.avatarLarge != null;
	}

	public Asset getAvatarSmall() {
		return this.avatarSmall;
	}

	public User setAvatarSmall(Asset avatarSmall) {
		this.avatarSmall = avatarSmall;
		return this;
	}

	public boolean isSetAvatarSmall() {
		return this.avatarSmall != null;
	}

	public boolean isIsWorkMarketEmployee() {
		return this.isWorkMarketEmployee;
	}

	public User setIsWorkMarketEmployee(boolean isWorkMarketEmployee) {
		this.isWorkMarketEmployee = isWorkMarketEmployee;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof User)
			return this.equals((User) that);
		return false;
	}

	private boolean equals(User that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_userNumber = true && this.isSetUserNumber();
		boolean that_present_userNumber = true && that.isSetUserNumber();
		if (this_present_userNumber || that_present_userNumber) {
			if (!(this_present_userNumber && that_present_userNumber))
				return false;
			if (!this.userNumber.equals(that.userNumber))
				return false;
		}

		boolean this_present_email = true && this.isSetEmail();
		boolean that_present_email = true && that.isSetEmail();
		if (this_present_email || that_present_email) {
			if (!(this_present_email && that_present_email))
				return false;
			if (!this.email.equals(that.email))
				return false;
		}

		boolean this_present_name = true && this.isSetName();
		boolean that_present_name = true && that.isSetName();
		if (this_present_name || that_present_name) {
			if (!(this_present_name && that_present_name))
				return false;
			if (!this.name.equals(that.name))
				return false;
		}

		boolean this_present_company = true && this.isSetCompany();
		boolean that_present_company = true && that.isSetCompany();
		if (this_present_company || that_present_company) {
			if (!(this_present_company && that_present_company))
				return false;
			if (!this.company.equals(that.company))
				return false;
		}

		boolean this_present_profile = true && this.isSetProfile();
		boolean that_present_profile = true && that.isSetProfile();
		if (this_present_profile || that_present_profile) {
			if (!(this_present_profile && that_present_profile))
				return false;
			if (!this.profile.equals(that.profile))
				return false;
		}

		boolean this_present_laneType = true && this.isSetLaneType();
		boolean that_present_laneType = true && that.isSetLaneType();
		if (this_present_laneType || that_present_laneType) {
			if (!(this_present_laneType && that_present_laneType))
				return false;
			if (!this.laneType.equals(that.laneType))
				return false;
		}

		boolean this_present_ratingSummary = true && this.isSetRatingSummary();
		boolean that_present_ratingSummary = true && that.isSetRatingSummary();
		if (this_present_ratingSummary || that_present_ratingSummary) {
			if (!(this_present_ratingSummary && that_present_ratingSummary))
				return false;
			if (!this.ratingSummary.equals(that.ratingSummary))
				return false;
		}

		boolean this_present_avatarOriginal = true && this.isSetAvatarOriginal();
		boolean that_present_avatarOriginal = true && that.isSetAvatarOriginal();
		if (this_present_avatarOriginal || that_present_avatarOriginal) {
			if (!(this_present_avatarOriginal && that_present_avatarOriginal))
				return false;
			if (!this.avatarOriginal.equals(that.avatarOriginal))
				return false;
		}

		boolean this_present_avatarLarge = true && this.isSetAvatarLarge();
		boolean that_present_avatarLarge = true && that.isSetAvatarLarge();
		if (this_present_avatarLarge || that_present_avatarLarge) {
			if (!(this_present_avatarLarge && that_present_avatarLarge))
				return false;
			if (!this.avatarLarge.equals(that.avatarLarge))
				return false;
		}

		boolean this_present_avatarSmall = true && this.isSetAvatarSmall();
		boolean that_present_avatarSmall = true && that.isSetAvatarSmall();
		if (this_present_avatarSmall || that_present_avatarSmall) {
			if (!(this_present_avatarSmall && that_present_avatarSmall))
				return false;
			if (!this.avatarSmall.equals(that.avatarSmall))
				return false;
		}

		if (this.isWorkMarketEmployee != that.isWorkMarketEmployee)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_userNumber = true && (isSetUserNumber());
		builder.append(present_userNumber);
		if (present_userNumber)
			builder.append(userNumber);

		boolean present_email = true && (isSetEmail());
		builder.append(present_email);
		if (present_email)
			builder.append(email);

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		boolean present_company = true && (isSetCompany());
		builder.append(present_company);
		if (present_company)
			builder.append(company);

		boolean present_profile = true && (isSetProfile());
		builder.append(present_profile);
		if (present_profile)
			builder.append(profile);

		boolean present_laneType = true && (isSetLaneType());
		builder.append(present_laneType);
		if (present_laneType)
			builder.append(laneType);

		boolean present_ratingSummary = true && (isSetRatingSummary());
		builder.append(present_ratingSummary);
		if (present_ratingSummary)
			builder.append(ratingSummary);

		boolean present_avatarOriginal = true && (isSetAvatarOriginal());
		builder.append(present_avatarOriginal);
		if (present_avatarOriginal)
			builder.append(avatarOriginal);

		boolean present_avatarLarge = true && (isSetAvatarLarge());
		builder.append(present_avatarLarge);
		if (present_avatarLarge)
			builder.append(avatarLarge);

		boolean present_avatarSmall = true && (isSetAvatarSmall());
		builder.append(present_avatarSmall);
		if (present_avatarSmall)
			builder.append(avatarSmall);

		builder.append(true);
		builder.append(isWorkMarketEmployee);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("User(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("userNumber:");
		if (this.userNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.userNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("email:");
		if (this.email == null) {
			sb.append("null");
		} else {
			sb.append(this.email);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("company:");
		if (this.company == null) {
			sb.append("null");
		} else {
			sb.append(this.company);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("profile:");
		if (this.profile == null) {
			sb.append("null");
		} else {
			sb.append(this.profile);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("laneType:");
		if (this.laneType == null) {
			sb.append("null");
		} else {
			sb.append(this.laneType);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("ratingSummary:");
		if (this.ratingSummary == null) {
			sb.append("null");
		} else {
			sb.append(this.ratingSummary);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("avatarOriginal:");
		if (this.avatarOriginal == null) {
			sb.append("null");
		} else {
			sb.append(this.avatarOriginal);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("avatarLarge:");
		if (this.avatarLarge == null) {
			sb.append("null");
		} else {
			sb.append(this.avatarLarge);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("avatarSmall:");
		if (this.avatarSmall == null) {
			sb.append("null");
		} else {
			sb.append(this.avatarSmall);
		}
		first = false;

		if (!first) sb.append(", ");
		sb.append("isWorkMarketEmployee:");
		sb.append(this.isWorkMarketEmployee);
		first = false;

		sb.append(")");
		return sb.toString();
	}
}

