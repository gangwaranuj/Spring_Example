package com.workmarket.service.business.dto.account.pricing.subscription;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentTier;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionType;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;

public class SubscriptionConfigurationFormDTO {
	@NotNull
	private Long companyId;
	private User creator;

	private Long subscriptionConfigurationId;
	private Integer effectiveDateMonth;
	private Integer effectiveDateYear;
	private Integer subscriptionPeriod = SubscriptionPeriod.MONTHLY.getMonths();
	private Integer numberOfMonths = 12;
	private Integer paymentTermsDays = 30;
	private Boolean vendorOfRecord = Boolean.FALSE;
	private List<SubscriptionPaymentTierDTO> pricingRanges = Lists.newArrayList();
	private List<AccountServiceTypeDTO> accountServiceTypeDTOs = Lists.newArrayList();

	@NotNull
	private String subscriptionTypeCode;
	private BigDecimal blockTierPercentage;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Calendar signedDate;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Calendar paymentTierEffectiveDate;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Calendar addOnsEffectiveDate;

	@NotNull
	private Boolean hasDiscountOptions = Boolean.FALSE;

	private Integer discountNumberOfPeriods = 0;
	private BigDecimal discountPerPeriod = BigDecimal.ZERO;
	private BigDecimal setUpFee = BigDecimal.ZERO;
	private Integer autoRenewal = 0;
	private String cancellationOption = StringUtils.EMPTY;
	private String clientRefId = StringUtils.EMPTY;

	@NotNull
	private Boolean hasAddOns = Boolean.FALSE;

	private List<SubscriptionAddOnDTO> subscriptionAddOnDTOs = Lists.newArrayList();
	private String additionalNotes = StringUtils.EMPTY;


	public Integer getEffectiveDateMonth() {
		return effectiveDateMonth;
	}

	public void setEffectiveDateMonth(Integer effectiveDateMonth) {
		this.effectiveDateMonth = effectiveDateMonth;
	}

	public Integer getEffectiveDateYear() {
		return effectiveDateYear;
	}

	public void setEffectiveDateYear(Integer effectiveDateYear) {
		this.effectiveDateYear = effectiveDateYear;
	}

	public Integer getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public void setSubscriptionPeriod(Integer subscriptionPeriod) {
		this.subscriptionPeriod = subscriptionPeriod;
	}

	public Integer getNumberOfMonths() {
		return numberOfMonths;
	}

	public void setNumberOfMonths(Integer numberOfMonths) {
		this.numberOfMonths = numberOfMonths;
	}

	public Boolean getVendorOfRecord() {
		return vendorOfRecord;
	}

	public void setVendorOfRecord(Boolean vendorOfRecord) {
		this.vendorOfRecord = vendorOfRecord;
	}

	public Boolean getHasDiscountOptions() {
		return hasDiscountOptions;
	}

	public void setHasDiscountOptions(Boolean hasDiscountOptions) {
		this.hasDiscountOptions = hasDiscountOptions;
	}

	public BigDecimal getDiscountPerPeriod() {
		return discountPerPeriod;
	}

	public void setDiscountPerPeriod(BigDecimal discountPerPeriod) {
		this.discountPerPeriod = discountPerPeriod;
	}

	public BigDecimal getSetUpFee() {
		return setUpFee;
	}

	public void setSetUpFee(BigDecimal setUpFee) {
		this.setUpFee = setUpFee;
	}

	public Integer getAutoRenewal() {
		return autoRenewal;
	}

	public void setAutoRenewal(Integer autoRenewal) {
		this.autoRenewal = autoRenewal;
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

	public Boolean getHasAddOns() {
		return hasAddOns;
	}

	public void setHasAddOns(Boolean hasAddOns) {
		this.hasAddOns = hasAddOns;
	}

	public String getAdditionalNotes() {
		return additionalNotes;
	}

	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}

	public List<SubscriptionPaymentTierDTO> getPricingRanges() {
		return pricingRanges;
	}

	public void setPricingRanges(List<SubscriptionPaymentTierDTO> pricingRanges) {
		this.pricingRanges = pricingRanges;
	}

	public List<SubscriptionAddOnDTO> getSubscriptionAddOnDTOs() {
		return subscriptionAddOnDTOs;
	}

	public void setSubscriptionAddOnDTOs(List<SubscriptionAddOnDTO> subscriptionAddOnDTOs) {
		this.subscriptionAddOnDTOs = subscriptionAddOnDTOs;
	}

	public Long getSubscriptionConfigurationId() {
		return subscriptionConfigurationId;
	}

	public void setSubscriptionConfigurationId(
			Long subscriptionConfigurationId) {
		this.subscriptionConfigurationId = subscriptionConfigurationId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Calendar getPaymentTierEffectiveDate() {
		return paymentTierEffectiveDate;
	}

	public void setPaymentTierEffectiveDate(Calendar paymentTierEffectiveDate) {
		this.paymentTierEffectiveDate = paymentTierEffectiveDate;
	}

	public Calendar getAddOnsEffectiveDate() {
		return addOnsEffectiveDate;
	}

	public void setAddOnsEffectiveDate(Calendar addOnsEffectiveDate) {
		this.addOnsEffectiveDate = addOnsEffectiveDate;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public List<AccountServiceTypeDTO> getAccountServiceTypeDTOs() {
		return accountServiceTypeDTOs;
	}

	public void setAccountServiceTypeDTOs(List<AccountServiceTypeDTO> accountServiceTypeDTOs) {
		this.accountServiceTypeDTOs = accountServiceTypeDTOs;
	}

	public Integer getDiscountNumberOfPeriods() {
		return discountNumberOfPeriods;
	}

	public void setDiscountNumberOfPeriods(Integer discountNumberOfPeriods) {
		this.discountNumberOfPeriods = discountNumberOfPeriods;
	}

	public Integer getPaymentTermsDays() {
		return paymentTermsDays;
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}

	public Calendar getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(Calendar signedDate) {
		this.signedDate = signedDate;
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

	public SubscriptionConfigurationDTO toSubscriptionConfigurationDTO() {
		SubscriptionConfigurationDTO subscription = new SubscriptionConfigurationDTO();
		// Set ID (if present)
		subscription.setSubscriptionConfigurationId(this.subscriptionConfigurationId);
	    subscription.setSignedDate(this.signedDate);

		// Set effective date
		if (this.effectiveDateYear != null && this.effectiveDateMonth != null
				&& this.numberOfMonths != null && this.subscriptionPeriod != null) {

			Calendar effectiveDate = Calendar.getInstance();
			effectiveDate.clear();
			effectiveDate.set(this.effectiveDateYear, this.effectiveDateMonth, 1);
			subscription.setEffectiveDate(effectiveDate);
		}

		// Set Payment Terms number of days
		if (this.paymentTermsDays != null && this.paymentTermsDays >= 30) {
			subscription.setPaymentTermsDays(this.paymentTermsDays);
		}

		// Set payment tier edition effective date
		if (this.getPaymentTierEffectiveDate() != null) {
			subscription.setPaymentTierEffectiveDate(this.paymentTierEffectiveDate);
		}

		// Set payment tiers
		List<SubscriptionPaymentTierDTO> tierDTOs = Lists.newArrayList();
		boolean lastTier = false;
		for (SubscriptionPaymentTierDTO tierDTO: this.pricingRanges) {
			// Replace null values
			if (tierDTO.getMinimum() == null) {
				tierDTO.setMinimum(BigDecimal.ZERO);
			}

			if (tierDTO.getPaymentAmount() == null) {
				tierDTO.setPaymentAmount(BigDecimal.ZERO);
			}

			if (tierDTO.getMaximum() == null) {
				tierDTO.setMaximum(SubscriptionPaymentTier.MAXIMUM);
				lastTier = true;
			}

			tierDTOs.add(tierDTO);

			if (lastTier)
				break;
		}
		subscription.setSubscriptionPaymentTierDTOs(tierDTOs);

		// Add-ons
		List<SubscriptionAddOnDTO> addOnDTOs = Lists.newArrayList();
		if (this.getHasAddOns()) {
			Calendar addOnsEffectiveDate = (this.getAddOnsEffectiveDate() != null) ? this.getAddOnsEffectiveDate() : subscription.getEffectiveDate();

			for (SubscriptionAddOnDTO dto: this.getSubscriptionAddOnDTOs()) {
				if (dto.getAddOnTypeCode() != null && dto.getCostPerPeriod() != null) {
					dto.setEffectiveDate(addOnsEffectiveDate);
					addOnDTOs.add(dto);
				}
			}
		}
		subscription.setSubscriptionAddOnDTOs(addOnDTOs);

		// Service type configurations
		List<AccountServiceTypeDTO> serviceTypeDTOs = Lists.newArrayList();
		for (AccountServiceTypeDTO serviceTypeConfig: this.accountServiceTypeDTOs) {
			AccountServiceTypeDTO dto = new AccountServiceTypeDTO();

			if (serviceTypeConfig.getAccountServiceTypeCode() != null && serviceTypeConfig.getCountryCode() != null) {
				dto.setAccountServiceTypeCode(serviceTypeConfig.getAccountServiceTypeCode());
				dto.setCountryCode(serviceTypeConfig.getCountryCode());
				serviceTypeDTOs.add(dto);
			}
		}
		subscription.setAccountServiceTypeDTOs(serviceTypeDTOs);

		// Set additional notes
		if (StringUtilities.isNotEmpty(this.additionalNotes)) {
			List<NoteDTO> notesDTOs = Lists.newArrayList( new NoteDTO(this.additionalNotes) );

			subscription.setSubscriptionNoteDTOs(notesDTOs);
		}

		// Set client ref id
		if (StringUtilities.isNotEmpty(this.clientRefId)) {
			subscription.setClientRefId(this.clientRefId);
		}

		// Set cancellation notes
		if (StringUtilities.isNotEmpty(this.cancellationOption)) {
			subscription.setCancellationOption(this.cancellationOption);
		}

		subscription.setVendorOfRecord(this.vendorOfRecord);
		subscription.setSetUpFee(this.setUpFee);
		subscription.setDiscountedPeriods(this.discountNumberOfPeriods > 0 ? this.discountNumberOfPeriods : 0);
		subscription.setSubscriptionPeriod(SubscriptionPeriod.getSubscriptionPeriod(this.subscriptionPeriod));
		subscription.setNumberOfPeriods(this.numberOfMonths / this.subscriptionPeriod);
		subscription.setNumberOfRenewals(this.autoRenewal);
		subscription.setCancellationOption(this.cancellationOption);
		subscription.setDiscountedAmountPerPeriod(this.discountPerPeriod);

		if (SubscriptionType.SUBSCRIPTION_TYPE_CODES.contains(this.getSubscriptionTypeCode())) {
			subscription.setSubscriptionTypeCode(this.getSubscriptionTypeCode());
			if (this.getSubscriptionTypeCode().equals(SubscriptionType.BLOCK)) {
				subscription.setBlockTierPercentage(this.getBlockTierPercentage());
			}
		}

		return subscription;
	}
}
