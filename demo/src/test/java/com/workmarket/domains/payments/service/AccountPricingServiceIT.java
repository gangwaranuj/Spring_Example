package com.workmarket.domains.payments.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.service.business.dto.account.pricing.AccountServiceTypeDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AccountPricingServiceIT extends BaseServiceIT {

	@Autowired private AccountPricingService accountPricingService;
	@Autowired private CompanyService companyService;

	private Company company;
	private User employee;

	@Test
	public void testUpdateAccountServiceTypeForCompany() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);
		Company company = companyService.findCompanyById(employee.getCompany().getId());
		Assert.assertNotNull(company);
		Assert.assertTrue(company.getAccountPricingType().isTransactionalPricing());
		Assert.assertTrue(company.getPaymentConfiguration().findAccountServiceTypeForCountry(Country.CANADA).isNone());
		Assert.assertTrue(company.getPaymentConfiguration().findAccountServiceTypeForCountry(Country.USA).isNone());

		List<AccountServiceTypeDTO> dtos = Lists.newArrayList();
		dtos.add(new AccountServiceTypeDTO(AccountServiceType.TAX_SERVICE_1099, Country.USA));
		accountPricingService.updateCompanyAccountServiceType(company.getId(), dtos);

		company = companyService.findCompanyById(employee.getCompany().getId());
		Assert.assertTrue(company.getAccountPricingType().isTransactionalPricing());
		Assert.assertTrue(company.getPaymentConfiguration().findAccountServiceTypeForCountry(Country.CANADA).isNone());
		Assert.assertTrue(company.getPaymentConfiguration().findAccountServiceTypeForCountry(Country.USA).isTaxService());
		Assert.assertNotNull(company.getPaymentConfiguration().getAccountServiceTypeModifiedOn());
	}

	@Before
	public void initialize() throws Exception {
		employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);
		company = companyService.findCompanyById(employee.getCompany().getId());

		List<AccountServiceTypeDTO> dtos = Lists.newArrayList();
		dtos.add(new AccountServiceTypeDTO(AccountServiceType.TAX_SERVICE_1099, Country.USA));
		dtos.add(new AccountServiceTypeDTO(AccountServiceType.VENDOR_OF_RECORD, Country.CANADA));
		accountPricingService.updateCompanyAccountServiceType(company.getId(), dtos);
		company = companyService.findCompanyById(employee.getCompany().getId());
	}
	@Test
	public void testFindAccountServiceTypeConfiguration_USA(){
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, Country.USA);
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_CANADA() throws Exception {
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, Country.CANADA);
		assertEquals(AccountServiceType.VENDOR_OF_RECORD, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_AS() throws Exception {
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, Country.PR);
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_GU() throws Exception {
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, Country.PR);
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_MP() throws Exception {
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, Country.PR);
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_PR() throws Exception {
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, Country.PR);
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_UM() throws Exception {
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, Country.PR);
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_VI() throws Exception {
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, Country.PR);
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_UNKNOWN() throws Exception {
		AccountServiceType accountServiceType = accountPricingService.findAccountServiceTypeConfiguration(company, "UNKNOWN");
		assertEquals(AccountServiceType.NONE, accountServiceType.getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_TaxEntityUSA() throws Exception {

		User contractor = newContractor();
		setTaxEntity(contractor, AbstractTaxEntity.COUNTRY_USA);

		authenticationService.setCurrentUser(employee);
		// create work without address
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		laneService.addUserToCompanyLane2(contractor.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		work = workService.findWork(work.getId());

		// Should use tax entity country
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountPricingService.findAccountServiceTypeConfiguration(work).getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_TaxEntityCanada() throws Exception {

		User contractor = newContractor();
		setTaxEntity(contractor, AbstractTaxEntity.COUNTRY_CANADA);

		authenticationService.setCurrentUser(employee);
		// create work without address
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		laneService.addUserToCompanyLane2(contractor.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		work = workService.findWork(work.getId());

		// Should use tax entity country
		assertEquals(AccountServiceType.VENDOR_OF_RECORD, accountPricingService.findAccountServiceTypeConfiguration(work).getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_ResourceCompanyCountryCanada() throws Exception {

		User contractor = newContractor();
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1("40 Bay St");
		addressDTO.setCity("Toronto");
		addressDTO.setState("ON");
		addressDTO.setPostalCode("M5J 2X2");
		addressDTO.setCountry(Country.CANADA);
		profileService.saveOrUpdateCompanyAddress(contractor.getId(), addressDTO);

		authenticationService.setCurrentUser(employee);
		// create work without address
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		laneService.addUserToCompanyLane2(contractor.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		work = workService.findWork(work.getId());

		// Should use tax entity country
		assertEquals(AccountServiceType.VENDOR_OF_RECORD, accountPricingService.findAccountServiceTypeConfiguration(work).getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_ResourceCompanyCountryUS() throws Exception {

		User contractor = newContractor();
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1("7 High St");
		addressDTO.setCity("Huntinton");
		addressDTO.setState("NY");
		addressDTO.setPostalCode("11743");
		addressDTO.setCountry(Country.USA);
		profileService.saveOrUpdateCompanyAddress(contractor.getId(), addressDTO);

		authenticationService.setCurrentUser(employee);
		// create work without address
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		laneService.addUserToCompanyLane2(contractor.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		work = workService.findWork(work.getId());

		// Should use tax entity country
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountPricingService.findAccountServiceTypeConfiguration(work).getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_ResourceProfileCountryUS() throws Exception {

		User contractor = newContractor();

		Address address = new Address();
		address.setAddress1("200 Fulton Street");
		address.setAddress2("4th Floor");
		address.setCity("New York");
		address.setState(invariantDataService.findState("NY"));
		address.setCountry(Country.USA_COUNTRY);
		address.setPostalCode("10007");
		address.setAddressType(new AddressType("business"));
		address.setLatitude(new BigDecimal(40.711454));
		address.setLongitude(new BigDecimal(-74.010427));

		profileService.updateAddress(contractor.getProfile().getId(), address);

		authenticationService.setCurrentUser(employee);
		// create work without address
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		laneService.addUserToCompanyLane2(contractor.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		work = workService.findWork(work.getId());

		// Should use tax entity country
		assertEquals(AccountServiceType.TAX_SERVICE_1099, accountPricingService.findAccountServiceTypeConfiguration(work).getCode());
	}

	@Test
	public void testFindAccountServiceTypeConfiguration_ResourceProfileCountryCanada() throws Exception {

		User contractor = newContractor();

		Address address = new Address();
		address.setAddress1("40 Bay St");
		address.setCity("Toronto");
		address.setState(invariantDataService.findState("ON"));
		address.setPostalCode("M5J 2X2");
		address.setCountry(Country.CANADA_COUNTRY);
		address.setAddressType(new AddressType("business"));

		profileService.updateAddress(contractor.getProfile().getId(), address);

		authenticationService.setCurrentUser(employee);
		// create work without address
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);
		laneService.addUserToCompanyLane2(contractor.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		work = workService.findWork(work.getId());

		// Should use tax entity country
		assertEquals(AccountServiceType.VENDOR_OF_RECORD, accountPricingService.findAccountServiceTypeConfiguration(work).getCode());
	}

	private void setTaxEntity(User contractor, String taxCountry) {
		TaxEntityDTO taxEntityDTO = new TaxEntityDTO();
		taxEntityDTO.setAddress("20 West 20th Street");
		taxEntityDTO.setCity("New York");
		taxEntityDTO.setState("NY");
		taxEntityDTO.setPostalCode("10011");
		taxEntityDTO.setCountry(taxCountry);
		taxEntityDTO.setTaxCountry(taxCountry);
		taxEntityDTO.setTaxEntityTypeCode(TaxEntityType.CORP);
		taxEntityDTO.setBusinessFlag(false);
		taxEntityDTO.setTaxName(RandomUtilities.generateAlphaString(10));
		taxEntityDTO.setTaxNumber(UUID.randomUUID().toString().substring(0, 24));
		taxEntityDTO.setActiveFlag(true);
		taxEntityDTO.setTaxVerificationStatusCode(TaxVerificationStatusType.APPROVED);
		authenticationService.setCurrentUser(contractor.getId());
		taxService.saveTaxEntityForCompany(contractor.getCompany().getId(), taxEntityDTO);
	}
}
