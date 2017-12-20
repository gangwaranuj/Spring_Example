package com.workmarket.service.business;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.RegisterTransactionCost;
import com.workmarket.domains.model.account.WorkFeeBand;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.RegisterTransactionCostDTO;
import com.workmarket.service.business.dto.WorkFeeConfigurationDTO;
import com.workmarket.service.exception.account.InvalidPricingException;
import com.workmarket.service.exception.account.OverAPLimitException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public interface PricingService {

	/**
	 * Calculate the maximum possible cost of a pricing strategy.
	 * @param strategy
	 * @return
	 */
	BigDecimal calculateMaximumResourceCost(PricingStrategy strategy);

	/**
	 * Calculate the maximum possible cost of an assignment.
	 * @param work
	 * @return
	 */
	BigDecimal calculateMaximumResourceCost(Work work);

	BigDecimal calculateMaximumResourceCostPlusFee(PricingStrategy pricingStrategy, Work work);

	/**
	 * Return the initial maximum resource cost that existed for an assignment.
	 * @param workId
	 * @return
	 */
	BigDecimal calculateOriginalMaximumResourceCost(Long workId);

	Map<String, BigDecimal> calculateMaximumResourceCostForAllStrategies(PricingStrategy pricing);
	Map<String, BigDecimal> calculateMaximumResourceCostForAllStrategies(FullPricingStrategy pricing);

	Map<String, BigDecimal> getMaxSpendOfAssignment(com.workmarket.thrift.work.Work work);

	BigDecimal calculateTotalResourceCostWithoutOverride(AbstractWork work, WorkResource workResource);
	/**
	 * Calculate the actual cost of an assignment respective to the resource and the work they've logged.
	 * @param work
	 * @param workResource
	 * @return
	 * @throws InvalidPricingException
	 */
	BigDecimal calculateTotalResourceCost(Work work, WorkResource workResource) throws InvalidPricingException;

	/**
	 * Calculate the actual cost of an assignment respective to the CompleteWorkDTO, which captures the
	 * amount of work done by a resource at the time of completing an assignment.
	 * @param work
	 * @param dto
	 * @return
	 * @throws InvalidPricingException
	 */
	BigDecimal calculateTotalResourceCost(Work work, CompleteWorkDTO dto) throws InvalidPricingException;

	/**
	 * Calculate the work price from sent to paid status
	 * @param work
	 * @return
	 * @throws InvalidPricingException
	 */

	BigDecimal calculateWorkPrice(Work work) throws InvalidPricingException;

	/**
	 * Get a list of all available pricing strategy types.
	 * @return
	 * @throws Exception
	 */
	PricingStrategy[] findAllPricingStrategies();

	/**
	 * Get a specific pricing strategy by its ID.
	 * @param pricingStrategyId
	 * @return
	 * @throws Exception
	 */
	PricingStrategy findPricingStrategyById(Long pricingStrategyId);

	/**
	 * Calculate the actual cost of an assignment respective to the resource and the work they've logged.
	 * Assumes that a resource is assigned and active on the assignment.
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	BigDecimal calculateWorkCost(Long workId);

	/**
	 * Calculates the buyer fee for a particular amount based on:
	 * 1) if there's a legacy work fee configuration associated with the work. This is the case when WM rolls out a new fee %
	 * but for "in-progress" assignments we kept a previous one.
	 * 2) the work fee configuration associated with the account register
	 * @param work
	 * @param amount
	 * @return BigDecimal - the fee amount
	 */
	BigDecimal calculateBuyerNetMoneyFee(AbstractWork work, BigDecimal amount);

	RegisterTransactionCost findCostForTransactionType(String registerTransactionTypeCode, AccountRegister accountRegister);

	Map<String,RegisterTransactionCostDTO> findCostForTransactionTypesByCompany(Long userId, Collection<String> registerTransactionTypeCodes);

	WorkFeeConfiguration findActiveWorkFeeConfiguration(Long companyId);

	void saveAndActivateWorkFeeConfiguration(Long companyId, WorkFeeConfigurationDTO workFeeConfigurationDTO);

	BigDecimal findDrugTestPrice(Long companyId);
	BigDecimal findBackgroundCheckPrice(Long companyId);
	BigDecimal findBackgroundCheckPrice(Long companyId, String countryCode);

	/**
	 * DISCLAIMER: This is a dirty dirty hack
	 * DO NOT USE THIS METHOD TO RETRIEVE A LOCKED-FOR-WRITING ACCOUNT REGISTER (use findDefaultRegisterForCompany(Long, boolean) instead)
	 * THIS WAS ONLY CREATED TO MAKE DELETING THIS TRASH, IN THE FUTURE @_@, MUCH EASIER

	 * This method is only called for the side effect of locking the Account Register entry for eventual writing
	 * The Account Register is never used
	 */
	void lockAccountRegisterForWritingHack(Long companyId);

	/**
	 * @param companyId
	 * @return Account Register that's NOT locked exclusively for writing
	 */

	AccountRegister findDefaultRegisterForCompany(Long companyId);

	AccountRegister findDefaultRegisterForCompany(Long companyId, boolean lockForWriting);

	AccountRegister updateAPLimit(Long companyId, String amount) throws OverAPLimitException;

	BigDecimal calculateRemainingAPBalance(Long companyId);

	/**
	 * If the account has TRANSACTIONAL pricing type, this method will return the correct fee band.
	 * Otherwise it will return NULL.
	 *
	 * @param accountRegister
	 * @return
	 */
	WorkFeeBand determineWorkFeeBand(AccountRegister accountRegister);

	WorkFeeBand determineWorkFeeBand(Company company);

	BigDecimal calculateBuyerNetMoneyFee(Company company, BigDecimal amount);

	/**
	 * Buyer's can set an assignment's pricing in terms of "I want to spend";
	 * adjust the actual prices accordingly based on their company's WM fee.
	 * @param pricing
	 * @param companyId
	 * @return New PricingStrategy w/adjusted values
	 * @throws Exception
	 */
	PricingStrategy adjustPricingByCompanyFeePercentage(PricingStrategy pricing, Long companyId);
	PricingStrategy adjustPricingByCompanyFeePercentage(PricingStrategy pricing, Long companyId, Long workId);

	/**
	 * Finds the right fee percentage for a particular assignment.
	 * When there are changes on the pricing for a company account the assignments that were open when the transition
	 * occurred or before the effective date, should keep the fee with which they were created.
	 * For new assignments we take whatever is active for the company at the time of creation.
	 *
	 * @param work
	 * @return
	 */
	BigDecimal getCurrentFeePercentageForWork(AbstractWork work);

	BigDecimal getCurrentFeePercentageForCompany(Long companyId);

	BigDecimal getCurrentFeePercentageForWork(Long workId);

	PricingStrategy copyPricingStrategy(PricingStrategy pricing);
}
