package com.workmarket.domains.model.account.pricing.subscription;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.LookupEntity;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity(name = "subscriptionType")
@Table(name = "subscription_type")
public class SubscriptionType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String BLOCK = "block";
	public static final String BAND = "band";

	public static final List<String> SUBSCRIPTION_TYPE_CODES = Lists.newArrayList(BLOCK, BAND);

	public SubscriptionType() {
	}

	public SubscriptionType(String code) {
		super(code);
	}

}
