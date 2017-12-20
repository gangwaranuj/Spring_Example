package com.workmarket.domains.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: micah
 * Date: 3/17/13
 * Time: 2:48 AM
 */
@Entity(name = "SocialUserConnection")
@org.hibernate.annotations.Entity(mutable = false)
@Table(name = "UserConnection")
public class SocialUserConnection implements Serializable {
	private static final long serialVersionUID = 1L;

	private SocialUserconnectionPK socialUserconnectionPK;
	private User user;

	private Integer rank;
	private String displayName;
	private String profileUrl;
	private String imageUrl;
	private String accessToken;
	private String secret;
	private String refreshToken;
	private BigDecimal expireTime;

	@EmbeddedId
	public SocialUserconnectionPK getId() {
		return socialUserconnectionPK;
	}

	public void setId(SocialUserconnectionPK socialUserconnectionPK) {
		this.socialUserconnectionPK = socialUserconnectionPK;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "userId", referencedColumnName = "id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "rank", nullable = false, length = 11)
	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Column(name = "displayName", nullable = true, length = 255)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name = "profileUrl", nullable = true, length = 512)
	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	@Column(name = "imageUrl", nullable = true, length = 512)
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Column(name = "accessToken", nullable = false, length = 255)
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Column(name = "secret", nullable = true, length = 255)
	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Column(name = "refreshToken", nullable = true, length = 255)
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Column(name = "expireTime", nullable = true, length = 20)
	public BigDecimal getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(BigDecimal expireTime) {
		this.expireTime = expireTime;
	}
}

@Embeddable
class SocialUserconnectionPK implements Serializable {
	private String providerId;
	private String providerUserId;

	@Column(name = "providerUserId", nullable = false, length = 255)
	public String getProviderUserId() {
		return providerUserId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	@Column(name = "providerId", nullable = false, length = 255)
	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public boolean equals(Object key) {
		if (!(key instanceof SocialUserconnectionPK)) return false;
		SocialUserconnectionPK otherKey = (SocialUserconnectionPK) key;
		String otherProviderId = otherKey.getProviderId();
		String otherProviderUserId = otherKey.getProviderUserId();
		if (
			otherProviderId == null || otherProviderUserId == null ||
			!otherProviderId.equals(providerId) ||
			!otherProviderUserId.equals(providerUserId)
		) return false;
		return true;
	}

	public int hashCode() {
		int code = 0;
		if (providerId != null) code += providerId.hashCode();
		if (providerUserId != null) code += providerUserId.hashCode();
		return code;
	}
}
