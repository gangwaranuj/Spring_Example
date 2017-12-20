package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.WorkFeeBand;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.domains.model.pricing.*;
import com.workmarket.service.business.dto.WorkFeeBandDTO;
import com.workmarket.service.business.dto.WorkFeeConfigurationDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.test.IntegrationTest;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class PricingServiceIT extends BaseServiceIT {

	@Autowired PricingService pricingService;

	@Test @Transactional
	public void testCreateNewUserWithDefaultWorkFee() throws Exception {
		User user = super.newContractor();

		// new users should have default terms
		boolean foundWorkFeeConfiguration = false;
		boolean foundWorkFeeBand = false;

		Set<AccountRegister> accountRegisters = user.getCompany().getAccountRegisters();

		for (AccountRegister register : accountRegisters) {
			for (WorkFeeConfiguration workFeeConfiguration : register.getWorkFeeConfigurations()) {
				foundWorkFeeConfiguration = true;
				for (WorkFeeBand band : workFeeConfiguration.getWorkFeeBands()) {
					foundWorkFeeBand = true;
				}
			}
		}

		assertTrue(foundWorkFeeConfiguration);
		assertTrue(foundWorkFeeBand);
	}

	@Test @Transactional
	public void testSaveAndActiveWorkFeeConfiguration() throws Exception {
		User user = super.newContractor();
		WorkFeeConfiguration defaultConfiguration = pricingService.findActiveWorkFeeConfiguration(user.getCompany().getId());
		assertNotNull(defaultConfiguration.getWorkFeeBands());

		WorkFeeBandDTO workFeeBand = new WorkFeeBandDTO();
		workFeeBand.setMaximum("250000");
		workFeeBand.setMinimum("0");
		workFeeBand.setPercentage("6.00");

		WorkFeeBandDTO workFeeBand2 = new WorkFeeBandDTO();
		workFeeBand2.setMaximum("250000");
		workFeeBand2.setMinimum("0");
		workFeeBand2.setPercentage("3.00");

		List<WorkFeeBandDTO> workFeeBandDTOs = new ArrayList<WorkFeeBandDTO>();
		workFeeBandDTOs.add(workFeeBand);
		workFeeBandDTOs.add(workFeeBand);
		workFeeBandDTOs.add(workFeeBand2);
		workFeeBandDTOs.add(workFeeBand2);

		WorkFeeConfigurationDTO configDTO = new WorkFeeConfigurationDTO();
		configDTO.setWorkFeeBandDTOs(workFeeBandDTOs);

		pricingService.saveAndActivateWorkFeeConfiguration(user.getCompany().getId(), configDTO);
		WorkFeeConfiguration activeConfiguration = pricingService.findActiveWorkFeeConfiguration(user.getCompany().getId());
		assertNotNull(activeConfiguration);

		boolean found3Percent = false;
		boolean found6Percent = false;

		for (WorkFeeBand foundBand : activeConfiguration.getWorkFeeBands()) {
			if (foundBand.getPercentage().equals(new BigDecimal("6.00"))) {
				found6Percent = true;
			}
			if (foundBand.getPercentage().equals(new BigDecimal("3.00"))) {
				found3Percent = true;
			}
		}

		assertTrue(found6Percent);
		assertTrue(found3Percent);
	}

	// Flat pricing
	@Test @Transactional
	public void testAdjustPricingByFeePercentageForFlatPricing() throws Exception {
		User user = super.newContractor();
		Long companyId = user.getCompany().getId();
		BigDecimal percentage;
		BigDecimal inputPrice;
		BigDecimal expectedPrice;
		PricingStrategy newPricing;
		FlatPricePricingStrategy pricing = new FlatPricePricingStrategy();
		WorkFeeConfiguration activeConfiguration = pricingService.findActiveWorkFeeConfiguration(companyId);

		Hibernate.initialize(activeConfiguration.getAccountRegister());
		activeConfiguration.getAccountRegister().setCurrentWorkFeePercentage(Constants.DEFAULT_WORK_FEE_PERCENTAGE);
		percentage = Constants.DEFAULT_WORK_FEE_PERCENTAGE.movePointLeft(2);

		// Case: doesn't exceed max fee
		inputPrice = new BigDecimal(100.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);

		//expectedPrice = inputPrice / (1 + percentage)
		expectedPrice = inputPrice.divide(percentage.add(BigDecimal.ONE), 8, RoundingMode.HALF_UP);

		pricing.setFlatPrice(inputPrice);
		newPricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, companyId);
		assertEquals(expectedPrice, newPricing.getFullPricingStrategy().getFlatPrice());

		// Case: exceeds max fee
		// (inputPrice * percentage) > MAX_WORK_FEE  -->  inputPrice > (MAX_WORK_FEE / percentage)
		inputPrice = Constants.MAX_WORK_FEE.divide(percentage)
			.add(BigDecimal.ONE)	// add 1 to exceed MAX_WORK_FEE
			.setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);

		expectedPrice = inputPrice.subtract(Constants.MAX_WORK_FEE);

		pricing.setFlatPrice(inputPrice);
		newPricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, companyId);
		assertEquals(expectedPrice, newPricing.getFullPricingStrategy().getFlatPrice());
	}

	// Per hour pricing
	@Test @Transactional
	public void testAdjustPricingByFeePercentageForPerHourPricing() throws Exception {
		User user = super.newContractor();
		Long companyId = user.getCompany().getId();
		BigDecimal percentage;
		BigDecimal inputPerHourPrice;
		BigDecimal maxNumberOfHours;
		BigDecimal expectedPrice;
		PricingStrategy newPricing;
		PerHourPricingStrategy pricing = new PerHourPricingStrategy();
		WorkFeeConfiguration activeConfiguration = pricingService.findActiveWorkFeeConfiguration(companyId);

		Hibernate.initialize(activeConfiguration.getAccountRegister());
		activeConfiguration.getAccountRegister().setCurrentWorkFeePercentage(Constants.DEFAULT_WORK_FEE_PERCENTAGE);
		percentage = Constants.DEFAULT_WORK_FEE_PERCENTAGE.movePointLeft(2);

		// Doesn't exceed max fee
		inputPerHourPrice = new BigDecimal(100.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);
		maxNumberOfHours = new BigDecimal(10.0);

		// expectedPrice = (inputPerHourPrice / (1 + percentage))
		expectedPrice = inputPerHourPrice.divide(percentage.add(BigDecimal.ONE), 8, RoundingMode.HALF_UP);

		pricing.setPerHourPrice(inputPerHourPrice);
		pricing.setMaxNumberOfHours(maxNumberOfHours);
		newPricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, companyId);
		assertEquals(expectedPrice, newPricing.getFullPricingStrategy().getPerHourPrice());

		// Exceeds max fee
		inputPerHourPrice = new BigDecimal(100.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);
		maxNumberOfHours = new BigDecimal(100.0);

		// expectedPrice = inputPrice - (MAX_WORK_FEE / maxNumberOfHours)
		expectedPrice = inputPerHourPrice.subtract(Constants.MAX_WORK_FEE.divide(maxNumberOfHours, 8, RoundingMode.HALF_UP));

		pricing.setPerHourPrice(inputPerHourPrice);
		pricing.setMaxNumberOfHours(maxNumberOfHours);
		newPricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, companyId);
		assertEquals(expectedPrice, newPricing.getFullPricingStrategy().getPerHourPrice());
	}

	// Per unit pricing
	@Test @Transactional
	public void testAdjustPricingByFeePercentageForPerUnitPricing() throws Exception {
		User user = super.newContractor();
		Long companyId = user.getCompany().getId();
		BigDecimal percentage;
		BigDecimal inputPerUnitPrice;
		BigDecimal maxNumberOfUnits;
		BigDecimal expectedPrice;
		PricingStrategy newPricing;
		PerUnitPricingStrategy pricing = new PerUnitPricingStrategy();
		WorkFeeConfiguration activeConfiguration = pricingService.findActiveWorkFeeConfiguration(companyId);

		Hibernate.initialize(activeConfiguration.getAccountRegister());
		activeConfiguration.getAccountRegister().setCurrentWorkFeePercentage(Constants.DEFAULT_WORK_FEE_PERCENTAGE);
		percentage = Constants.DEFAULT_WORK_FEE_PERCENTAGE.movePointLeft(2);

		// Doesn't exceed max fee
		inputPerUnitPrice = new BigDecimal(100.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);
		maxNumberOfUnits = new BigDecimal(10.0);

		// expectedPrice = inputPerUnitPrice / (1 + percentage)
		expectedPrice = inputPerUnitPrice.divide(percentage.add(BigDecimal.ONE), 8, RoundingMode.HALF_UP);

		pricing.setPerUnitPrice(inputPerUnitPrice);
		pricing.setMaxNumberOfUnits(maxNumberOfUnits);
		newPricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, companyId);
		assertEquals(expectedPrice, newPricing.getFullPricingStrategy().getPerUnitPrice());

		// Exceeds max fee
		inputPerUnitPrice = new BigDecimal(100.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);
		maxNumberOfUnits = new BigDecimal(100.0);

		// expectedPrice = inputPerUnitPrice - (MAX_WORK_FEE / maxNumberOfUnits)
		expectedPrice = inputPerUnitPrice.subtract(Constants.MAX_WORK_FEE.divide(maxNumberOfUnits, 8, RoundingMode.HALF_UP));

		pricing.setPerUnitPrice(inputPerUnitPrice);
		pricing.setMaxNumberOfUnits(maxNumberOfUnits);
		newPricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, companyId);
		assertEquals(expectedPrice, newPricing.getFullPricingStrategy().getPerUnitPrice());
	}

	// Blended per hour pricing
	@Test @Transactional
	public void testAdjustPricingByFeePercentageForBlendedPerHourPricing() throws Exception {
		User user = super.newContractor();
		Long companyId = user.getCompany().getId();
		BigDecimal percentage;
		BigDecimal inputInitialPerHourPrice;
		BigDecimal initialNumberOfHours;
		BigDecimal inputAdditionalPerHourPrice;
		BigDecimal maxBlendedHours;
		BigDecimal expectedInitialPerHourPrice;
		BigDecimal expectedAdditionalPerHourPrice;
		BigDecimal inputTotalPrice;
		BigDecimal proportion;
		PricingStrategy newPricing;

		BlendedPerHourPricingStrategy pricing = new BlendedPerHourPricingStrategy();
		WorkFeeConfiguration activeConfiguration = pricingService.findActiveWorkFeeConfiguration(companyId);

		activeConfiguration.getAccountRegister().setCurrentWorkFeePercentage(Constants.DEFAULT_WORK_FEE_PERCENTAGE);
		percentage = Constants.DEFAULT_WORK_FEE_PERCENTAGE.movePointLeft(2);

		// Doesn't exceed max fee
		initialNumberOfHours = new BigDecimal(20.0);
		inputInitialPerHourPrice = new BigDecimal(30.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);

		maxBlendedHours = new BigDecimal(10.0);
		inputAdditionalPerHourPrice = new BigDecimal(20.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);

		// expectedInitialPerHourPrice = inputInitialPerHourPrice / (1 + percentage)
		expectedInitialPerHourPrice = inputInitialPerHourPrice.divide(percentage.add(BigDecimal.ONE), 8, RoundingMode.HALF_UP);

		// expectedAdditionalPerHourPrice = inputAdditionalPerHourPrice / (1 + percentage)
		expectedAdditionalPerHourPrice = inputAdditionalPerHourPrice.divide(percentage.add(BigDecimal.ONE), 8, RoundingMode.HALF_UP);

		pricing.setInitialPerHourPrice(inputInitialPerHourPrice);
		pricing.setInitialNumberOfHours(initialNumberOfHours);
		pricing.setAdditionalPerHourPrice(inputAdditionalPerHourPrice);
		pricing.setMaxBlendedNumberOfHours(maxBlendedHours);
		newPricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, companyId);
		assertEquals(expectedInitialPerHourPrice, newPricing.getFullPricingStrategy().getInitialPerHourPrice());
		assertEquals(expectedAdditionalPerHourPrice, newPricing.getFullPricingStrategy().getAdditionalPerHourPrice());

		// Exceeds max fee
		initialNumberOfHours = new BigDecimal(200.0);
		inputInitialPerHourPrice = new BigDecimal(30.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);

		maxBlendedHours = new BigDecimal(100.0);
		inputAdditionalPerHourPrice = new BigDecimal(20.0).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE);

		// inputTotalPrice = (initialNumberOfHours * inputInitialPerHourPrice) + (maxBlendedHours * inputAdditionalPerHourPrice)
		inputTotalPrice = initialNumberOfHours.multiply(inputInitialPerHourPrice)
			.add(maxBlendedHours.multiply(inputAdditionalPerHourPrice));

		// Proportion in which per hour price varies: 1 - (MAX_WORK_FEE / inputTotalPrice)
		proportion = BigDecimal.ONE.subtract(Constants.MAX_WORK_FEE.divide(inputTotalPrice, 8, RoundingMode.HALF_UP));

		expectedInitialPerHourPrice = inputInitialPerHourPrice.multiply(proportion);
		expectedAdditionalPerHourPrice = inputAdditionalPerHourPrice.multiply(proportion);

		pricing.setInitialPerHourPrice(inputInitialPerHourPrice);
		pricing.setInitialNumberOfHours(initialNumberOfHours);
		pricing.setAdditionalPerHourPrice(inputAdditionalPerHourPrice);
		pricing.setMaxBlendedNumberOfHours(maxBlendedHours);
		newPricing = pricingService.adjustPricingByCompanyFeePercentage(pricing, companyId);
		assertEquals(expectedInitialPerHourPrice, newPricing.getFullPricingStrategy().getInitialPerHourPrice());
		assertEquals(expectedAdditionalPerHourPrice, newPricing.getFullPricingStrategy().getAdditionalPerHourPrice());
	}
}
