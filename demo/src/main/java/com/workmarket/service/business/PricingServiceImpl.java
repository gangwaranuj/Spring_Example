package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.account.RegisterTransactionCostDAO;
import com.workmarket.dao.account.WorkFeeConfigurationDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkPrice;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransactionCost;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkFeeBand;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.pricing.PricingStrategyUtilities;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkPriceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionFactory;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.RegisterTransactionCostDTO;
import com.workmarket.service.business.dto.WorkFeeBandDTO;
import com.workmarket.service.business.dto.WorkFeeConfigurationDTO;
import com.workmarket.service.exception.account.InvalidPricingException;
import com.workmarket.service.exception.account.OverAPLimitException;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
public class PricingServiceImpl implements PricingService {

	private static final Log logger = LogFactory.getLog(PricingServiceImpl.class);
	private static final BigDecimal LOWEST_WORK_FEE_PERCENTAGE = BigDecimal.ZERO;

	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterServicePrefundImpl;
	@Autowired private RegisterTransactionCostDAO registerTransactionCostDAO;
	@Autowired private AccountRegisterDAO accountRegisterDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private WorkFeeConfigurationDAO workFeeConfigurationDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private WorkPriceDAO workPriceDAO;
	@Autowired private BaseWorkDAO baseWorkDAO;
	@Autowired private RegisterTransactionFactory registerTransactionFactory;
	@Autowired private WorkService workService;

	/**
	 * This is the total cost to the buyer
	 *
	 * @param work
	 * @param hoursWorked
	 * @param unitsProcessed
	 * @param overridePrice
	 * @param additionalExpenses
	 * @return
	 * @throws InvalidPricingException
	 */
	private BigDecimal calculateTotalResourceCost(
			Work work,
			BigDecimal hoursWorked,
			BigDecimal unitsProcessed,
			BigDecimal overridePrice,
			BigDecimal additionalExpenses,
			BigDecimal bonus) throws InvalidPricingException {

		Assert.notNull(work);
		Assert.notNull(work.getPricingStrategy());

		if (overridePrice != null) {
			return overridePrice;
		}

		if (work.getPricingStrategy() instanceof InternalPricingStrategy) {
			return BigDecimal.ZERO;
		}

		return calculateTotalResourceCostWithoutOverride(work, hoursWorked, unitsProcessed, additionalExpenses, bonus);
	}

	@Override
	public BigDecimal calculateTotalResourceCostWithoutOverride(AbstractWork work, WorkResource workResource) {
		Assert.notNull(work);
		Assert.notNull(work.getPricingStrategy());
		Assert.notNull(workResource);

		return calculateTotalResourceCostWithoutOverride(
			work,
			workResource.getHoursWorked(),
			workResource.getUnitsProcessed(),
			workResource.getAdditionalExpenses(),
			workResource.getBonus()
		);
	}

	private BigDecimal calculateTotalResourceCostWithoutOverride(
		AbstractWork work, BigDecimal hoursWorked, BigDecimal unitsProcessed, BigDecimal additionalExpenses, BigDecimal bonus) {

		BigDecimal cost = work.getPricingStrategy().calculatePrice(work.getId(), hoursWorked, unitsProcessed);
		BigDecimal additionalCost = calculateAdditionalExpenseCost(work, additionalExpenses, bonus);
		return cost.add(additionalCost);
	}

	private BigDecimal calculateAdditionalExpenseCost(AbstractWork work, BigDecimal additionalExpenses, BigDecimal bonus) {
		BigDecimal cost = BigDecimal.ZERO;

		boolean hasCompletionExpenseOverride = false;

		BigDecimal totalExpenseAmt = NumberUtilities.defaultValue(work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses());
		BigDecimal totalBonusAmt = NumberUtilities.defaultValue(work.getPricingStrategy().getFullPricingStrategy().getBonus());
		BigDecimal preCompleteExpenseAmt = BigDecimal.ZERO;
		BigDecimal postCompleteExpenseAmt = BigDecimal.ZERO;
		BigDecimal postCompleteBonusAmt = BigDecimal.ZERO;

		/* Handle post-completion expenses/bonuses - need to add any expenses/bonuses approved during Complete since they
		 * are not included in the total cost. If the user overrides the value, we also need to back out all the SLIs
		 * that happened before completion and and rebuild the final cost.
		 */
		if (work.isComplete() || work.isPaymentPending() || work.isPaid() || work.isClosed()) {
			List<WorkExpenseNegotiation> prevExpenseNegotiations = workNegotiationService.findPreCompletionExpenseIncreasesForWork(work.getId());
			List<WorkBonusNegotiation> prevBonusNegotiations = workNegotiationService.findPreCompletionBonusesForWork(work.getId());
			BigDecimal preCompleteBonusAmt;

			// if there are pre-completion expenses of either type, unpack them and subtract them from the total amount
			// note that pre-completion expenses can exist in two forms (WorkExpenseNegotiations and additionalExpenses i.e. counteroffer/apply)
			if ((CollectionUtils.isNotEmpty(prevExpenseNegotiations) || NumberUtilities.isPositive(additionalExpenses))
				|| (CollectionUtils.isNotEmpty(prevBonusNegotiations) || NumberUtilities.isPositive(totalBonusAmt))) {

				preCompleteExpenseAmt = calculatePreCompleteExpenseCost(prevExpenseNegotiations);
				preCompleteBonusAmt = calculatePreCompleteBonusCost(prevBonusNegotiations);

				hasCompletionExpenseOverride = NumberUtilities.isPositive(additionalExpenses) && additionalExpenses.compareTo(preCompleteExpenseAmt) == -1;

				if (NumberUtilities.isPositive(totalExpenseAmt)) {
					if (hasCompletionExpenseOverride) {
						postCompleteExpenseAmt = totalExpenseAmt.subtract(preCompleteExpenseAmt.subtract(additionalExpenses));
					} else {
						postCompleteExpenseAmt = totalExpenseAmt.subtract(preCompleteExpenseAmt);
					}
				}

				if (NumberUtilities.isPositive(totalBonusAmt)) {
					postCompleteBonusAmt = totalBonusAmt.subtract(preCompleteBonusAmt);
				}

				cost = cost.add(postCompleteExpenseAmt);
				cost = cost.add(postCompleteBonusAmt);
			}
		}

		// if there are still leftover expenses not accounted for in overrides + post-completion, they are counteroffer/apply expenses
		// rather than do a separate query for these (ugh) we can back them out by process of elimination
		// we have to do this because they show up in additionalExpenses and would get double counted
		// it's a counteroffer if:
		// if additionalExpenses are less than the total (i.e. there were other pre-complete expenses added after the counteroffer)
		// OR
		// if additionalExpenses are more than the preComplete amount (i.e. there were NO pre-complete expenses added after counteroffer)
		boolean hasCounterOfferExpenses = !hasCompletionExpenseOverride // can't have both because the override would take precedence
			&& NumberUtilities.isPositive(additionalExpenses)
			&& (additionalExpenses.compareTo(totalExpenseAmt) == -1 || additionalExpenses.compareTo(preCompleteExpenseAmt) == 1);

		// add if the override or counteroffers weren't already accounted for, since additionalExpenses can contain either
		if (!hasCompletionExpenseOverride) {
			if (!hasCounterOfferExpenses && additionalExpenses != null) {
				cost = cost.add(additionalExpenses);
			} else if (NumberUtilities.isPositive(postCompleteExpenseAmt)) {
				cost = cost.add(totalExpenseAmt.subtract(postCompleteExpenseAmt));
			} else {
				cost = cost.add(totalExpenseAmt);
			}
		}
		if (bonus != null) {
			if (NumberUtilities.isPositive(postCompleteBonusAmt)) {
				cost = cost.add(totalBonusAmt.subtract(postCompleteBonusAmt));
			} else {
				cost = cost.add(bonus); // bonuses can't be overridden or set during counteroffer/apply
			}
		}
		return cost;
	}

	private BigDecimal calculatePreCompleteExpenseCost(List<WorkExpenseNegotiation> previousSLIs) {
		BigDecimal result = BigDecimal.ZERO;
		BigDecimal prevExpenseTotal = BigDecimal.ZERO;
		BigDecimal current = BigDecimal.ZERO;

		// the additionalExpenses are stored cumulatively so need to back out each one by subtracting from the previous
		for (WorkExpenseNegotiation sli : previousSLIs) {
			if (prevExpenseTotal.compareTo(BigDecimal.ZERO) == 0)
				result = sli.getFullPricingStrategy().getAdditionalExpenses();

			current = sli.getFullPricingStrategy().getAdditionalExpenses();
			BigDecimal diff = prevExpenseTotal.subtract(current);

			result = result.add(diff);
			prevExpenseTotal = current;
		}
		result = result.add(current);

		return result;
	}

	private BigDecimal calculatePreCompleteBonusCost(List<WorkBonusNegotiation> previousBonuses) {
		BigDecimal result = BigDecimal.ZERO;
		BigDecimal prevBonusTotal = BigDecimal.ZERO;
		BigDecimal current = BigDecimal.ZERO;

		// the bonuses are stored cumulatively so need to back out each one by subtracting from the previous
		for (WorkBonusNegotiation bonus : previousBonuses) {
			if (prevBonusTotal.compareTo(BigDecimal.ZERO) == 0)
				result = bonus.getFullPricingStrategy().getBonus();

			current = bonus.getFullPricingStrategy().getBonus();
			BigDecimal diff = prevBonusTotal.subtract(current);

			result = result.add(diff);
			prevBonusTotal = current;
		}
		result = result.add(current);

		return result;
	}

	/**
	 * This is the total maximum cost to the buyer
	 *
	 * @param strategy
	 * @return
	 */
	@Override
	public BigDecimal calculateMaximumResourceCost(PricingStrategy strategy) {
		Assert.notNull(strategy);

		BigDecimal cost = BigDecimal.ZERO;
		FullPricingStrategy fps = strategy.getFullPricingStrategy();

		// Add the additional expenses and bonus to the cost
		if (fps != null) {
			if (strategy.getFullPricingStrategy().getAdditionalExpenses() != null) {
				cost = cost.add(strategy.getFullPricingStrategy().getAdditionalExpenses());
			}
			if (strategy.getFullPricingStrategy().getBonus() != null) {
				cost = cost.add(strategy.getFullPricingStrategy().getBonus());
			}
		}

		// This is valid for draft work and templates. Return 0.
		if (strategy.isNull()) {
			return cost;
		}
		if (strategy instanceof FlatPricePricingStrategy) {
			FlatPricePricingStrategy s = (FlatPricePricingStrategy) strategy;
			cost = cost.add(s.getFlatPrice());
		} else if (strategy instanceof PerHourPricingStrategy) {
			PerHourPricingStrategy s = (PerHourPricingStrategy) strategy;
			cost = cost.add(s.getMaxNumberOfHours().multiply(s.getPerHourPrice()));
		} else if (strategy instanceof PerUnitPricingStrategy) {
			PerUnitPricingStrategy s = (PerUnitPricingStrategy) strategy;
			// We support 3 decimal places for unit price so we round to 2 decimal places here to get accurate cost
			cost = cost.add(s.getMaxNumberOfUnits().multiply(s.getPerUnitPrice())).setScale(2, RoundingMode.HALF_UP);
		} else if (strategy instanceof BlendedPerHourPricingStrategy) {
			BlendedPerHourPricingStrategy s = (BlendedPerHourPricingStrategy) strategy;
			cost = cost
					.add(s.getInitialNumberOfHours().multiply(s.getInitialPerHourPrice()))
					.add(s.getMaxBlendedNumberOfHours().multiply(s.getAdditionalPerHourPrice()));
		} else if (strategy instanceof InternalPricingStrategy) {
			return BigDecimal.ZERO;// cost is ZERO
		} else {
			Assert.isTrue(false, "Unable to handle pricing strategy " + strategy);
		}

		return cost;
	}

	@Override
	public BigDecimal calculateMaximumResourceCost(Work work) {
		Assert.notNull(work);
		Assert.notNull(work.getPricingStrategy());
		return calculateMaximumResourceCost(work.getPricingStrategy()).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal calculateMaximumResourceCostPlusFee(PricingStrategy pricingStrategy, Work work){

		BigDecimal maximumResourceCost = calculateMaximumResourceCost(pricingStrategy);
		BigDecimal buyerNetMoneyFee = calculateBuyerNetMoneyFee(work, maximumResourceCost);
		return buyerNetMoneyFee.add(maximumResourceCost);
	}

	@Override
	public BigDecimal calculateOriginalMaximumResourceCost(Long workId) {
		Assert.notNull(workId);
		AbstractWork work = baseWorkDAO.findById(workId);
		Assert.notNull(work);
		return calculateOriginalMaximumResourceCost(work);
	}

	/**
	 * Returns the first maximum resource price
	 *
	 * @param work
	 * @return
	 */
	private BigDecimal calculateOriginalMaximumResourceCost(AbstractWork work) {
		Assert.notNull(work);

		List<WorkPrice> priceHistory = workPriceDAO.findPriceHistoryForWork(work.getId());
		if (!priceHistory.isEmpty()) {
			//The approved negotiations come in reverse chronological order, so use the last one
			return calculateMaximumResourceCost(priceHistory.get(priceHistory.size() - 1).getPricingStrategy());
		}

		Assert.notNull(work.getPricingStrategy());
		return calculateMaximumResourceCost(work.getPricingStrategy());
	}

	@Override
	public Map<String, BigDecimal> calculateMaximumResourceCostForAllStrategies(PricingStrategy pricing) {
		return calculateMaximumResourceCostForAllStrategies(pricing.getFullPricingStrategy());
	}

	@Override
	public Map<String, BigDecimal> calculateMaximumResourceCostForAllStrategies(FullPricingStrategy p) {
		BigDecimal flatPrice = NumberUtilities.defaultValue(p.getFlatPrice());

		BigDecimal perHourPrice = NumberUtilities.defaultValue(p.getPerHourPrice());
		BigDecimal maxHours = NumberUtilities.defaultValue(p.getMaxNumberOfHours());
		BigDecimal perHourTotal = perHourPrice.multiply(maxHours);

		BigDecimal perUnitPrice = NumberUtilities.defaultValue(p.getPerUnitPrice());
		BigDecimal maxUnits = NumberUtilities.defaultValue(p.getMaxNumberOfUnits());
		BigDecimal perUnitTotal = perUnitPrice.multiply(maxUnits);

		BigDecimal initialPerHourPrice = NumberUtilities.defaultValue(p.getInitialPerHourPrice());
		BigDecimal initialMaxHours = NumberUtilities.defaultValue(p.getInitialNumberOfHours());
		BigDecimal additionalPricePerHour = NumberUtilities.defaultValue(p.getAdditionalPerHourPrice());
		BigDecimal additionalMaxHours = NumberUtilities.defaultValue(p.getMaxBlendedNumberOfHours());
		BigDecimal blendedPerHourTotal = BigDecimal.ZERO
				.add(additionalPricePerHour.multiply(additionalMaxHours))
				.add(initialPerHourPrice.multiply(initialMaxHours));

		return CollectionUtilities.newTypedObjectMap(
				PricingStrategyType.FLAT.name(), flatPrice,
				PricingStrategyType.PER_HOUR.name(), perHourTotal,
				PricingStrategyType.PER_UNIT.name(), perUnitTotal,
				PricingStrategyType.BLENDED_PER_HOUR.name(), blendedPerHourTotal
		);
	}

	@Override
	public Map<String, BigDecimal> getMaxSpendOfAssignment(com.workmarket.thrift.work.Work work) {
		Assert.notNull(work);
		return calculateMaximumResourceCostForAllStrategies(PricingStrategyUtilities.copyThrift(work.getPricing()));
	}

	@Override
	public BigDecimal calculateTotalResourceCost(Work work, WorkResource workResource) throws InvalidPricingException {
		Assert.notNull(work);
		Assert.notNull(work.getPricingStrategy());
		Assert.notNull(workResource);
		return calculateTotalResourceCost(
				work,
				workResource.getHoursWorked(),
				workResource.getUnitsProcessed(),
				work.getPricingStrategy().getFullPricingStrategy().getOverridePrice(),
				workResource.getAdditionalExpenses(),
				workResource.getBonus()
		);
	}

	@Override
	public BigDecimal calculateTotalResourceCost(Work work, CompleteWorkDTO dto) throws InvalidPricingException {
		Assert.notNull(work);
		Assert.notNull(dto);

		BigDecimal hoursWorked = (dto.getHoursWorked() != null) ? BigDecimal.valueOf(dto.getHoursWorked()) : null;
		BigDecimal unitsProcessed = (dto.getUnitsProcessed() != null) ? BigDecimal.valueOf(dto.getUnitsProcessed()) : null;
		BigDecimal overridePrice = (dto.getOverridePrice() != null) ? BigDecimal.valueOf(dto.getOverridePrice()) : null;
		BigDecimal additionalExpenses = (dto.getAdditionalExpenses() != null) ? BigDecimal.valueOf(dto.getAdditionalExpenses()) : null;
		BigDecimal bonus = (dto.getBonus() != null) ? BigDecimal.valueOf(dto.getBonus()) : null;

		return calculateTotalResourceCost(work, hoursWorked, unitsProcessed, overridePrice, additionalExpenses, bonus);
	}

	@Override
	public BigDecimal calculateWorkPrice(Work work) throws InvalidPricingException {
		Assert.notNull(work);
		WorkStatusType workStatusType = work.getWorkStatusType();
		if(WorkStatusType.CANCELLED_PAYMENT_PENDING.equals(workStatusType.getCode())
				|| WorkStatusType.CANCELLED_WITH_PAY.equals(workStatusType.getCode())) {
			WorkResource workResource = workService.findActiveWorkResource(work.getId());
			return calculateTotalResourceCost(work, workResource);
		}	else if(WorkStatusType.PAID.equals(workStatusType.getCode())) {
			return work.getFulfillmentStrategy().getWorkPrice();
		} else {
			return calculateMaximumResourceCost(work);
		}
	}

	@Override
	public PricingStrategy[] findAllPricingStrategies() {
		return Lists.newArrayList(
				new FlatPricePricingStrategy(),
				new PerHourPricingStrategy(),
				new PerUnitPricingStrategy(),
				new BlendedPerHourPricingStrategy(),
				new InternalPricingStrategy())
				.toArray(new PricingStrategy[5]);
	}

	@Override
	public PricingStrategy findPricingStrategyById(Long pricingStrategyId) {
		Assert.notNull(pricingStrategyId);

		for (PricingStrategy strategy : findAllPricingStrategies()) {
			if (pricingStrategyId.equals(strategy.getId())) {
				return strategy;
			}
		}

		return null;
	}

	@Override
	public BigDecimal calculateWorkCost(Long workId) {
		Assert.notNull(workId);

		Work work = workDAO.findWorkById(workId);

		Assert.notNull(work);
		Assert.notNull(work.getPricingStrategy());

		WorkResource resource = workService.findActiveWorkResource(work.getId());

		Assert.notNull(resource);

		if (work.getPricingStrategy() instanceof PerHourPricingStrategy || work.getPricingStrategy() instanceof BlendedPerHourPricingStrategy) {
			Assert.notNull(resource.getHoursWorked());
			Assert.state(BigDecimal.valueOf(0).compareTo(resource.getHoursWorked()) < 1);
		} else if (work.getPricingStrategy() instanceof PerUnitPricingStrategy) {
			Assert.notNull(resource.getUnitsProcessed());
			Assert.state(BigDecimal.valueOf(0).compareTo(resource.getUnitsProcessed()) < 1);
		}

		return calculateTotalResourceCost(work, resource);
	}

	@Override
	public BigDecimal calculateBuyerNetMoneyFee(AbstractWork work, BigDecimal amount) {
		Assert.notNull(work);

		if (work.hasLegacyWorkFeeConfiguration()) {
			return calculateBuyerNetMoneyFee(work.getCompany(), work.getWorkFeeConfiguration(), amount);
		}
		if (work.hasSubscriptionPricing()) {
			return BigDecimal.ZERO;
		}
		return calculateBuyerNetMoneyFee(work.getCompany(), amount);
	}

	@Override
	public PricingStrategy adjustPricingByCompanyFeePercentage(PricingStrategy pricing, Long companyId) {
		AccountRegister register = findDefaultRegisterForCompany(companyId, false);
		BigDecimal feePercentage = register.getCurrentWorkFeePercentage();

		return adjustPricingByFeePercentage(pricing, feePercentage);
	}

	@Override
	public PricingStrategy adjustPricingByCompanyFeePercentage(PricingStrategy pricing, Long companyId, Long workId) {
		if (workId == null) {
			return adjustPricingByCompanyFeePercentage(pricing, companyId);
		}
		BigDecimal feePercentage = getCurrentFeePercentageForWork(workId);
		return adjustPricingByFeePercentage(pricing, feePercentage);
	}

	@Override
	public BigDecimal getCurrentFeePercentageForWork(AbstractWork work) {
		Assert.notNull(work);

		BigDecimal currentFeePercentage = accountRegisterDAO.getCurrentWorkFeePercentage(work.getCompany().getId());
		if (work.hasLegacyWorkFeeConfiguration()) {
			WorkFeeBand workFeeBand = determineWorkFeeBand(work.getCompany());
			currentFeePercentage = workFeeBand.getPercentage();
		}
		if (work.hasSubscriptionPricing()) {
			return BigDecimal.ZERO;
		}
		return currentFeePercentage;
	}

	@Override
	public BigDecimal getCurrentFeePercentageForCompany(Long companyId) {
		Assert.notNull(companyId);
		return accountRegisterDAO.getCurrentWorkFeePercentage(companyId);
	}

	@Override
	public BigDecimal getCurrentFeePercentageForWork(Long workId) {
		Assert.notNull(workId);
		AbstractWork work = baseWorkDAO.get(workId);
		if (work != null) {
			return getCurrentFeePercentageForWork(work);
		}
		return null;
	}

	/**
	 * This will come in handy when users have a different register based on their currency
	 * <p/>
	 * TODO: at some point, currency will need to be taken to account
	 */
	@Override
	public RegisterTransactionCost findCostForTransactionType(String registerTransactionTypeCode, AccountRegister accountRegister) {

		logger.debug("Looking for transaction cost..." + registerTransactionTypeCode);
		logger.debug("Account Register Id: " + accountRegister.getId());

		for (RegisterTransactionCost type : accountRegister.getRegisterTransactionCost()) {
			if (type.getRegisterTransactionType() == null) {
				type = registerTransactionCostDAO.get(type.getId());
				logger.debug("Checking " + type.getRegisterTransactionType().getCode());
			}
			if (type.getRegisterTransactionType().getCode().equals(registerTransactionTypeCode)) {
				return type;
			}
		}
		logger.debug(String.format("Cost not found for code %s ", registerTransactionTypeCode));
		return null;
	}

	@Override
	public Map<String, RegisterTransactionCostDTO> findCostForTransactionTypesByCompany(
			Long companyId,
			Collection<String> registerTransactionTypeCodes) {

		Map<String, RegisterTransactionCostDTO> result = Maps.newHashMap();

		if (isEmpty(registerTransactionTypeCodes))
			return result;

		AccountRegister accountRegister = findDefaultRegisterForCompany(companyId, false);
		logger.debug(String.format("Looking for transaction costs for codes %s, account register %s",
				registerTransactionTypeCodes, accountRegister));

		for (String code : registerTransactionTypeCodes) {
			for (RegisterTransactionCost type : accountRegister.getRegisterTransactionCost()) {
				if (type.getRegisterTransactionType() == null) {
					type = registerTransactionCostDAO.get(type.getId());
				}
				if (type != null && type.getRegisterTransactionType().getCode().equals(code))
					result.put(code, RegisterTransactionCostDTO.newDTO(type));
			}
		}
		return result;
	}

	@Override
	public WorkFeeConfiguration findActiveWorkFeeConfiguration(Long companyId) {
		return workFeeConfigurationDAO.findWithWorkFeeBands(companyId);
	}

	@Override
	public void saveAndActivateWorkFeeConfiguration(Long companyId, WorkFeeConfigurationDTO workFeeConfigurationDTO) {
		Assert.notNull(companyId);
		Company company = companyDAO.get(companyId);
		Assert.notNull(company);
		Assert.isTrue(!company.getPaymentConfiguration().isSubscriptionPricing(), "Can't override work fee bands under Subscription");

		Calendar now = GregorianCalendar.getInstance();
		WorkFeeConfiguration workFeeConfig = workFeeConfigurationDAO.findWithWorkFeeBands(companyId);
		workFeeConfig.setActive(false);
		workFeeConfig.setReplacedDate(now);

		WorkFeeConfiguration newConfig = new WorkFeeConfiguration();
		newConfig.setAccountRegister(workFeeConfig.getAccountRegister());
		newConfig.setActive(true);
		newConfig.setActiveDate(now);

		List<WorkFeeBand> workFeeBands = new ArrayList<>();
		newConfig.setWorkFeeBands(workFeeBands);

		for (WorkFeeBandDTO band : workFeeConfigurationDTO.getWorkFeeBandDTOs()) {
			WorkFeeBand workFeeBand = new WorkFeeBand();
			workFeeBand.setMinimum(new BigDecimal(band.getMinimum()));
			workFeeBand.setMaximum(new BigDecimal(band.getMaximum()));
			workFeeBand.setPercentage(new BigDecimal(band.getPercentage()));
			workFeeBand.setWorkFeeConfiguration(newConfig);
			workFeeBands.add(workFeeBand);
		}

		workFeeConfigurationDAO.saveOrUpdate(newConfig);

		accountRegisterServicePrefundImpl.updateAccountRegisterWorkFeeData(workFeeConfig.getAccountRegister().getId());

	}

	@Override
	public BigDecimal findDrugTestPrice(Long companyId) {
		AccountRegister register = findDefaultRegisterForCompany(companyId, false);
		RegisterTransactionCost cost = findCostForTransactionType(RegisterTransactionType.DRUG_TEST, register);

		return cost.getFixedAmount();
	}

	@Override
	public BigDecimal findBackgroundCheckPrice(Long companyId) {
		return findBackgroundCheckPrice(companyId, Country.USA);
	}

	@Override
	public BigDecimal findBackgroundCheckPrice(Long companyId, String countryCode) {
		Assert.notNull(companyId);
		Assert.hasText(countryCode);
		String transactionType = registerTransactionFactory.newBackgroundCheckRegisterTransactionType(countryCode).getCode();
		Assert.hasText(transactionType);

		AccountRegister register = findDefaultRegisterForCompany(companyId, false);
		RegisterTransactionCost cost = findCostForTransactionType(transactionType, register);

		return cost.getFixedAmount();
	}

	public void lockAccountRegisterForWritingHack(Long companyId) {
		// DISCLAIMER: This is a dirty dirty hack
		// THIS WAS ONLY CREATED TO MAKE DELETING THIS TRASH, IN THE FUTURE @_@, MUCH EASIER

		// This method is only called for the side effect of locking the Account Register entry for eventual writing
		// The Account Register is never used
		findDefaultRegisterForCompany(companyId, true);
	}

	public AccountRegister findDefaultRegisterForCompany(Long companyId) {
		return findDefaultRegisterForCompany(companyId, false);
	}

	public AccountRegister findDefaultRegisterForCompany(Long companyId, boolean lockForWriting) {
		AccountRegister accountRegister = accountRegisterDAO.findByCompanyId(companyId, lockForWriting);

		if (accountRegister != null) {
			return accountRegister;
		}

		return createNewAccountRegisterForCompany(companyId);
	}

	/**
	 * This will come in handy when users have a different register based on their currency
	 */
	private AccountRegister createNewAccountRegisterForCompany(Long companyId) {
		Assert.notNull(companyId);

		logger.debug("Default U.S. Dollar register not found, so creating new...");
		Company company = companyDAO.get(companyId);
		Assert.notNull(company);
		AccountRegister accountRegister = new AccountRegister();

		Set<RegisterTransactionCost> costs = new HashSet<>();
		for (RegisterTransactionCost cost : accountRegister.getRegisterTransactionCost()) {
			costs.add(registerTransactionCostDAO.get(cost.getId()));
		}
		accountRegister.setRegisterTransactionCost(costs);

		logger.debug("Setting company for register to " + company.getId());

		accountRegister.setCompany(company);
		accountRegister.setCredit(new BigDecimal(0));

		AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
		accountRegister.setAccountRegisterSummaryFields(accountRegisterSummaryFields);

		// make sure the parent company is set up to backref the account register
		Set<AccountRegister> accountRegisters = new HashSet<>();
		accountRegisters.add(accountRegister);
		company.setAccountRegisters(accountRegisters);

		logger.debug("Attempting to save register via company...");

		accountRegisterDAO.saveOrUpdate(accountRegister);

		logger.debug("Save called from CompanyDAO");
		return accountRegister;
	}

	public AccountRegister updateAPLimit(Long companyId, String amount) throws OverAPLimitException {

		BigDecimal newApLimit = new BigDecimal(amount);

		if (Constants.MAX_AP_BALANCE_LIMIT.compareTo(newApLimit) < 0) {
			throw new OverAPLimitException();
		}

		Company company = companyDAO.findCompanyById(companyId);
		AccountRegister register = findDefaultRegisterForCompany(company.getId(), true);
		register.setApLimit(newApLimit);

		return register;
	}

	public BigDecimal calculateRemainingAPBalance(Long companyId) {
		Assert.notNull(companyId);
		return accountRegisterDAO.calcRemainingAPBalance(companyId);
	}

	@Override
	public WorkFeeBand determineWorkFeeBand(AccountRegister accountRegister) {
		Assert.notNull(accountRegister);

		WorkFeeConfiguration workFeeConfiguration = findActiveWorkFeeConfiguration(accountRegister.getCompany().getId());
		return determineWorkFeeBand(accountRegister, workFeeConfiguration);
	}

	@Override
	public WorkFeeBand determineWorkFeeBand(Company company) {
		Assert.notNull(company);

		WorkFeeConfiguration workFeeConfiguration = findActiveWorkFeeConfiguration(company.getId());
		return determineWorkFeeBand(company, accountRegisterServicePrefundImpl.getPaymentSummation(company.getId()), workFeeConfiguration);
	}

	private WorkFeeBand determineWorkFeeBand(AccountRegister accountRegister, WorkFeeConfiguration workFeeConfiguration) {
		Assert.notNull(accountRegister);

		return determineWorkFeeBand(accountRegister.getCompany(), accountRegister.getPaymentSummation(), workFeeConfiguration);
	}

	private WorkFeeBand determineWorkFeeBand(Company company, BigDecimal paymentSummation, WorkFeeConfiguration workFeeConfiguration) {
		Assert.notNull(company);

		if (company.getAccountPricingType().isTransactionalPricing()) {
			if (workFeeConfiguration == null) {
				WorkFeeBand workFeeBand = new WorkFeeBand();
				workFeeBand.setPercentage(Constants.MAX_WORK_FEE_PERCENTAGE);
				workFeeBand.setLevel(1);
				logger.error("Had to create workFeeBand for companyId:" + company.getId());

				return workFeeBand;
			}

			int level = 1;
			for (WorkFeeBand workFeeBand : workFeeConfiguration.getWorkFeeBands()) {
				if (paymentSummation.compareTo(workFeeBand.getMaximum()) == -1) {
					if (workFeeBand.getPercentage().compareTo(LOWEST_WORK_FEE_PERCENTAGE) == -1) {
						//Meant to eliminate negative workFeeBand percentages etc, hackers...
						logger.error("Had to set WorkFeeBand.percentage to " + LOWEST_WORK_FEE_PERCENTAGE + " it was:"
							+ workFeeBand.getPercentage() + " for companyId:" + company.getId());
						workFeeBand.setPercentage(LOWEST_WORK_FEE_PERCENTAGE);
						workFeeBand.setLevel(level);
					}

					return workFeeBand;
				}
				level++;
			}

			throw new IllegalArgumentException("There isn't a WorkFeeBand for accountRegister.accountRegisterSummaryFields.paymentSummation:" + paymentSummation);
		}
		//TODO: Maybe return a Fee band with 0
		return null;
	}

	@Override
	public BigDecimal calculateBuyerNetMoneyFee(Company company, BigDecimal amount) {
		BigDecimal fee = BigDecimal.ZERO;
		if (company.getAccountPricingType().isTransactionalPricing()) {
			WorkFeeBand workFeeBand = determineWorkFeeBand(company);
			fee = calculateFeeFromWorkFeeBand(workFeeBand, amount);
		}
		return fee;
	}

	/**
	 * Calculates the Net Fee based on an specific configuration
	 */
	private BigDecimal calculateBuyerNetMoneyFee(Company company, WorkFeeConfiguration workFeeConfiguration, BigDecimal amount) {
		WorkFeeBand workFeeBand = determineWorkFeeBand(company, accountRegisterServicePrefundImpl.getPaymentSummation(company.getId()), workFeeConfiguration);
		return calculateFeeFromWorkFeeBand(workFeeBand, amount);
	}

	/**
	 * Calculates the Net Fee based on a fee band
	 */
	private BigDecimal calculateFeeFromWorkFeeBand(WorkFeeBand workFeeBand, BigDecimal amount) {
		Assert.notNull(workFeeBand);
		Assert.notNull(workFeeBand.getPercentage());

		Assert.notNull(amount);
		BigDecimal fee = amount.multiply(workFeeBand.getPercentage().divide(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP);

		if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {
			fee = Constants.MAX_WORK_FEE;
		}
		return fee;
	}

	@Override
	public PricingStrategy copyPricingStrategy(PricingStrategy pricing) {
		PricingStrategy newPricing = findPricingStrategyById(pricing.getId());
		BeanUtilities.copyProperties(newPricing.getFullPricingStrategy(), pricing.getFullPricingStrategy());

		return newPricing;
	}

	private PricingStrategy adjustPricingByFeePercentage(PricingStrategy pricing, BigDecimal feePercentage) {
		PricingStrategy newPricing = copyPricingStrategy(pricing);

		BigDecimal fee;
		BigDecimal percentage = feePercentage.movePointLeft(2);
		BigDecimal adjust = feePercentage.movePointLeft(2).add(BigDecimal.ONE);
		FullPricingStrategy fullPricingStrategy = pricing.getFullPricingStrategy();

		// Flat price
		if (fullPricingStrategy.getFlatPrice() != null) {
			BigDecimal flatPrice = fullPricingStrategy.getFlatPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP);

			fee = flatPrice.multiply(percentage);
			if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {    // Is fee > MAX_WORK_FEE ?
				// newPricing = flatPrice - MAX_WORK_FEE
				newPricing.getFullPricingStrategy().setFlatPrice(flatPrice.subtract(Constants.MAX_WORK_FEE));
			} else {
				// newPricing = flatPrice / adjust
				newPricing.getFullPricingStrategy().setFlatPrice(flatPrice.divide(adjust, 8, RoundingMode.HALF_UP));
			}
		}

		// Max flat price
		if (fullPricingStrategy.getMaxFlatPrice() != null) {
			BigDecimal maxFlatPrice = fullPricingStrategy.getMaxFlatPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP);

			fee = maxFlatPrice.multiply(percentage);
			if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {    // Is fee > MAX_WORK_FEE ?
				// newPricing = maxFlatPrice - MAX_WORK_FEE
				newPricing.getFullPricingStrategy().setMaxFlatPrice(maxFlatPrice.subtract(Constants.MAX_WORK_FEE));
			} else {
				// newPricing = maxFlatPrice / adjust
				newPricing.getFullPricingStrategy().setMaxFlatPrice(maxFlatPrice.divide(adjust, 8, RoundingMode.HALF_UP));
			}
		}

		// Per hour price
		if (fullPricingStrategy.getPerHourPrice() != null) {
			BigDecimal perHourPrice = fullPricingStrategy.getPerHourPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP);
			BigDecimal maxNumberOfHours = fullPricingStrategy.getMaxNumberOfHours();

			if (maxNumberOfHours != null) {
				// fee = (perHourPrice * maxNumberOfHours) * percentage
				fee = perHourPrice.multiply(maxNumberOfHours).multiply(percentage);

				if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {    // Is fee > MAX_WORK_FEE ?
					// newPerHourPrice = perHourPrice - (MAX_FEE / maxNumberOfHours)
					BigDecimal newPerHourPrice = perHourPrice.subtract(Constants.MAX_WORK_FEE.divide(maxNumberOfHours, 8, RoundingMode.HALF_UP));
					newPricing.getFullPricingStrategy().setPerHourPrice(newPerHourPrice);
				} else {
					// newPricing = perHourPrice / adjust
					newPricing.getFullPricingStrategy().setPerHourPrice(perHourPrice.divide(adjust, 8, RoundingMode.HALF_UP));
				}
			} else {
				// newPricing = perHourPrice / adjust
				newPricing.getFullPricingStrategy().setPerHourPrice(perHourPrice.divide(adjust, 8, RoundingMode.HALF_UP));
			}
		}

		// Per unit price
		if (fullPricingStrategy.getPerUnitPrice() != null) {
			BigDecimal perUnitPrice = fullPricingStrategy.getPerUnitPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP);
			BigDecimal maxNumberOfUnits = fullPricingStrategy.getMaxNumberOfUnits();

			if (maxNumberOfUnits != null) {
				//fee = (perUnitPrice * maxNumberOfUnits) * percentage
				fee = perUnitPrice.multiply(maxNumberOfUnits).multiply(percentage);

				if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {    // Is fee > MAX_WORK_FEE ?
					// newPerUnitPrice = perUnitPrice - (MAX_FEE / maxNumberOfUnits)
					BigDecimal newPerUnitPrice = perUnitPrice.subtract(Constants.MAX_WORK_FEE.divide(maxNumberOfUnits, 8, RoundingMode.HALF_UP));
					newPricing.getFullPricingStrategy().setPerUnitPrice(newPerUnitPrice);
				} else {
					// newPricing = perUnitPrice / adjust
					newPricing.getFullPricingStrategy().setPerUnitPrice(perUnitPrice.divide(adjust, 8, RoundingMode.HALF_UP));
				}
			} else {
				// newPricing = perUnitPrice / adjust
				newPricing.getFullPricingStrategy().setPerUnitPrice(perUnitPrice.divide(adjust, 8, RoundingMode.HALF_UP));
			}
		}

		// Blended per hour price
		if (fullPricingStrategy.getInitialPerHourPrice() != null) {
			BigDecimal initialPerHourPrice = fullPricingStrategy.getInitialPerHourPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP),
					initialNumberOfHours = fullPricingStrategy.getInitialNumberOfHours(),

					additionalPerHourPrice = pricing.getFullPricingStrategy().getAdditionalPerHourPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP),
					maxBlendedNumberOfHours = fullPricingStrategy.getMaxBlendedNumberOfHours();

			if (initialNumberOfHours != null) {
				if (additionalPerHourPrice != null && maxBlendedNumberOfHours != null) {
					// fee = ((initialPerHourPrice * initialNumberOfHours) + (additionalPerHourPrice * maxBlendedNumberOfHours)) * percentage
					fee = initialPerHourPrice.multiply(initialNumberOfHours)
							.add(additionalPerHourPrice.multiply(maxBlendedNumberOfHours))
							.multiply(percentage);
				} else {
					// fee = (initialPerHourPrice * initialNumberOfHours) * percentage
					fee = initialPerHourPrice.multiply(initialNumberOfHours).multiply(percentage);
				}

				if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {    // Is fee > MAX_WORK_FEE ?
					if (additionalPerHourPrice != null && maxBlendedNumberOfHours != null) {
						// totalPrice = (initialPerHourPrice * initialNumberOfHours) + (additionalPerHourPrice * maxBlendedNumberOfHours)
						BigDecimal totalPrice = initialPerHourPrice.multiply(initialNumberOfHours)
								.add(additionalPerHourPrice.multiply(maxBlendedNumberOfHours));

						// coefficient for scaling price = (1 - (MAX_FEE / totalPrice))
						BigDecimal coefficient = BigDecimal.ONE.subtract(Constants.MAX_WORK_FEE.divide(totalPrice, 8, RoundingMode.HALF_UP));

						// Scale initialPerHourPrice
						newPricing.getFullPricingStrategy().setInitialPerHourPrice(initialPerHourPrice.multiply(coefficient));

						// Scale additionalPerHourPrice
						newPricing.getFullPricingStrategy().setAdditionalPerHourPrice(additionalPerHourPrice.multiply(coefficient));
					} else {
						// newInitialPerHourPrice = (initialPerHourPrice * initialNumberOfHours) - (MAX_WORK_FEE / initialNumberOfHours)
						BigDecimal newInitialPerHourPrice = fullPricingStrategy.getInitialPerHourPrice().multiply(initialNumberOfHours)
								.subtract(Constants.MAX_WORK_FEE)
								.divide(initialNumberOfHours, 8, RoundingMode.HALF_UP);

						newPricing.getFullPricingStrategy().setInitialPerHourPrice(newInitialPerHourPrice);
					}
				} else { // If MAX_FEE was not exceeded
					// newPricing = perHourPrice / adjust
					newPricing.getFullPricingStrategy().setInitialPerHourPrice(initialPerHourPrice.divide(adjust, 8, RoundingMode.HALF_UP));

					if (additionalPerHourPrice != null && maxBlendedNumberOfHours != null) {
						// newPricing = perHourPrice / adjust
						newPricing.getFullPricingStrategy().setAdditionalPerHourPrice(additionalPerHourPrice.divide(adjust, 8, RoundingMode.HALF_UP));
					}
				}
			}
		} else if (fullPricingStrategy.getAdditionalPerHourPrice() != null) { // Only additional per hour price
			BigDecimal additionalPerHourPrice = fullPricingStrategy.getAdditionalPerHourPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP);
			BigDecimal maxBlendedNumberOfHours = fullPricingStrategy.getMaxBlendedNumberOfHours();

			if (maxBlendedNumberOfHours != null) {
				// fee = (additionalPerHourPrice * maxBlendedNumberOfHours) * percentage
				fee = additionalPerHourPrice.multiply(maxBlendedNumberOfHours).multiply(percentage);

				if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {    // Is fee > MAX_WORK_FEE ?
					// newAdditionalPerHourPrice = additionalPerHourPrice - (MAX_FEE / maxBlendedNumberOfHours)
					BigDecimal newAdditionalPerHourPrice = additionalPerHourPrice.subtract(Constants.MAX_WORK_FEE.divide(maxBlendedNumberOfHours, 8, RoundingMode.HALF_UP));
					newPricing.getFullPricingStrategy().setAdditionalPerHourPrice(newAdditionalPerHourPrice);
				} else {
					// newPricing = additionalPerHourPrice / adjust
					newPricing.getFullPricingStrategy().setAdditionalPerHourPrice(additionalPerHourPrice.divide(adjust, 8, RoundingMode.HALF_UP));
				}
			} else {
				// newPricing = additionalPerHourPrice / adjust
				newPricing.getFullPricingStrategy().setAdditionalPerHourPrice(additionalPerHourPrice.divide(adjust, 8, RoundingMode.HALF_UP));
			}
		}

		// Blended per unit price
		if (fullPricingStrategy.getInitialPerUnitPrice() != null) {
			BigDecimal initialPerUnitPrice = fullPricingStrategy.getInitialPerUnitPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP),
					initialNumberOfUnits = fullPricingStrategy.getInitialNumberOfUnits(),

					additionalPerUnitPrice = pricing.getFullPricingStrategy().getAdditionalPerUnitPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP),
					maxBlendedNumberOfUnits = fullPricingStrategy.getMaxBlendedNumberOfUnits();

			if (initialNumberOfUnits != null) {
				if (additionalPerUnitPrice != null && maxBlendedNumberOfUnits != null) {
					// fee = ((initialPerUnitPrice * initialNumberOfUnits) + (additionalPerUnitPrice * maxBlendedNumberOfUnits)) * percentage
					fee = initialPerUnitPrice.multiply(initialNumberOfUnits)
							.add(additionalPerUnitPrice.multiply(maxBlendedNumberOfUnits))
							.multiply(percentage);
				} else {
					// fee = (initialPerUnitPrice * initialNumberOfUnits) * percentage
					fee = initialPerUnitPrice.multiply(initialNumberOfUnits).multiply(percentage);
				}

				if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {    // Is fee > MAX_WORK_FEE ?
					if (additionalPerUnitPrice != null && maxBlendedNumberOfUnits != null) {
						// totalPrice = (initialPerUnitPrice * initialNumberOfUnits) + (additionalPerUnitPrice * maxBlendedNumberOfUnits)
						BigDecimal totalPrice = initialPerUnitPrice.multiply(initialNumberOfUnits)
								.add(additionalPerUnitPrice.multiply(maxBlendedNumberOfUnits));

						// coefficient for scaling price = (1 - (MAX_FEE / totalPrice))
						BigDecimal coefficient = BigDecimal.ONE.subtract(Constants.MAX_WORK_FEE.divide(totalPrice, 8, RoundingMode.HALF_UP));

						// Scale initialPerUnitPrice
						newPricing.getFullPricingStrategy().setInitialPerUnitPrice(initialPerUnitPrice.multiply(coefficient));

						// Scale additionalPerUnitPrice
						newPricing.getFullPricingStrategy().setAdditionalPerUnitPrice(additionalPerUnitPrice.multiply(coefficient));
					} else {
						// newInitialPrice = (initialPerUnitPrice * initialNumberOfUnits - MAX_WORK_FEE) / initialNumberOfUnits
						BigDecimal newInitialPrice = fullPricingStrategy.getInitialPerUnitPrice().multiply(initialNumberOfUnits)
								.subtract(Constants.MAX_WORK_FEE)
								.divide(initialNumberOfUnits, 8, RoundingMode.HALF_UP);

						newPricing.getFullPricingStrategy().setInitialPerUnitPrice(newInitialPrice);
					}
				} else {
					// newPricing = initialPerUnitPrice / adjust
					newPricing.getFullPricingStrategy().setInitialPerUnitPrice(initialPerUnitPrice.divide(adjust, 8, RoundingMode.HALF_UP));

					if (additionalPerUnitPrice != null && maxBlendedNumberOfUnits != null) {
						// newPricing = additionalPerHourPrice / adjust
						newPricing.getFullPricingStrategy().setAdditionalPerUnitPrice(additionalPerUnitPrice.divide(adjust, 8, RoundingMode.HALF_UP));
					}
				}
			}
		} else if (fullPricingStrategy.getAdditionalPerUnitPrice() != null) {
			BigDecimal additionalPerUnitPrice = fullPricingStrategy.getAdditionalPerUnitPrice().setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP);
			BigDecimal maxBlendedNumberOfUnits = fullPricingStrategy.getMaxBlendedNumberOfUnits();

			if (maxBlendedNumberOfUnits != null) {
				// fee = (additionalPerUnitPrice * maxBlendedNumberOfUnits) * percentage
				fee = additionalPerUnitPrice.multiply(maxBlendedNumberOfUnits).multiply(percentage);

				if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {    // Is fee > MAX_WORK_FEE ?
					// newAdditionalPerUnitPrice = additionalPerUnitPrice - (MAX_FEE / maxBlendedNumberOfUnits)
					BigDecimal newAdditionalPerUnitPrice = additionalPerUnitPrice.subtract(Constants.MAX_WORK_FEE.divide(maxBlendedNumberOfUnits, 8, RoundingMode.HALF_UP));
					newPricing.getFullPricingStrategy().setAdditionalPerUnitPrice(newAdditionalPerUnitPrice);
				} else {
					// newPricing = additionalPerUnitPrice / adjust
					newPricing.getFullPricingStrategy().setAdditionalPerUnitPrice(additionalPerUnitPrice.divide(adjust, 8, RoundingMode.HALF_UP));
				}
			} else {
				// newPricing = additionalPerUnitPrice / adjust
				newPricing.getFullPricingStrategy().setAdditionalPerUnitPrice(additionalPerUnitPrice.divide(adjust, 8, RoundingMode.HALF_UP));
			}
		}

		return newPricing;
	}

	public void setWorkNegotiationService(WorkNegotiationService workNegotiationService) {
		this.workNegotiationService = workNegotiationService;
	}
}
