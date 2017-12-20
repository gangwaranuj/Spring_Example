package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;

import javax.persistence.*;

@Entity(name = "userLinkAssociation")
@Table(name = "user_link_association")
public class UserLinkAssociation extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	private User user;
	private Link link;
	private Integer assetOrder;
	private boolean deleted = false;

	public UserLinkAssociation() {}

	public UserLinkAssociation(User user, Link link, Integer assetOrder) {
		this.user = user;
		this.link = link;
		this.assetOrder = assetOrder;
	}

	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="user_id", referencedColumnName="id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name="link_id", referencedColumnName="id")
	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	@Column(name = "asset_order", nullable = false)
	public Integer getAssetOrder() {
		return this.assetOrder;
	}

	public void setAssetOrder(Integer assetOrder) {
		this.assetOrder = assetOrder;
	}

	@Column(name = "deleted", nullable = false)
	public boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
