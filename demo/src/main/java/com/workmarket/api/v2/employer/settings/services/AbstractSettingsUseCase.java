package com.workmarket.api.v2.employer.settings.services;

import com.google.common.collect.Lists;
import com.workmarket.api.v2.employer.assignments.services.UseCase;
import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentResponseDTO;
import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BankingService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.account.CreditCardErrorException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.validation.ValidationService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.util.AutoPopulatingList;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public abstract class AbstractSettingsUseCase<T, K> implements UseCase<T, K> {

	private static final Log logger = LogFactory.getLog(AbstractSettingsUseCase.class);
	private static final String IGNORE_PROPERTY = "name";

	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired protected AuthenticationService authenticationService;
	@Autowired protected ProfileService profileService;
	@Autowired protected UserService userService;
	@Autowired protected CompanyService companyService;
	@Autowired protected BankingService bankingService;
	@Autowired protected TaxService taxService;
	@Autowired protected EventRouter eventRouter;
	@Autowired protected MessageBundleHelper messageBundleHelper;
	@Autowired protected PricingService pricingService;
	@Autowired protected AccountRegisterService accountRegisterServicePrefundImpl;
	@Autowired @Qualifier("companyProfileValidationService") ValidationService companyProfileValidationService;
	@Autowired @Qualifier("creditCardPaymentValidationService") ValidationService paymentValidationService;
	@Autowired @Qualifier("addressValidationService") ValidationService addressValidationService;
	@Autowired @Qualifier("achBankAccountValidationService") ValidationService bankAccountValidationService;
	@Autowired @Qualifier("taxInfoValidationService") ValidationService taxInfoValidationService;

	private List<ConstraintViolation> errors =  Lists.newArrayList();
	protected Exception exception;
	protected User user;
	protected ExtendedUserDetails userDetails;
	protected Company company;
	protected BankAccountDTO bankAccountDTO;
	protected AbstractBankAccount bankAccount;
	protected AddressDTO addressDTO;
	protected TaxEntityDTO taxEntityDTO;
	protected AbstractTaxEntity taxEntity;
	protected AccountRegister accountRegister;
	protected PaymentDTO paymentDTO;
	protected PaymentResponseDTO paymentResponseDTO;

	protected LocationDTO locationDTO;
	protected LocationDTO.Builder locationDTOBuilder = new LocationDTO.Builder();
	protected CompanyProfileDTO companyProfileDTO;
	protected CompanyProfileDTO.Builder companyProfileDTOBuilder = new CompanyProfileDTO.Builder();
	protected ACHBankAccountDTO achBankAccountDTO;
	protected ACHBankAccountDTO.Builder achBankAccountDTOBuilder = new ACHBankAccountDTO.Builder();
	protected TaxInfoDTO taxInfoDTO;
	protected TaxInfoDTO.Builder taxInfoDTOBuilder = new TaxInfoDTO.Builder();
	protected CreditCardPaymentDTO creditCardPaymentDTO;
	protected CreditCardPaymentDTO.Builder creditCardPaymentDTOBuilder = new CreditCardPaymentDTO.Builder();
	protected CreditCardPaymentResponseDTO creditCardPaymentResponseDTO;
	protected CreditCardPaymentResponseDTO.Builder creditCardPaymentResponseDTOBuilder = new CreditCardPaymentResponseDTO.Builder();

	protected abstract T me();
	protected abstract T handleExceptions() throws Exception;

	@Override
	public T execute() {
		try {
			failFast();
			init();
			prepare();
			process();
			save();
			finish();
		} catch (ValidationException | InvalidAclRoleException | BeansException | CreditCardErrorException |
			HostServiceException | IOException | AssetTransformationException e) {
			exception = e;
		}
		return me();
	}

	protected void failFast() {
		// no-op default implementation
		// override to add behavior
	}

	protected void init() {
		// no-op default implementation
		// override to add behavior
	}

	protected void prepare() {
		// no-op default implementation
		// override to add behavior
	}

	protected void process() throws ValidationException, CreditCardErrorException {
		// no-op default implementation
		// override to add behavior
	}

	protected void save() throws ValidationException, InvalidAclRoleException, HostServiceException, IOException,
		AssetTransformationException {
		// no-op default implementation
		// override to add behavior
	}

	protected void finish() {
		// no-op default implementation
		// override to add behavior
	}

	protected void createAddressDTO() {
		this.addressDTO = new AddressDTO();
	}

	protected void createBankAccountDTO() {
		this.bankAccountDTO = new BankAccountDTO();
	}

	protected void createTaxEntityDTO() {
		this.taxEntityDTO = new TaxEntityDTO();
	}

	protected void createPaymentDTO() {
		this.paymentDTO = new PaymentDTO();
	}

	protected void copyCompanyProfileDTO() {
		this.companyProfileDTOBuilder = new CompanyProfileDTO.Builder(companyProfileDTO);
	}

	protected void copyACHBankAccountDTO() {
		this.achBankAccountDTOBuilder = new ACHBankAccountDTO.Builder(achBankAccountDTO);
	}

	protected void copyTaxInfoDTO() {
		this.taxInfoDTOBuilder = new TaxInfoDTO.Builder(taxInfoDTO);
	}

	protected void copyCreditCardPaymentDTO() {
		this.creditCardPaymentDTOBuilder = new CreditCardPaymentDTO.Builder(creditCardPaymentDTO);
	}

	protected void getUser() {
		user = authenticationService.getCurrentUser();
	}

	protected void getUserDetails() {
		userDetails = securityContextFacade.getCurrentUser();
	}

	protected void getCompany() {
		company = profileService.findCompany(user.getId());
	}

	protected Address getCompanyAddress() {
		Assert.notNull(user);
		return profileService.findCompanyAddress(user.getId());
	}

	protected AddressDTO toAddressDTO() {
		createAddressDTO();
		Assert.notNull(locationDTO);
		Assert.notNull(addressDTO);
		addressDTO.setAddress1(locationDTO.getAddressLine1());
		addressDTO.setAddress2(locationDTO.getAddressLine2());
		addressDTO.setCity(locationDTO.getCity());
		addressDTO.setState(locationDTO.getState());
		addressDTO.setPostalCode(locationDTO.getZip());
		addressDTO.setCountry(Country.valueOf(locationDTO.getCountry()).getId());
		addressDTO.setLatitude(locationDTO.getLatitude() == null ? null : new BigDecimal(locationDTO.getLatitude()));
		addressDTO.setLongitude(locationDTO.getLongitude() == null ? null : new BigDecimal(locationDTO.getLongitude()));
		return addressDTO;
	}

	protected void toBankAccountDTO() throws BeansException {
		createBankAccountDTO();
		Assert.notNull(achBankAccountDTO);
		Assert.notNull(bankAccountDTO);
		BeanUtils.copyProperties(achBankAccountDTO, bankAccountDTO);
	}

	protected void toTaxEntityDTO() throws BeansException {
		createTaxEntityDTO();
		Assert.notNull(taxInfoDTO);
		Assert.notNull(taxEntityDTO);
		BeanUtils.copyProperties(taxInfoDTO, taxEntityDTO);
	}

	protected void toPaymentDTO() throws BeansException {
		createPaymentDTO();
		Assert.notNull(creditCardPaymentDTO);
		Assert.notNull(paymentDTO);
		BeanUtils.copyProperties(creditCardPaymentDTO, paymentDTO);
		locationDTO = creditCardPaymentDTO.getLocation();
		Assert.notNull(locationDTO);
		paymentDTO.setAddress1(locationDTO.getAddressLine1());
		paymentDTO.setCity(locationDTO.getCity());
		paymentDTO.setState(locationDTO.getState());
		paymentDTO.setPostalCode(locationDTO.getZip());
		paymentDTO.setCountry(locationDTO.getCountry());
	}

	protected void loadCompany() throws BeansException {
		Assert.notNull(companyProfileDTO);
		Assert.notNull(company);
		BeanUtils.copyProperties(companyProfileDTO, company, IGNORE_PROPERTY);
	}

	protected void loadCompanyProfileDTO() {
		Assert.notNull(company);

		companyProfileDTOBuilder
			.setName(company.getName())
			.setOverview(company.getOverview())
			.setWebsite(company.getWebsite())
			.setYearFounded(company.getYearFounded())
			.setInVendorSearch(company.isInVendorSearch());

		CompanyAssetAssociation avatars = companyService.findCompanyAvatars(company.getId());

		if(avatars != null && avatars.getSmall() != null) {
			companyProfileDTOBuilder.setAvatar(avatars.getSmall().getCdnUri());
		}

		Address address = getCompanyAddress();

		if (address != null) {
			locationDTOBuilder.setAddressLine1(address.getAddress1())
				.setAddressLine2(address.getAddress2())
				.setCity(address.getCity())
				.setState(address.getState().getShortName())
				.setCountry(address.getCountry().getISO3())
				.setZip(address.getPostalCode())
				.setLatitude(address.getLatitude().doubleValue())
				.setLongitude(address.getLongitude().doubleValue());

			companyProfileDTOBuilder.setLocation(locationDTOBuilder);
		}

		companyProfileDTO = companyProfileDTOBuilder.build();
	}

	protected void loadACHBankAccountDTO() {
		Assert.notNull(bankAccount);
		achBankAccountDTOBuilder.setType(bankAccount.getType())
			.setBankName(bankAccount.getBankName())
			.setCountry(bankAccount.getCountry().getId())
			.setBankAccountTypeCode(bankAccount.getBankAccountType().getCode())
			.setAccountNumber(bankAccount.getBankAccountSecureNumber())
			.setNameOnAccount(bankAccount.getNameOnAccount());
		achBankAccountDTO = achBankAccountDTOBuilder.build();
	}

	protected void loadPaymentResponseDTO() {
		Assert.notNull(paymentResponseDTO);
		creditCardPaymentResponseDTOBuilder.setApproved(paymentResponseDTO.isApproved())
			.setResponseMessage(paymentResponseDTO.getResponseMessage())
			.setCreditCardTransactionId(paymentResponseDTO.getCreditCardTransactionId())
			.setCreditCardFeeTransactionId(paymentResponseDTO.getCreditCardFeeTransactionId());
		creditCardPaymentResponseDTO = creditCardPaymentResponseDTOBuilder.build();
	}

	private boolean isLocationEmpty() {
		return
			StringUtils.isEmpty(locationDTO.getAddressLine1()) &&
			StringUtils.isEmpty(locationDTO.getCity()) &&
			StringUtils.isEmpty(locationDTO.getState()) &&
			StringUtils.isEmpty(locationDTO.getCountry()) &&
			StringUtils.isEmpty(locationDTO.getZip());
	}

	protected void validateCompanyProfile() {
		companyProfileValidationService.validate(companyProfileDTO, errors);
	}

	protected void validateCompanyAddress() {
		Assert.notNull(companyProfileDTO);
		locationDTO = companyProfileDTO.getLocation();
		if (locationDTO != null && !isLocationEmpty()) {
			addressValidationService.validate(toAddressDTO(), errors);
		}
	}

	protected void validateBankAccount() throws BeansException {
		toBankAccountDTO();
		bankAccountValidationService.validate(bankAccountDTO, errors);
	}

	protected void validateTaxInfo() {
		taxInfoValidationService.validate(taxInfoDTO, errors);
	}

	protected void validateCreditCardPayment() {
		paymentValidationService.validate(creditCardPaymentDTO, errors);
	}

	protected void validateCreditCardBillingAddress() {
		Assert.notNull(creditCardPaymentDTO);
		locationDTO = creditCardPaymentDTO.getLocation();
		AddressDTO billingAddress = toAddressDTO();
		addressValidationService.validate(billingAddress, errors);
	}

	protected void saveCompanyProfile() throws ValidationException {
		if (!CollectionUtilities.isEmpty(errors)) {
			throw new ValidationException("Unable to save company profile", errors);
		}
		Assert.notNull(company);
		profileService.saveOrUpdateCompany(company);

		if(addressDTO != null) {
			profileService.saveOrUpdateCompanyAddress(user.getId(), addressDTO);
			Profile userProfile = profileService.findProfile(user.getId());
			if(userProfile.getAddressId() == null) {
				// Update address properties.
				Map<String, String> addressProperties = CollectionUtilities.newStringMap(
					"address1", addressDTO.getAddress1(),
					"address2", addressDTO.getAddress2(),
					"city", addressDTO.getCity(),
					"state", addressDTO.getState(),
					"postalCode", addressDTO.getPostalCode(),
					"country", addressDTO.getCountry(),
					"latitude", String.valueOf(addressDTO.getLatitude()),
					"longitude", String.valueOf(addressDTO.getLongitude()),
					"addressType", "profile");
				profileService.updateProfileAddressProperties(user.getId(), addressProperties);
			}

			List<String> blacklistedCodes = profileService.findBlacklistedZipcodesForUser(user.getId());
			if (org.apache.commons.collections.CollectionUtils.isNotEmpty(blacklistedCodes) && blacklistedCodes.contains(addressDTO.getPostalCode())) {
				blacklistedCodes.remove(addressDTO.getPostalCode());
				profileService.setBlacklistedZipcodesForUser(user.getId(), blacklistedCodes);
			}
		}
	}

	protected void saveWorkInviteSentTo() throws InvalidAclRoleException {
		//Assigning Dispatcher role to user specified for all incoming work invites
		Assert.notNull(companyProfileDTO);
		if(companyProfileDTO.getInVendorSearch() && StringUtils.isNotEmpty(companyProfileDTO.getWorkInviteSentToUserId())) {
			User workInviteSentToUser = userService.findUserByUserNumber(companyProfileDTO.getWorkInviteSentToUserId());
			Assert.notNull(workInviteSentToUser);
			Long[] roles = new Long[]{AclRole.ACL_DISPATCHER, AclRole.ACL_WORKER};
			authenticationService.assignAclRolesToUser(workInviteSentToUser.getId(), roles);
		}
	}

	protected void saveBankAccount() throws ValidationException {
		Assert.notNull(bankAccountDTO);
		if (!CollectionUtilities.isEmpty(errors)) {
			throw new ValidationException("Unable to save bank account", errors);
		}
		bankAccount = bankingService.saveBankAccount(userDetails.getId(), bankAccountDTO);
	}

	protected void saveTaxInfo() throws ValidationException, BeansException {
		if (!CollectionUtilities.isEmpty(errors)) {
			throw new ValidationException("Unable to save tax information", errors);
		}
		toTaxEntityDTO();
		taxService.saveTaxEntity(user.getId(), taxEntityDTO);
	}

	protected void prepareTaxInfoDTO() {
		Assert.notNull(taxInfoDTO);
		Assert.notNull(taxInfoDTOBuilder);
		if (taxInfoDTO.getBusinessFlag()) {
			if (AbstractTaxEntity.COUNTRY_CANADA.equals(taxInfoDTO.getTaxCountry()) ||
				AbstractTaxEntity.COUNTRY_OTHER.equals(taxInfoDTO.getTaxCountry())) {
				taxInfoDTOBuilder.setBusinessName(taxInfoDTO.getTaxName());
			} else {
				taxInfoDTOBuilder.setTaxName(taxInfoDTO.getLastName());
			}
		} else {
			taxInfoDTOBuilder.setTaxName(StringUtilities.fullName(taxInfoDTO.getFirstName(), taxInfoDTO.getMiddleName(), taxInfoDTO.getLastName()));
		}
		taxInfoDTO = taxInfoDTOBuilder.build();
	}

	protected void setTaxEntityVerificationStatus() {
		//setting the proper verification status code based on the tax country
		Assert.notNull(taxInfoDTO);
		copyTaxInfoDTO();
		if (AbstractTaxEntity.COUNTRY_USA.equalsIgnoreCase(taxInfoDTO.getTaxCountry())) {
			taxInfoDTOBuilder.setTaxVerificationStatusCode(TaxVerificationStatusType.UNVERIFIED);
		} else if (AbstractTaxEntity.COUNTRY_CANADA.equalsIgnoreCase(taxInfoDTO.getTaxCountry())) {
			taxInfoDTOBuilder.setTaxVerificationStatusCode(TaxVerificationStatusType.VALIDATED);
		} else if (AbstractTaxEntity.COUNTRY_OTHER.equalsIgnoreCase(taxInfoDTO.getTaxCountry())) {
			taxInfoDTOBuilder.setTaxVerificationStatusCode(TaxVerificationStatusType.SIGNED_FORM_W8);
		}
		taxInfoDTO = taxInfoDTOBuilder.build();
	}

	protected void processCreditCardPayment() throws ValidationException, CreditCardErrorException, BeansException {
		if (CollectionUtils.isNotEmpty(errors)) {
			throw new ValidationException("unable to process credit card payment", errors);
		}
		toPaymentDTO();
		paymentResponseDTO = accountRegisterServicePrefundImpl.addFundsToRegisterFromCreditCard(new AutoPopulatingList<>(String.class), new AutoPopulatingList<>(Float.class), user.getId(), paymentDTO, true);
	}

	protected void handleValidationException() throws ValidationException {
		if (exception instanceof ValidationException) {
			throw (ValidationException) exception;
		}
	}

	protected void handleInvalidAclRoleException() throws InvalidAclRoleException {
		if (exception instanceof InvalidAclRoleException) {
			throw (InvalidAclRoleException) exception;
		}
	}

	protected void handleBeansException() throws BeansException {
		if (exception instanceof BeansException) {
			throw (BeansException) exception;
		}
	}

	protected void handleCreditCardErrorException() throws CreditCardErrorException {
		if (exception instanceof CreditCardErrorException) {
			throw (CreditCardErrorException) exception;
		}
	}
}
