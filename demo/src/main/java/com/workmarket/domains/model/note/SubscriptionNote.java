package com.workmarket.domains.model.note;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("subscription")
@AuditChanges
public class SubscriptionNote extends Note {

	private static final long serialVersionUID = 1L;

	private SubscriptionConfiguration subscriptionConfiguration;

	public SubscriptionNote() {
    }

    public SubscriptionNote(String content, SubscriptionConfiguration subscriptionConfiguration) {
        super(content);
        this.subscriptionConfiguration = subscriptionConfiguration;
    }

    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subscription_configuration_id", referencedColumnName="id", updatable = false)
	public SubscriptionConfiguration getSubscriptionConfiguration() {
		return subscriptionConfiguration;
	}

	public void setSubscriptionConfiguration(SubscriptionConfiguration subscriptionConfiguration) {
		this.subscriptionConfiguration = subscriptionConfiguration;
	}
}
