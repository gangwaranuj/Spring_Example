package com.workmarket.domains.payments.service;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService;
import com.workmarket.domains.banking.util.BankRoutingUtil;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.GlobalCashCardTransactionResponse;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.model.banking.GlobalCashCardAccount;
import com.workmarket.domains.model.banking.PayPalAccount;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.domains.payments.dao.GlobalCashCardAccountDAO;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.integration.autotask.util.StringUtil;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.exception.AccountConfirmationAttemptsExceededException;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.infra.payment.GCCPaymentAdapterImpl;
import com.workmarket.utility.DateUtilities;
import com.workmarket.vault.services.VaultHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BankingServiceImpl implements BankingService {

	@Autowired private UserDAO userDAO;
	@Autowired private BankAccountDAO bankAccountDAO;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterServicePrefundImpl;
	@Autowired private GCCPaymentAdapterImpl globalCashCardService;
	@Autowired private GlobalCashCardAccountDAO globalCashCardAccountDAO;
	@Autowired private EmailTemplateFactory emailTemplateFactory;
	@Autowired private NotificationService notificationService;
	@Autowired private HydratorCache hydratorCache;
	@Autowired private TaxService taxService;
	@Autowired protected FeatureEvaluator featureEvaluator;
	@Autowired private VaultHelper vaultHelper;
	@Autowired ExtendedUserDetailsOptionsService extendedUserDetailsService;

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	/* NOTE: if you will want to use auto withdrawal for any account just remove the BankAccount type */
	private final static String ACCOUNT_WITH_AMOUNT_TO_WITHDRAW = "select user.id as user_id,ar.company_id as company_id,ba.id as bank_account_id,withdrawable_cash " +
			"from account_register ar " +
			"inner join user on ar.company_id = user.company_id " +
			"inner join bank_account ba on ba.company_id = ar.company_id " +
			"and confirmed_flag = 'Y' and ba.type = '" + BankAccount.GCC + "' and auto_withdraw = 'Y' " +
			"where withdrawable_cash > 0 ";


	private static final Log logger = LogFactory.getLog(BankingServiceImpl.class);

	@Override
	public List<Long> getAllIds() {
		return bankAccountDAO.getAllIds();
	}

	@Override
	public AbstractBankAccount saveBankAccount(Long userId, BankAccountDTO dto) {
		if (StringUtils.equalsIgnoreCase(dto.getType(), AbstractBankAccount.ACH)) {
			return saveACHAccount(userId, dto);
		}

		if (StringUtils.equalsIgnoreCase(dto.getType(), AbstractBankAccount.PAYPAL)) {
			return savePayPalAccount(userId, dto);
		}

		if (StringUtils.equalsIgnoreCase(dto.getType(), AbstractBankAccount.GCC)) {
			return saveGCCAccount(userId, dto);
		}

		throw new UnsupportedOperationException(String.format("Unsupported bank account type: %s", dto.getType()));
	}

	private GlobalCashCardAccount saveGCCAccount(Long userId, BankAccountDTO dto) {
		User user = userDAO.get(userId);
		GlobalCashCardTransactionResponse gccResponse;

		try {

			String keyField = UUID.randomUUID().toString().replaceAll("-", "");

			gccResponse = globalCashCardService.addSignatureCard(keyField, dto);

			if ("success".equals(gccResponse.getStatus())) {
				GlobalCashCardAccount gccAccount = new GlobalCashCardAccount();
				String nameOnAccount = dto.getNameOnAccount();

				if (StringUtil.isNullOrEmpty(nameOnAccount)) {
					nameOnAccount = String.format("%s %s", dto.getFirstName(), dto.getLastName());
				}

				dto.setCcLastFour(StringUtils.substring(gccResponse.getCardnumber(), -4));
				gccAccount.setBankAccountType(new BankAccountType(dto.getBankAccountTypeCode()));
				gccAccount.setNameOnAccount(nameOnAccount);
				gccAccount.setAccountNumber(keyField);

				gccAccount.setBankName(Constants.GLOBAL_CASH_CARD_BANK_NAME);

				gccAccount.setActiveFlag(Boolean.TRUE);
				gccAccount.setConfirmedFlag(Boolean.FALSE);
				gccAccount.setCompany(user.getCompany());
				gccAccount.setCountry(Country.valueOf(dto.getCountry()));
				gccAccount.setAutoWithdraw(Boolean.FALSE);

				bankAccountDAO.saveOrUpdate(gccAccount);

				EmailTemplate template = emailTemplateFactory.buildGlobalCashCardCreatedTemplate(user.getId());
				notificationService.sendNotification(template);


				return gccAccount;
			} else {
				logger.debug(String.format("GCC Add card failed with response: %s", gccResponse.getDescription()));
				throw new RuntimeException(globalCashCardService.getHumanizedError(gccResponse.getResponsecode()));
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	private BankAccount saveACHAccount(Long userId, BankAccountDTO dto) {
		User user = userDAO.get(userId);

		BankAccount bankAccount = new BankAccount();
		BeanUtils.copyProperties(dto, bankAccount);
		bankAccount.setBankAccountType(new BankAccountType(dto.getBankAccountTypeCode()));
		bankAccount.setCompany(user.getCompany());
		bankAccount.setConfirmedFlag(Boolean.FALSE);
		bankAccount.setActiveFlag(Boolean.TRUE);

		final Country dtoCountry = Country.valueOf(dto.getCountry());
		if (Country.CANADA_COUNTRY.getId().equals(dtoCountry.getId())) {
			bankAccount.setRoutingNumber(BankRoutingUtil.buildRoutingNumber(dto.getBranchNumber(), dto.getInstitutionNumber()));
		}

		BankRouting bankRouting = invariantDataService.getBankRouting(bankAccount.getRoutingNumber(), dtoCountry.getId());
		Assert.notNull(bankRouting);
		// verify the bank country with the user's tax info - they must be the same
		ExtendedUserDetails userDetails = (ExtendedUserDetails)extendedUserDetailsService.loadUserByEmail(user.getEmail(), ExtendedUserDetailsOptionsService.ALL_OPTIONS);
		if (userDetails.isSeller()) {
			AbstractTaxEntity taxEntity = taxService.findActiveTaxEntity(userId);
			Assert.state(taxEntity != null);
			Assert.state(StringUtils.equals(taxEntity.getIsoCountry().getId(), bankRouting.getCountry().getId()));
		}
		else {
			// for buyers the bank account country has to be US
			Assert.state(StringUtils.equals(Country.USA_COUNTRY.getId(), bankRouting.getCountry().getId()));
		}

		bankAccount.setCountry(bankRouting.getCountry());
		bankAccountDAO.saveOrUpdate(bankAccount);

		// if a USA account then we send a verification deposit, otherwise
		if (Country.USA_COUNTRY.getId().equals(dtoCountry.getId())) {
			logger.debug("Sending verification deposit for US bank account");
			accountRegisterServicePrefundImpl.createACHVerificationTransactions(userId, bankAccount);
		}
		else {
			logger.debug("Non-US bank account added, no verification being sent");
			// override confirmed since we won't send verification deposit
			bankAccount.setConfirmedFlag(Boolean.TRUE);
		}
		return bankAccount;
	}

	private PayPalAccount savePayPalAccount(Long userId, BankAccountDTO dto) {
		Assert.state(!hasPayPalAccount(userId), "Unable to add a new PayPal account, A PayPal account already already exists.");

		final User user = userDAO.get(userId);
		final PayPalAccount payPalAccount = new PayPalAccount();
		final Country country = Country.valueOf(dto.getCountryCode());
		final BankAccountType accountType = new BankAccountType(BankAccountType.PAY_PAL);

		Assert.notNull(country);

		BeanUtils.copyProperties(dto, payPalAccount);

		payPalAccount.setBankAccountType(accountType);
		payPalAccount.setCompany(user.getCompany());
		payPalAccount.setCountry(country);

		logger.info(String.format("New PayPal account added in [%s]", country.getName()));

		bankAccountDAO.saveOrUpdate(payPalAccount);
		return payPalAccount;
	}

	@Override
	public boolean confirmBankAccount(Long bankAccountId, int amount1, int amount2, Long companyId) throws Exception  {

		boolean amount1Matches = false;
		boolean amount2Matches = false;

		Assert.notNull(bankAccountId);
		Assert.notNull(companyId);

		BankAccount bankAccount = (BankAccount) bankAccountDAO.get(bankAccountId);
		if (bankAccount == null) {
			return false;
		}
		if (!companyId.equals(bankAccount.getCompany().getId())) {
			return false;
		}

		if (bankAccount.getConfirmationAttempts() > 3) {
			throw new AccountConfirmationAttemptsExceededException();
		} else {
			bankAccount.setConfirmationAttempts(bankAccount.getConfirmationAttempts() + 1);
		}

		List<BankAccountTransaction> bankAccountTransactions = accountRegisterServicePrefundImpl.findACHVerificationTransactions(bankAccountId);

		for (BankAccountTransaction accountTransaction : bankAccountTransactions) {
			int amountToCompare = accountTransaction.getAmount().multiply(new BigDecimal(100)).intValue();

			if (amountToCompare == amount1) {
				amount1Matches = true;
			}

			if (amountToCompare == amount2) {
				amount2Matches = true;
			}
		}

		if (amount1Matches && amount2Matches) {
			bankAccount.setConfirmedFlag(Boolean.TRUE);
			bankAccount.setConfirmedOn(DateUtilities.getCalendarNow());
			//Once they have a bank account and if they are a buyer
			if (!bankAccount.getCompany().isResourceAccount()) {
				accountRegisterServicePrefundImpl.updateApLimit(companyId, Constants.DEFAULT_ACCOUNTS_PAYABLE_LIMIT);
			}
			return true;
		}
		hydratorCache.updateCompanyCache(companyId);
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractBankAccount> T confirmGCCAccount(Long id) {
		T bankAccount = (T) bankAccountDAO.get(id);
		bankAccount.setConfirmedFlag(Boolean.TRUE);
		((GlobalCashCardAccount)bankAccount).setConfirmedOn(DateUtilities.getCalendarNow());
		bankAccountDAO.saveOrUpdate(bankAccount);
		return bankAccount;
	}

	@Override
	public boolean updateAutoWithdrawSettings(Long id, Long companyId, Boolean autoWithdraw) {
		AbstractBankAccount bankAccount = bankAccountDAO.get(id);

		if (bankAccount == null) {
			throw new RuntimeException(String.format("account: %d was not found", id));
		}

		if (!companyId.equals(bankAccount.getCompany().getId())) {
			throw new RuntimeException(String.format("account: %d does not belong to the user: %s", id, companyId));

		}

		bankAccount.setAutoWithdraw(autoWithdraw);
		bankAccountDAO.saveOrUpdate(bankAccount);
		return Boolean.TRUE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractBankAccount> T deactivateBankAccount(Long bankAccountId, Long companyId)  {
		Assert.notNull(bankAccountId);
		Assert.notNull(companyId);
		T bankAccount = (T)bankAccountDAO.get(bankAccountId);
		if (bankAccount == null) {
			throw new EntityNotFoundException(String.format("account: %d was not found", bankAccountId));
		}
		Assert.isTrue(companyId.equals(bankAccount.getCompany().getId()));

		bankAccount.setActiveFlag(Boolean.FALSE);
		return bankAccount;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractBankAccount> T findBankAccount(Long bankAccountId) {
		Assert.notNull(bankAccountId);
		return (T)bankAccountDAO.get(bankAccountId);
	}

	@Override
	public BankAccountPagination findBankAccounts(Long userId, BankAccountPagination pagination) {
		User user = userDAO.get(userId);
		return bankAccountDAO.find(user.getCompany().getId(), pagination);
	}

	@Override
	public List<? extends AbstractBankAccount> findBankAccounts(Long userId)  {
		BankAccountPagination pagination = new BankAccountPagination(true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		return findBankAccounts(userId, pagination).getResults();
	}

	@Override
	public List<? extends AbstractBankAccount> findBankAccountsByCompany(Long companyId) {
		BankAccountPagination pagination = new BankAccountPagination(true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		return bankAccountDAO.find(companyId, pagination).getResults();
	}

	@Override
	public List<? extends AbstractBankAccount> findConfirmedBankAccounts(Long userId) {
		BankAccountPagination pagination = new BankAccountPagination(true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.CONFIRMED, true);
		return findBankAccounts(userId, pagination).getResults();
	}

	@Override
	public List<BankAccount> getAllBankAccountsFrom(final Calendar fromCreatedOnDate) {
		return bankAccountDAO.getAllBankAccountsFrom(fromCreatedOnDate);
	}

	@Override
	public List<BankAccount> getAllBankAccountsFromModifiedOn(final Calendar fromModifiedOnDate) {
		return bankAccountDAO.getAllBankAccountsFromModifiedDate(fromModifiedOnDate);
	}

	@Override
	public void saveOrUpdate(final BankAccount account) {
		bankAccountDAO.saveOrUpdate(account);
	}

	@Override
	public BankAccountPagination findAllUnConfirmedGccAccounts(BankAccountPagination pagination) {
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.CONFIRMED, false);
		return globalCashCardAccountDAO.find(pagination);
	}

	@Override
	public List<? extends AbstractBankAccount> findACHBankAccounts(Long userId) {
		BankAccountPagination pagination = new BankAccountPagination(true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.TYPE, AbstractBankAccount.ACH);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.COUNTRY, Country.USA);
		return findBankAccounts(userId, pagination).getResults();
	}

	@Override
	public List<? extends AbstractBankAccount> findConfirmedACHBankAccounts(Long userId) {
		BankAccountPagination pagination = new BankAccountPagination(true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.CONFIRMED, true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.TYPE, AbstractBankAccount.ACH);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.COUNTRY, Country.USA);
		return findBankAccounts(userId, pagination).getResults();
	}

	@Override
	public List<String> getUnobfuscatedAccountNumbers(final List<? extends AbstractBankAccount> accounts) {
		ImmutableList.Builder<String> unobfuscatedAccountNumbers = ImmutableList.builder();

		if (CollectionUtils.isEmpty(accounts)) {
			return unobfuscatedAccountNumbers.build();
		}

		for (AbstractBankAccount account : accounts) {
			if (account instanceof BankAccount) {
				final String valueFromVault = vaultHelper.get(account, BankAccount.VAULTED_FIELD_ACCOUNT_NUMBER, "").getValue();
				if (StringUtils.isEmpty(valueFromVault)) {
					unobfuscatedAccountNumbers.add(((BankAccount) account).getAccountNumber());
				} else {
					unobfuscatedAccountNumbers.add(valueFromVault);
				}
			}
		}

		return unobfuscatedAccountNumbers.build();
	}

	@Override
	public boolean hasPayPalAccount(Long userId) {
		BankAccountPagination pagination = new BankAccountPagination();
		pagination.setStartRow(0);
		pagination.setResultsLimit(0);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.TYPE, AbstractBankAccount.PAYPAL);
		pagination = findBankAccounts(userId, pagination);
		return pagination.getRowCount() > 0;
	}

	@Override
	public boolean hasGCCAccount(Long userId) {
		BankAccountPagination pagination = new BankAccountPagination();
		pagination.setStartRow(0);
		pagination.setResultsLimit(0);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.TYPE, AbstractBankAccount.GCC);
		pagination = findBankAccounts(userId, pagination);
		return pagination.getRowCount() > 0;
	}

	@Override
	public List<Map<String, Object>> findFieldsForAutoWithdrawalAccounts() {
		List<Map<String, Object>> accountsWithWithdrawals = jdbcTemplate.queryForList(ACCOUNT_WITH_AMOUNT_TO_WITHDRAW, new MapSqlParameterSource());

		logger.info(String.format("Found %d records that can be used for withdrawal",accountsWithWithdrawals.size()));
		return accountsWithWithdrawals;
	}

	@Override
	public BankAccountPagination findAllActiveGlobalCashCardAccounts(BankAccountPagination pagination) {
		pagination.addFilter(BankAccountPagination.FILTER_KEYS.ACTIVE, true);
		return globalCashCardAccountDAO.find(pagination);
	}
}
