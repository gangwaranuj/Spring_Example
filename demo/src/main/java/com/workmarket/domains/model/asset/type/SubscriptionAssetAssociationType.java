package com.workmarket.domains.model.asset.type;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

@Entity(name="subscriptionAssetAssociationType")
@Table(name="subscription_asset_association_type")
public class SubscriptionAssetAssociationType extends AssetType {

	private static final long serialVersionUID = 1L;

	public static final String ATTACHMENT = "attachment";

	public static final List<String> TYPES = Arrays.asList(new String[] {NONE, ATTACHMENT});

	public SubscriptionAssetAssociationType() {
		super();
	}
	public SubscriptionAssetAssociationType(String code){
		super(code);
	}
}