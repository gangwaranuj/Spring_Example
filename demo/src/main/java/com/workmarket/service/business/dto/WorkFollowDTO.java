package com.workmarket.service.business.dto;

import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class WorkFollowDTO {

	private Long id;
	private Long userId;
	private String
		followerFirstName,
		followerLastName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFollowerLastName() {
		return followerLastName;
	}

	public void setFollowerLastName(String followerLastName) {
		this.followerLastName = followerLastName;
	}

	public String getFollowerFirstName() {
		return followerFirstName;
	}

	public void setFollowerFirstName(String followerFirstName) {
		this.followerFirstName = followerFirstName;
	}

	public String getFollowerFullName() {
		return StringUtilities.fullName(getFollowerFirstName(), getFollowerLastName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WorkFollowDTO)) {
			return false;
		}

		WorkFollowDTO that = (WorkFollowDTO) obj;
		return new EqualsBuilder()
			.append(id, that.getId())
			.append(userId, that.getUserId())
			.append(followerFirstName, that.getFollowerFirstName())
			.append(followerLastName, that.getFollowerLastName())
			.build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(userId)
			.append(followerFirstName)
			.append(followerLastName)
			.build();
	}
}
