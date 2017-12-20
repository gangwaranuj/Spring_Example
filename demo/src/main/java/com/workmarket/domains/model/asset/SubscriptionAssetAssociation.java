package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="subscriptionAssetAssociation")
@Table(name="subscription_asset_association")
@AuditChanges
public class SubscriptionAssetAssociation extends AbstractEntityAssetAssociation<SubscriptionConfiguration> implements EntityAssetAssociation<SubscriptionConfiguration> {
	private static final long serialVersionUID = 1L;

	public SubscriptionAssetAssociation() {}
	public SubscriptionAssetAssociation(SubscriptionConfiguration entity, Asset asset, AssetType type) {
		super(entity, asset, type);
	}
}
