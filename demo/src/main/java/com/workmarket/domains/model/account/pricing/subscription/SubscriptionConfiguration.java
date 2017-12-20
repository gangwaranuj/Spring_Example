package com.workmarket.domains.model.account.pricing.subscription;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.ApprovableVerifiableEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.pricing.SubscriptionAccountServiceTypeConfiguration;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.note.SubscriptionNote;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;

/**
 * Author: rocio
 */
@Entity(name = "subscriptionConfiguration")
@Table(name = "subscription_configuration")
@AuditChanges
public class SubscriptionConfiguration extends ApprovableVerifiableEntity {

	private static final long serialVersionUID = 1L;

	private Company company;
	private Calendar effectiveDate;
	private Calendar signedDate;
	private SubscriptionPeriod subscriptionPeriod = SubscriptionPeriod.MONTHLY;
	private Integer numberOfPeriods = 12;
	private Calendar endDate;
	private Integer discountedPeriods = 0;
	private BigDecimal discountedAmountPerPeriod = BigDecimal.ZERO;
	private BigDecimal setUpFee = BigDecimal.ZERO;
	private String cancellationOption;
	private String clientRefId;
	private Set<SubscriptionAddOnTypeAssociation> subscriptionAddOns = Sets.newHashSet();
	private Set<SubscriptionAddOnTypeAssociation> activeSubscriptionAddOns = Sets.newHashSet();
	private Set<SubscriptionNote> notes = Sets.newHashSet();
	private Set<SubscriptionFeeConfiguration> subscriptionFeeConfigurations = Sets.newHashSet();
	private SubscriptionStatusType subscriptionStatusType = new SubscriptionStatusType(SubscriptionStatusType.PENDING);
	private Calendar approvedOn;
	private User approvedBy;
	private Calendar lastThroughputUpperBoundReachedOn;
	private Integer pastPaymentPeriods = 0;
	private Calendar nextPaymentPeriodStartDate;
	private SubscriptionCancellation subscriptionCancellation;
	private Set<SubscriptionAccountServiceTypeConfiguration> accountServiceTypeConfigurations = Sets.newHashSet();
	private Integer numberOfRenewals = 0;
	private Integer paymentTermsDays = 30;
	private SubscriptionConfiguration parentSubscription;
	private Calendar nextThroughputResetDate;

	// Transient properties
	private SubscriptionUtilities.APPROVAL_TYPE approvalType = SubscriptionUtilities.APPROVAL_TYPE.NEW;

	public SubscriptionConfiguration() {
	}

	public SubscriptionConfiguration(Company company) {
		this.company = company;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "cancellation_option")
	public String getCancellationOption() {
		return cancellationOption;
	}

	public void setCancellationOption(String cancellationOption) {
		this.cancellationOption = cancellationOption;
	}

	@Column(name = "client_ref_id")
	public String getClientRefId() {
		return clientRefId;
	}

	public void setClientRefId(String clientRefId) {
		this.clientRefId = clientRefId;
	}

	@Column(name = "effective_date", nullable = false)
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Column(name = "signed_date")
	public Calendar getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(Calendar signedDate) {
		this.signedDate = signedDate;
	}

	@Column(name = "end_date", nullable = false)
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	@Fetch(FetchMode.JOIN)
	@OneToMany
	@JoinColumn(name = "subscription_configuration_id")
	@Where(clause = "deleted = 0")
	public Set<SubscriptionNote> getNotes() {
		return notes;
	}

	public void setNotes(Set<SubscriptionNote> notes) {
		this.notes = notes;
	}

	@Column(name = "number_of_periods", nullable = false)
	public Integer getNumberOfPeriods() {
		return numberOfPeriods;
	}

	public void setNumberOfPeriods(Integer numberOfPeriods) {
		this.numberOfPeriods = numberOfPeriods;
	}

	@Column(name = "set_up_fee", nullable = false)
	public BigDecimal getSetUpFee() {
		return setUpFee;
	}

	public void setSetUpFee(BigDecimal setUpFee) {
		this.setUpFee = setUpFee;
	}

	@OneToMany
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "subscription_configuration_id")
	@Where(clause = "deleted = 0")
	public Set<SubscriptionAddOnTypeAssociation> getSubscriptionAddOns() {
		return subscriptionAddOns;
	}

	public void setSubscriptionAddOns(Set<SubscriptionAddOnTypeAssociation> subscriptionAddOns) {
		this.subscriptionAddOns = subscriptionAddOns;
	}

	@OneToMany
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "subscription_configuration_id")
	@Where(clause = "active = 1 AND deleted = 0")
	public Set<SubscriptionAddOnTypeAssociation> getActiveSubscriptionAddOns() {
		return activeSubscriptionAddOns;
	}

	public void setActiveSubscriptionAddOns(Set<SubscriptionAddOnTypeAssociation> activeSubscriptionAddOns) {
		this.activeSubscriptionAddOns = activeSubscriptionAddOns;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "subscription_period", nullable = false)
	public SubscriptionPeriod getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public void setSubscriptionPeriod(SubscriptionPeriod subscriptionPeriod) {
		this.subscriptionPeriod = subscriptionPeriod;
	}

	@Column(name = "discounted_amount_per_period", nullable = false)
	public BigDecimal getDiscountedAmountPerPeriod() {
		return discountedAmountPerPeriod;
	}

	public void setDiscountedAmountPerPeriod(BigDecimal discountedAmountPerPeriod) {
		this.discountedAmountPerPeriod = discountedAmountPerPeriod;
	}

	@Column(name = "discounted_periods", nullable = false)
	public Integer getDiscountedPeriods() {
		return discountedPeriods;
	}

	public void setDiscountedPeriods(Integer discountedPeriods) {
		this.discountedPeriods = discountedPeriods;
	}

	@Fetch(FetchMode.JOIN)
	@OneToMany
	@JoinColumn(name = "subscription_configuration_id")
	public Set<SubscriptionFeeConfiguration> getSubscriptionFeeConfigurations() {
		return subscriptionFeeConfigurations;
	}

	public void setSubscriptionFeeConfigurations(Set<SubscriptionFeeConfiguration> subscriptionFeeConfigurations) {
		this.subscriptionFeeConfigurations = subscriptionFeeConfigurations;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "subscription_status_type_code", referencedColumnName = "code")
	public SubscriptionStatusType getSubscriptionStatusType() {
		return subscriptionStatusType;
	}

	public void setSubscriptionStatusType(SubscriptionStatusType subscriptionStatusType) {
		this.subscriptionStatusType = subscriptionStatusType;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "approved_by")
	public User getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	@Column(name = "approved_on")
	public Calendar getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Calendar approvedOn) {
		this.approvedOn = approvedOn;
	}

	@Column(name = "last_throughput_upper_bound_reached_on")
	public Calendar getLastThroughputUpperBoundReachedOn() {
		return lastThroughputUpperBoundReachedOn;
	}

	public void setLastThroughputUpperBoundReachedOn(Calendar lastThroughputUpperBoundReachedOn) {
		this.lastThroughputUpperBoundReachedOn = lastThroughputUpperBoundReachedOn;
	}

	@Column(name = "past_payment_periods", nullable = false)
	public Integer getPastPaymentPeriods() {
		return pastPaymentPeriods;
	}

	public void setPastPaymentPeriods(Integer pastPaymentPeriods) {
		this.pastPaymentPeriods = pastPaymentPeriods;
	}

	@Column(name = "next_payment_period_start_date", nullable = true)
	public Calendar getNextPaymentPeriodStartDate() {
		return nextPaymentPeriodStartDate;
	}

	public void setNextPaymentPeriodStartDate(Calendar nextPaymentPeriodStartDate) {
		this.nextPaymentPeriodStartDate = nextPaymentPeriodStartDate;
	}

	@OneToOne(mappedBy = "subscriptionConfiguration")
	public SubscriptionCancellation getSubscriptionCancellation() {
		return subscriptionCancellation;
	}

	public void setSubscriptionCancellation(SubscriptionCancellation subscriptionCancellation) {
		this.subscriptionCancellation = subscriptionCancellation;
	}

	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "subscriptionConfiguration")
	public Set<SubscriptionAccountServiceTypeConfiguration> getAccountServiceTypeConfigurations() {
		return accountServiceTypeConfigurations;
	}

	public void setAccountServiceTypeConfigurations(Set<SubscriptionAccountServiceTypeConfiguration> accountServiceTypeConfigurations) {
		this.accountServiceTypeConfigurations = accountServiceTypeConfigurations;
	}

	@Column(name = "number_of_renewals")
	public Integer getNumberOfRenewals() {
		return numberOfRenewals;
	}

	public void setNumberOfRenewals(Integer numberOfRenewals) {
		this.numberOfRenewals = numberOfRenewals;
	}

	@Column(name = "payment_terms_days", nullable = false)
	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}

	@Column(name = "next_throughput_reset_date")
	public Calendar getNextThroughputResetDate() {
		return nextThroughputResetDate;
	}

	public void setNextThroughputResetDate(Calendar nextThroughputResetDate) {
		this.nextThroughputResetDate = nextThroughputResetDate;
	}

	@Fetch(FetchMode.JOIN)
	@OneToOne
	@JoinColumn(name = "parent_subscription_id", referencedColumnName = "id")
	public SubscriptionConfiguration getParentSubscription() {
		return parentSubscription;
	}

	public void setParentSubscription(SubscriptionConfiguration parentSubscription) {
		// Don't allow self references
		if (parentSubscription != null && parentSubscription.getId() != null
				&& !parentSubscription.getId().equals(this.getId()) ) {
			this.parentSubscription = parentSubscription;
		}
	}

	@Transient
	public SubscriptionFeeConfiguration getActiveSubscriptionFeeConfiguration() {
		for (SubscriptionFeeConfiguration subscriptionFeeConfiguration : getSubscriptionFeeConfigurations()) {
			if (subscriptionFeeConfiguration.getActive()) {
				return subscriptionFeeConfiguration;
			}
		}
		return null;
	}

	@Transient
	public Set<SubscriptionFeeConfiguration> getApprovedSubscriptionFeeConfigurations() {
		final Set<SubscriptionFeeConfiguration> approvedSubscriptions = Sets.newHashSet();
		for (SubscriptionFeeConfiguration subscriptionFeeConfiguration : getSubscriptionFeeConfigurations()) {
			if (subscriptionFeeConfiguration.isApproved()) {
				approvedSubscriptions.add(subscriptionFeeConfiguration);
			}
		}
		return approvedSubscriptions;
	}

	@Transient
	public SubscriptionFeeConfiguration getLatestPendingApprovalFeeConfiguration() {
		for (SubscriptionFeeConfiguration subscriptionFeeConfiguration : getSubscriptionFeeConfigurations()) {
			if (subscriptionFeeConfiguration.isPendingApproval()) {
				return subscriptionFeeConfiguration;
			}
		}
		return null;
	}

	@Transient
	public SubscriptionAccountServiceTypeConfiguration findAccountServiceTypeConfigurationForCountry(String countryId) {
		if (CollectionUtils.isNotEmpty(accountServiceTypeConfigurations)) {
			for (SubscriptionAccountServiceTypeConfiguration c : accountServiceTypeConfigurations) {
				if (c.getCountry().getId().equals(countryId)) {
					return c;
				}
			}
		}
		return null;
	}

	@Transient
	public boolean hasDiscount() {
		return (discountedPeriods != null) && (discountedAmountPerPeriod != null)
				&& (discountedPeriods > 0) && (discountedAmountPerPeriod.compareTo(BigDecimal.ZERO) > 0);
	}

	@Transient
	public boolean isDiscountApplicable() {
		return hasDiscount() && (pastPaymentPeriods.compareTo(discountedPeriods) < 0);
	}

	@Transient
	public boolean hasAddOns() {
		return CollectionUtils.isNotEmpty(subscriptionAddOns);
	}

	@Transient
	public boolean hasActiveAddOns() {
		return CollectionUtils.isNotEmpty(activeSubscriptionAddOns);
	}

	@Transient
	public boolean hasPendingApprovalAddOns() {
		return CollectionUtils.isNotEmpty(select(getSubscriptionAddOns(), having(on(SubscriptionAddOnTypeAssociation.class).isPendingApproval())));
	}

	@Transient
	public boolean hasPendingApprovalFeeConfigurations() {
		return CollectionUtils.isNotEmpty(select(getSubscriptionFeeConfigurations(), having(on(SubscriptionFeeConfiguration.class).isPendingApproval())));
	}

	@Transient
	public boolean hasPendingApprovalEditions(){
		return hasPendingApprovalAddOns() || hasPendingApprovalFeeConfigurations();
	}

	@Transient
	public boolean hasSetupFee() {
		if (setUpFee != null) {
			return setUpFee.compareTo(BigDecimal.ZERO) > 0 && pastPaymentPeriods == 0;
		}
		return false;
	}

	@Transient
	public boolean hasNotes() {
		return CollectionUtils.isNotEmpty(notes);
	}

	@Transient
	public boolean isPending() {
		return subscriptionStatusType.isPending();
	}

	@Transient
	public boolean isActive() {
		return subscriptionStatusType.isActive();
	}

	@Transient
	public boolean isCancelled() {
		return subscriptionStatusType.isCancelled();
	}

	@Transient
	public boolean isExpired() {
		return subscriptionStatusType.isExpired();
	}

	@Transient
	public boolean isRejected() {
		return subscriptionStatusType.isRejected();
	}

	@Transient
	public boolean isPendingRenewal() {
		return subscriptionStatusType.isPendingRenewal();
	}

	@Transient
	public User getCancelledBy() {
		if (subscriptionCancellation != null) {
			return subscriptionCancellation.getApprovedBy();
		}
		return null;
	}

	@Transient
	public Calendar getCancelledOn() {
		if (subscriptionCancellation != null) {
			return subscriptionCancellation.getApprovedOn();
		}
		return null;
	}

	@Transient
	public BigDecimal getCancellationFee() {
		if (subscriptionCancellation != null) {
			return subscriptionCancellation.getCancellationFee();
		}
		return null;
	}

	@Transient
	public SubscriptionInvoice getCancellationInvoice() {
		if (subscriptionCancellation != null) {
			return subscriptionCancellation.getCancellationInvoice();
		}
		return null;
	}

	@Transient
	public boolean hasEffectiveSubscriptionAddOns(Calendar date) {
		for (SubscriptionAddOnTypeAssociation addOn : getSubscriptionAddOns()) {
			//If there's at least one add-on where the effective date is in the past or equals then there are effective add-ons
			if (!addOn.getEffectiveDate().after(date) && addOn.isApproved()) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public List<SubscriptionPaymentTier> getSubscriptionPaymentTiers() {
		SubscriptionFeeConfiguration feeConfiguration;
		if (this.isPending() || this.isNotReady()) {
			feeConfiguration = this.getLatestPendingApprovalFeeConfiguration();
		} else {
			feeConfiguration = this.getActiveSubscriptionFeeConfiguration();
		}
		if (feeConfiguration != null && feeConfiguration.getSubscriptionPaymentTiers() != null) {
			return feeConfiguration.getSubscriptionPaymentTiers();
		}
		return Lists.newArrayList();
	}

	@Transient
	public List<SubscriptionAddOnTypeAssociation> getAddOns() {
		if (this.isPending() || this.isNotReady()) {
			Set<SubscriptionAddOnTypeAssociation> addOnAssociations = Sets.filter(this.getSubscriptionAddOns(), new Predicate<SubscriptionAddOnTypeAssociation>() {
				@Override
				public boolean apply(SubscriptionAddOnTypeAssociation addOnAssociation) {
					return addOnAssociation.isPendingApproval();
				}
			});

			return Lists.newArrayList(addOnAssociations);
		} else {
			return Lists.newArrayList(this.getActiveSubscriptionAddOns());
		}
	}

	@Transient
	public void setAddOns(Set<SubscriptionAddOnTypeAssociation> addOns) {
		if (this.isPending() || this.isNotReady()) {
			this.setSubscriptionAddOns(addOns);
		} else {
			this.setActiveSubscriptionAddOns(addOns);
		}
	}

	@Transient
	public boolean hasAccountServiceType(String accountServiceTypeCode) {
		if (StringUtils.isNotBlank(accountServiceTypeCode)) {
			if (CollectionUtils.isNotEmpty(accountServiceTypeConfigurations)) {
				for (SubscriptionAccountServiceTypeConfiguration c : accountServiceTypeConfigurations) {
					if (accountServiceTypeCode.equals(c.getAccountServiceType().getCode()))
						return true;
				}
			}
		}

		return false;
	}

	@Transient
	public boolean isVendorOfRecord() {
		if (CollectionUtils.isNotEmpty(accountServiceTypeConfigurations)) {
			for (SubscriptionAccountServiceTypeConfiguration c : accountServiceTypeConfigurations) {
				if (c.getAccountServiceType().isVendorOfRecord())
					return true;
			}
		}
		return false;
	}

	@Transient
	public Integer getTermsInMonths() {
		return numberOfPeriods * subscriptionPeriod.getMonths();
	}

	@Transient
	public BigDecimal getTotalDiscount() {
		if (hasDiscount()) {
			return discountedAmountPerPeriod.multiply(BigDecimal.valueOf(discountedPeriods));
		}
		return BigDecimal.ZERO;
	}

	@Transient
	public BigDecimal getTotalAddOnsFee() {
		BigDecimal totalAddOns = BigDecimal.ZERO;
		for (SubscriptionAddOnTypeAssociation subscriptionAddOnTypeAssociation : getSubscriptionAddOns()) {
			totalAddOns = totalAddOns.add(subscriptionAddOnTypeAssociation.getCostPerPeriod());
		}
		return totalAddOns;
	}

	@Transient
	public BigDecimal getTotalActiveAddOnsFeeByEffectiveDate(Calendar date) {
		BigDecimal totalAddOns = BigDecimal.ZERO;
		for (SubscriptionAddOnTypeAssociation subscriptionAddOnTypeAssociation : getSubscriptionAddOns()) {
			if (!subscriptionAddOnTypeAssociation.getEffectiveDate().after(date) && subscriptionAddOnTypeAssociation.isApproved()) {
				totalAddOns = totalAddOns.add(subscriptionAddOnTypeAssociation.getCostPerPeriod());
			}
		}
		return totalAddOns;
	}

	@Transient
	public Calendar calculateEndDate() {
		if (getEffectiveDate() != null && getNumberOfPeriods() != null && getSubscriptionPeriod() != null) {
    		Calendar calculatedEndDate = DateUtilities.cloneCalendar(this.getEffectiveDate());
    		calculatedEndDate.add(Calendar.MONTH, getNumberOfPeriods() * getSubscriptionPeriod().getMonths());
    		return calculatedEndDate;
		}
		return endDate;
	}

	@Transient
	public SubscriptionUtilities.APPROVAL_TYPE getApprovalType() {
	    return approvalType;
    }

	public void setApprovalType(SubscriptionUtilities.APPROVAL_TYPE approvalType) {
	    this.approvalType = approvalType;
    }


	@Transient
	public boolean isSubscriptionResettingOnTransactionDate(Calendar transactionDate) {
		if (transactionDate != null) {
			return (nextThroughputResetDate != null
					&& !nextThroughputResetDate.after(transactionDate)
					&& !nextThroughputResetDate.equals(endDate));
		}
		return false;
	}
}
