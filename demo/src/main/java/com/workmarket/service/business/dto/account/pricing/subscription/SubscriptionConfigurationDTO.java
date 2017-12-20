package com.workmarket.service.business.dto.account.pricing.subscription;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.utility.BeanUtilities;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * Author: rocio
 */
public class SubscriptionConfigurationDTO {

	private Long subscriptionConfigurationId;
	private Calendar effectiveDate;
	private Calendar signedDate;
	private SubscriptionPeriod subscriptionPeriod = SubscriptionPeriod.MONTHLY;
	private Integer numberOfPeriods = 12;
	private Calendar endDate;
	private boolean vendorOfRecord = false;
	private BigDecimal setUpFee = BigDecimal.ZERO;
	private Integer numberOfRenewals = 0;
	private Integer numberOfRenewalPeriods = 0;
	private String cancellationOption;
	private String clientRefId;
	private List<SubscriptionAddOnDTO> subscriptionAddOnDTOs = Lists.newArrayList();
	private List<SubscriptionPaymentTierDTO> subscriptionPaymentTierDTOs = Lists.newArrayList();
	private List<AccountServiceTypeDTO> accountServiceTypeDTOs = Lists.newArrayList();
	private List<NoteDTO> subscriptionNoteDTOs = Lists.newArrayList();
	private Calendar paymentTierEffectiveDate;
	private Integer discountedPeriods = 0;
	private BigDecimal discountedAmountPerPeriod = BigDecimal.ZERO;
	private Integer paymentTermsDays = 30;
	private String subscriptionTypeCode;
	private BigDecimal blockTierPercentage;

	public SubscriptionConfigurationDTO() {
	}

	public SubscriptionConfigurationDTO(SubscriptionConfiguration subscriptionConfiguration) {
		BeanUtilities.copyProperties(this, subscriptionConfiguration);
		this.setSubscriptionTypeCode(SubscriptionType.BAND);
		this.setSubscriptionConfigurationId(subscriptionConfiguration.getId());
	}

	public Long getSubscriptionConfigurationId() {
		return subscriptionConfigurationId;
	}

	public void setSubscriptionConfigurationId(Long subscriptionConfigurationId) {
		this.subscriptionConfigurationId = subscriptionConfigurationId;
	}

	public String getCancellationOption() {
		return cancellationOption;
	}

	public void setCancellationOption(String cancellationOption) {
		this.cancellationOption = cancellationOption;
	}

	public String getClientRefId() {
		return clientRefId;
	}

	public void setClientRefId(String clientRefId) {
		this.clientRefId = clientRefId;
	}

	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Calendar getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(Calendar signedDate) {
		this.signedDate = signedDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public Integer getNumberOfPeriods() {
		return numberOfPeriods;
	}

	public void setNumberOfPeriods(Integer numberOfPeriods) {
		this.numberOfPeriods = numberOfPeriods;
	}

	public BigDecimal getSetUpFee() {
		return setUpFee;
	}

	public void setSetUpFee(BigDecimal setUpFee) {
		this.setUpFee = setUpFee;
	}

	public SubscriptionPeriod getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public void setSubscriptionPeriod(SubscriptionPeriod subscriptionPeriod) {
		this.subscriptionPeriod = subscriptionPeriod;
	}

	public void setSubscriptionPeriod(Integer numberOfMonthsPerPeriod) {
		SubscriptionPeriod subscription = SubscriptionPeriod.getSubscriptionPeriod(numberOfMonthsPerPeriod);
		if (subscription != null) {
			this.subscriptionPeriod = subscription;
		}
	}

	public boolean isVendorOfRecord() {
		return vendorOfRecord;
	}

	public void setVendorOfRecord(boolean vendorOfRecord) {
		this.vendorOfRecord = vendorOfRecord;
	}

	public List<SubscriptionAddOnDTO> getSubscriptionAddOnDTOs() {
		return subscriptionAddOnDTOs;
	}

	public void setSubscriptionAddOnDTOs(List<SubscriptionAddOnDTO> subscriptionAddOnDTOs) {
		this.subscriptionAddOnDTOs = subscriptionAddOnDTOs;
	}

	public List<SubscriptionPaymentTierDTO> getSubscriptionPaymentTierDTOs() {
		return subscriptionPaymentTierDTOs;
	}

	public void setSubscriptionPaymentTierDTOs(List<SubscriptionPaymentTierDTO> subscriptionPaymentTierDTOs) {
		this.subscriptionPaymentTierDTOs = subscriptionPaymentTierDTOs;
	}

	public List<NoteDTO> getSubscriptionNoteDTOs() {
		return subscriptionNoteDTOs;
	}

	public void setSubscriptionNoteDTOs(List<NoteDTO> subscriptionNoteDTOs) {
		this.subscriptionNoteDTOs = subscriptionNoteDTOs;
	}

	public BigDecimal getDiscountedAmountPerPeriod() {
		return discountedAmountPerPeriod;
	}

	public void setDiscountedAmountPerPeriod(BigDecimal discountedAmountPerPeriod) {
		this.discountedAmountPerPeriod = discountedAmountPerPeriod;
	}

	public Integer getDiscountedPeriods() {
		return discountedPeriods;
	}

	public void setDiscountedPeriods(Integer discountedPeriods) {
		this.discountedPeriods = discountedPeriods;
	}

	public SubscriptionConfigurationDTO addToSubscriptionPaymentTierDTOs(SubscriptionPaymentTierDTO dto) {
		if (subscriptionPaymentTierDTOs == null) {
			setSubscriptionPaymentTierDTOs(Lists.newArrayList(dto));
		} else {
			subscriptionPaymentTierDTOs.add(dto);
		}
		return this;
	}

	public SubscriptionConfigurationDTO addToSubscriptionAddOnDTOs(SubscriptionAddOnDTO dto) {
		if (subscriptionAddOnDTOs == null) {
			setSubscriptionAddOnDTOs(Lists.newArrayList(dto));
		} else {
			subscriptionAddOnDTOs.add(dto);
		}
		return this;
	}

	public SubscriptionConfigurationDTO addToSubscriptionNoteDTOs(NoteDTO dto) {
		if (subscriptionNoteDTOs == null) {
			setSubscriptionNoteDTOs(Lists.newArrayList(dto));
		} else {
			subscriptionNoteDTOs.add(dto);
		}
		return this;
	}

	public boolean hasNotes() {
		return (subscriptionNoteDTOs != null && !subscriptionNoteDTOs.isEmpty());
	}

	public boolean hasAddOns() {
		return (subscriptionAddOnDTOs != null && !subscriptionAddOnDTOs.isEmpty());
	}

	public Calendar getPaymentTierEffectiveDate() {
		return paymentTierEffectiveDate;
	}

	public void setPaymentTierEffectiveDate(Calendar paymentTierEffectiveDate) {
		this.paymentTierEffectiveDate = paymentTierEffectiveDate;
	}

	public List<AccountServiceTypeDTO> getAccountServiceTypeDTOs() {
	    return accountServiceTypeDTOs;
    }

	public void setAccountServiceTypeDTOs(List<AccountServiceTypeDTO> accountServiceTypeDTOs) {
	    this.accountServiceTypeDTOs = accountServiceTypeDTOs;
    }

	public Integer getNumberOfRenewals() {
		return numberOfRenewals;
	}

	public void setNumberOfRenewals(Integer numberOfRenewals) {
		this.numberOfRenewals = numberOfRenewals;
	}

	public void addToAccountServiceTypeDTO(AccountServiceTypeDTO dto) {
		accountServiceTypeDTOs.add(dto);
	}

	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}

	public Integer getNumberOfRenewalPeriods() {
	    return numberOfRenewalPeriods;
    }

	public void setNumberOfRenewalPeriods(Integer numberOfRenewalPeriods) {
	    this.numberOfRenewalPeriods = numberOfRenewalPeriods;
    }

	public String getSubscriptionTypeCode() {
		return subscriptionTypeCode;
	}

	public void setSubscriptionTypeCode(String subscriptionTypeCode) {
		this.subscriptionTypeCode = subscriptionTypeCode;
	}

	public BigDecimal getBlockTierPercentage() {
		return blockTierPercentage;
	}

	public void setBlockTierPercentage(BigDecimal blockTierPercentage) {
		this.blockTierPercentage = blockTierPercentage;
	}

	public boolean isBlockSubscription() {
		return SubscriptionType.BLOCK.equals(subscriptionTypeCode);
	}
}
