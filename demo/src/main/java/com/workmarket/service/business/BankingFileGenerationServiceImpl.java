package com.workmarket.service.business;

import com.google.common.collect.Lists;

import au.com.bytecode.opencsv.CSVWriter;

import com.workmarket.configuration.Constants;
import com.workmarket.dao.banking.BankingIntegrationGenerationRequestDAO;
import com.workmarket.data.export.adapter.CSVAdapter;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.BankAccountTransactionStatusHistory;
import com.workmarket.domains.model.account.GeneralTransaction;
import com.workmarket.domains.model.account.GlobalCashCardTransactionResponse;
import com.workmarket.domains.model.account.ProjectTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequest;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestStatus;
import com.workmarket.domains.model.banking.BankingIntegrationGenerationRequestType;
import com.workmarket.domains.model.banking.BankingIntegrationRequest;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.dao.GlobalCashCardAccountDAO;
import com.workmarket.domains.payments.dao.RegisterTransactionDAO;
import com.workmarket.factory.BankingIntegrationGenerationRequestHeaderMapper;
import com.workmarket.factory.BankingIntegrationGenerationRequestRowMapper;
import com.workmarket.factory.BankingIntegrationGenerationRequestRowMapperFactory;
import com.workmarket.service.business.accountregister.BankAccountTransactionExecutor;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.accountregister.factory.RegisterTransactionExecutableFactory;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.event.UpdateBankTransactionsStatusEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.jms.BatchMessageType;
import com.workmarket.service.infra.jms.JmsService;
import com.workmarket.service.infra.payment.GCCPaymentAdapterImpl;
import com.workmarket.utility.FileUtilities;
import com.workmarket.vault.services.VaultHelper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;


@Service
public class BankingFileGenerationServiceImpl implements BankingFileGenerationService {

	@Autowired private BankingIntegrationGenerationRequestDAO bankingIntegrationGenerationRequestDAO;
	@Autowired private RegisterTransactionDAO registerTransactionDAO;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private JmsService jmsService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private BankingIntegrationGenerationRequestRowMapperFactory rowMapperFactory;
	@Autowired private RegisterTransactionExecutableFactory registerTransactionExecutableFactory;
	@Autowired private UserService userService;
	@Autowired private GlobalCashCardAccountDAO globalCashCardAccountDAO;
	@Autowired private GCCPaymentAdapterImpl globalCashCardService;
	@Autowired private EventRouter eventRouter;
	@Autowired private VaultHelper vaultHelper;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private PricingService pricingService;
	@Autowired FeatureEvaluator featureEvaluator;

	static final String GCC_SUCCESS = "success";
	public static final String WORLD_LINK_DELIMITER = "#";
	private static final Log logger = LogFactory.getLog(BankingFileGenerationServiceImpl.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#initiateBankFileProcessing(java.lang.String)
	 */
	@Override
	public void initiateBankFileProcessing(Long requestorId, String type, String notes) {
		Assert.notNull(requestorId);
		Assert.hasText(type);

		User user = userService.findUserById(requestorId);
		BankingIntegrationGenerationRequest bankingIntegrationGenerationRequest = new BankingIntegrationGenerationRequest();
		BankingIntegrationGenerationRequestType requestType = new BankingIntegrationGenerationRequestType(type);

		logger.debug(" Requesting Banking Integration Generation Request: " + type);
		bankingIntegrationGenerationRequest.setBankingIntegrationGenerationRequestType(requestType);
		bankingIntegrationGenerationRequest.setBankingIntegrationGenerationRequestStatus(new BankingIntegrationGenerationRequestStatus(BankingIntegrationGenerationRequestStatus.SUBMITTED));
		bankingIntegrationGenerationRequest.setRequestor(user);
		bankingIntegrationGenerationRequest.setRequestDate(Calendar.getInstance());
		bankingIntegrationGenerationRequest.setNotes(notes);
		bankingIntegrationGenerationRequestDAO.saveOrUpdate(bankingIntegrationGenerationRequest);

		BatchMessageType batchType = BatchMessageType.findByCode(requestType.getCode());
		Assert.notNull(batchType);
		jmsService.sendBatchMessage(batchType);
	}

	@Override
	public void cancelBankingIntegrationGenerationRequest(long bankingIntegrationRequestId) {
		BankingIntegrationGenerationRequest request = bankingIntegrationGenerationRequestDAO.get(bankingIntegrationRequestId);
		if (request != null && request.getBankingIntegrationGenerationRequestStatus().isSubmitted()) {
			request.setDeleted(true);
		}
	}

	@Override
	public BankingIntegrationGenerationRequest loadTransactionsAttachedToBankingIntegration(long bankingIntegrationRequestId) {
		return bankingIntegrationGenerationRequestDAO.get(bankingIntegrationRequestId, true);
	}

	@Override
	public BankingIntegrationGenerationRequest findBankingIntegrationGenerationRequest(long bankingIntegrationRequestId) {
		return bankingIntegrationGenerationRequestDAO.get(bankingIntegrationRequestId);
	}

	@Override
	public void addBatchNumberToBankingFile(Long requestId, String batchNumber) {
		Assert.notNull(requestId);
		Assert.hasText(batchNumber);
		BankingIntegrationGenerationRequest request = bankingIntegrationGenerationRequestDAO.get(requestId);
		if (request != null) {
			request.setBatchNumber(batchNumber);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#markBankingTransactionNonPending(java.lang.Long)
	 */
	@Override
	public void markBankAccountTransactionNonPending(Long transactionId) {
		Assert.notNull(transactionId);
		BankAccountTransaction tx = (BankAccountTransaction) registerTransactionDAO.get(transactionId);

		pricingService.lockAccountRegisterForWritingHack(tx.getBankAccount().getCompany().getId());

		BankAccountTransactionExecutor transactionExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.BANK_ACCOUNT_TRANSACTION);
		transactionExecutor.executeAddFundsApprovalAccountRegister(tx);

		tx.setPendingFlag(Boolean.FALSE);
		tx.setEffectiveDate(Calendar.getInstance());
		tx.setApprovedByBankDate(Calendar.getInstance());

		List<RegisterTransaction> childTxs = copyStatusToChildren(transactionId);
		List<RegisterTransaction> projectChildTxs = copyStatusToProjectChildren(transactionId);
		List<RegisterTransaction> generalChildTxs = copyStatusToGeneralChildren(transactionId);
		for (RegisterTransaction childTx : childTxs) {
			if (childTx instanceof BankAccountTransaction) {
				BankAccountTransactionExecutor executor = registerTransactionExecutableFactory.newInstance(childTx.getRegisterTransactionType().getCode());
				executor.executeAddFundsApprovalAccountRegister((BankAccountTransaction) childTx);
			}
		}
		for (RegisterTransaction childTx : projectChildTxs) {
			if (childTx instanceof ProjectTransaction) {
				ProjectTransaction projectTransaction = (ProjectTransaction) childTx;
				projectTransaction.setPendingFlag(Boolean.FALSE);
				AccountRegister accountRegister = projectTransaction.getAccountRegister();
				BigDecimal amount = projectTransaction.getAmount();

				RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.ADD_FUNDS_TO_PROJECT);
				registerTransactionsAbstract.setPending(Boolean.FALSE);
				registerTransactionsAbstract.execute(accountRegister, amount, projectTransaction);
			}
		}

		for (RegisterTransaction childTx : generalChildTxs) {
			if (childTx instanceof GeneralTransaction) {
				GeneralTransaction generalTransaction = (GeneralTransaction) childTx;
				generalTransaction.setPendingFlag(Boolean.FALSE);
				AccountRegister accountRegister = generalTransaction.getAccountRegister();
				BigDecimal amount = generalTransaction.getAmount();

				RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.ADD_FUNDS_TO_GENERAL);
				registerTransactionsAbstract.setPending(Boolean.FALSE);
				registerTransactionsAbstract.execute(accountRegister, amount, generalTransaction);
			}
		}

		// Add money to general cash for those bank transaction in progress before release general cash code
		if (projectChildTxs.size() == 0 && generalChildTxs.size() == 0) {
			GeneralTransaction generalTransaction = new GeneralTransaction();
			generalTransaction.setPendingFlag(Boolean.FALSE);

			RegisterTransactionExecutor registerTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.ADD_FUNDS_TO_GENERAL);
			registerTransactionsAbstract.setPending(Boolean.FALSE);
			registerTransactionsAbstract.execute(tx.getAccountRegister(), tx.getAmount(), generalTransaction);
		}

		userNotificationService.onCreditTransaction(tx);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.domains.payments.service.AccountRegisterService#findBankingIntegrationGenerationRequests(java.lang.String)
	 */
	@Override
	public List<BankingIntegrationGenerationRequest> findBankingIntegrationGenerationRequests(String type) {
		Assert.hasText(type);
		return bankingIntegrationGenerationRequestDAO.findByType(type);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.workmarket.service.business.AccountRegisterService#findBankingIntegrationGenerationRequests(java.lang.String)
	 */
	@Override
	public boolean bankFileGenerationInProcess(String type) {
		Assert.hasText(type);
		List<BankingIntegrationGenerationRequest> results = bankingIntegrationGenerationRequestDAO.findByTypeAndStatus(type, BankingIntegrationGenerationRequestStatus.SUBMITTED);
		return isNotEmpty(results);
	}

	@Override
	public void processPendingOutbound(Country country) throws Exception {
		String bankingIntegrationRequestType = BankingIntegrationGenerationRequestType.NON_USA_OUTBOUND;

		if (country != null && country.getId().equals(Country.USA_COUNTRY.getId())) {
			bankingIntegrationRequestType = BankingIntegrationGenerationRequestType.OUTBOUND;
		}

		processTransactions(
				new BankingIntegrationRequest(bankingIntegrationRequestType,
				RegisterTransactionType.REMOVE_FUNDS)
				.setAutoApproved(true)
				.setCountry(country)
		);
	}

	@Override
	public void processPendingInbound() throws Exception {
		processTransactions(
				new BankingIntegrationRequest(BankingIntegrationGenerationRequestType.INBOUND,
				RegisterTransactionType.ADD_FUNDS)
				.setAutoApproved(false)
		);
	}

	@Override
	public void processPendingPayPal() throws Exception {
		//TODO: Per PayPal Mass Payment documentation, transactions are limited to 5000/batch
		processTransactions(
				new BankingIntegrationRequest(BankingIntegrationGenerationRequestType.PAYPAL,
				RegisterTransactionType.REMOVE_FUNDS_PAYPAL).setAutoApproved(true));
	}

	@Override
	public void processPendingAch() throws Exception {
		processTransactions(
				new BankingIntegrationRequest(BankingIntegrationGenerationRequestType.ACHVERIFY, RegisterTransactionType.ACH_VERIFY)
				.setAutoApproved(true));
	}

	@Override
	public void processPendingGCC() throws Exception {
		processTransactions(
			new BankingIntegrationRequest(BankingIntegrationGenerationRequestType.GCC, RegisterTransactionType.REMOVE_FUNDS_GCC)
				.setAutoApproved(true));
	}

	private void processTransactions(BankingIntegrationRequest request) throws Exception {
		Assert.notNull(request);
		List<BankingIntegrationGenerationRequest> requests = bankingIntegrationGenerationRequestDAO.findByTypeAndStatus(request.getBankingIntegrationRequestType(), BankingIntegrationGenerationRequestStatus.SUBMITTED);

		logger.debug(String.format("Attempting to process integration requests [%s] => %d requests", request.getBankingIntegrationRequestType(), requests.size()));

		for (BankingIntegrationGenerationRequest integrationRequest : requests) {
			List<BankAccountTransaction> transactions = registerTransactionDAO.findBankAccountTransactions(request.getTransactionType(), BankAccountTransactionStatus.APPROVED, request.getCountry());
			logger.debug(String.format("Attempting to process transactions => %d transactions", transactions.size()));
			processTransactionsForType(integrationRequest, transactions, request.isAutoApproved());
		}
	}

	void processTransactionsForType(BankingIntegrationGenerationRequest request, List<BankAccountTransaction> txs, boolean setAutoApprovedByBank) throws Exception {
		Assert.notNull(request);
		Assert.notNull(txs);
		Calendar now = Calendar.getInstance();

		BankingIntegrationGenerationRequestType bankingIntegrationGenerationRequestType = request.getBankingIntegrationGenerationRequestType();
		Assert.notNull(bankingIntegrationGenerationRequestType);

		BankingIntegrationGenerationRequestHeaderMapper headerMapper = rowMapperFactory.getHeaderMapper(bankingIntegrationGenerationRequestType);
		BankingIntegrationGenerationRequestRowMapper rowMapper = rowMapperFactory.getRowMapper(bankingIntegrationGenerationRequestType);

		final List<String[]> rows = Lists.newArrayList();
		final List<String[]> headers = Lists.newArrayList();

		if (ArrayUtils.isNotEmpty(headerMapper.mapHeader())) {
			headers.add(headerMapper.mapHeader());
		}

		boolean hasFeature = featureEvaluator.hasFeature(authenticationService.getCurrentUserCompanyId(), "vaultRead");
		for (BankAccountTransaction tx : txs) {
			String bankAccountNumber = "";

			if (setAutoApprovedByBank) {
				tx.setApprovedByBankDate(now);
			}

			if (hasFeature) {
				bankAccountNumber = vaultHelper.get(tx.getBankAccount(), "accountNumber", "").getValue();
			} else if (tx.getBankAccount() instanceof BankAccount){
				bankAccountNumber = ((BankAccount) tx.getBankAccount()).getAccountNumberSanitized();
			}

			updateBankTransactionStatus(Constants.BACK_END_USER_ID, tx.getId(), "File Batch Process", BankAccountTransactionStatus.PROCESSED);
			String[] row = rowMapper.mapRow(tx, bankAccountNumber);
			if (ArrayUtils.isNotEmpty(row)) {
				rows.add(rowMapper.mapRow(tx, bankAccountNumber));
			}
		}

		request.setBankingIntegrationGenerationRequestStatus(new BankingIntegrationGenerationRequestStatus(BankingIntegrationGenerationRequestStatus.COMPLETE));
		request.setBankAccountTransactions(txs);

		if (isNotEmpty(rows)) {
			String filename = createFileFromRows(bankingIntegrationGenerationRequestType, headers, rows);

			AssetDTO dto = new AssetDTO();
			dto.setName("Confirmation");
			dto.setDescription("Description");
			dto.setSourceFilePath(filename);
			dto.setMimeType(MimeType.TEXT_CSV.toString());
			assetManagementService.storeAssetForBankingFile(dto, request);
		}
	}

	private String createFileFromRows(BankingIntegrationGenerationRequestType bankingIntegrationGenerationRequestType, final List<String[]> headers, final List<String[]> rows) throws Exception {
		Assert.notNull(bankingIntegrationGenerationRequestType);
		String filename = FileUtilities.generateTemporaryFileName();
		Writer writer = new FileWriter(filename);
		CSVAdapter adapter;

		switch (bankingIntegrationGenerationRequestType.getCode()) {
			case BankingIntegrationGenerationRequestType.NON_USA_OUTBOUND:
				adapter = new CSVAdapter(WORLD_LINK_DELIMITER.charAt(0), CSVWriter.NO_QUOTE_CHARACTER);
				break;
			default:
				adapter = new CSVAdapter();
		}

		//Write the headers
		if (ArrayUtils.isNotEmpty(headers.toArray())) {
			adapter.exportWithoutCloseAndUnquoted(writer, headers);
		}

		//Write the data
		adapter.export(writer, rows);

		return filename;
	}

	@Override
	public void updateBankTransactionStatus(Long userId, Long transactionId, String notes, String statusCode) throws Exception {
		Assert.notNull(userId);
		Assert.notNull(transactionId);

		logger.info("Updating Bank Account Transaction Status...." + statusCode);
		BankAccountTransaction transaction = (BankAccountTransaction) registerTransactionDAO.get(transactionId);

		pricingService.lockAccountRegisterForWritingHack(transaction.getBankAccount().getCompany().getId());

		final String previousStatusCode = transaction.getBankAccountTransactionStatus() != null ? transaction.getBankAccountTransactionStatus().getCode() : "";
		if (previousStatusCode.equals(statusCode) ||
				(previousStatusCode.equals(BankAccountTransactionStatus.APPROVED) && statusCode.equals(BankAccountTransactionStatus.REJECTED)) ||
				(previousStatusCode.equals(BankAccountTransactionStatus.REJECTED) && statusCode.equals(BankAccountTransactionStatus.APPROVED))) {
			logger.info(String.format("Previous Bank Account Transaction Status code [%s], adding to history but skipping updates....", previousStatusCode));
			final BankAccountTransactionStatusHistory history = new BankAccountTransactionStatusHistory(
					transaction, new BankAccountTransactionStatus(statusCode),
					Calendar.getInstance(), "Ignore", userService.findUserById(userId));
			transaction.addHistory(history);
			return;
		}

		transaction.setBankAccountTransactionStatus(new BankAccountTransactionStatus(statusCode));
		String txType = transaction.getRegisterTransactionType().getCode();

		List<RegisterTransaction> childrenTxs = copyStatusToChildren(transactionId);

		if (BankAccountTransactionStatus.APPROVED.equals(statusCode) && RegisterTransactionType.REMOVE_FUNDS_GCC.equals(txType)) {
			BankAccountTransaction processedGCCTransaction = processRemoveFundsGCC(transaction);

			if(processedGCCTransaction.getBankAccountTransactionStatus() != null &&
					BankAccountTransactionStatus.REJECTED.equals(processedGCCTransaction.getBankAccountTransactionStatus().getCode())){
				statusCode =   BankAccountTransactionStatus.REJECTED;
			}
		}

		BankAccountTransactionStatusHistory history = new BankAccountTransactionStatusHistory(transaction, new BankAccountTransactionStatus(statusCode),
			Calendar.getInstance(), notes, userService.findUserById(userId));
		transaction.addHistory(history);

		if (BankAccountTransactionStatus.REJECTED.equals(statusCode) && RegisterTransactionType.REMOVE_FUNDS_TRANSACTION_TYPE_CODES.contains(txType)) {
			BankAccountTransactionExecutor executor = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.BANK_ACCOUNT_TRANSACTION);
			executor.reverseWithdrawableSummaries(transaction);

			for (RegisterTransaction childTx : childrenTxs) {
				if (childTx instanceof BankAccountTransaction) {
					BankAccountTransactionExecutor childExecutor = registerTransactionExecutableFactory.newInstance(childTx.getRegisterTransactionType().getCode());
					childExecutor.reverseWithdrawableSummaries(childTx);
				}
			}
		}
	}

	protected BankAccountTransaction processRemoveFundsGCC(BankAccountTransaction transaction) throws Exception {
		Assert.notNull(transaction);
		//wm returns negative amount need to make it to be positive
		BigDecimal withdrawalAmount = transaction.getAmount().negate();
		String keyField = globalCashCardAccountDAO.findAccountNumber(transaction.getBankAccount().getId());
		GlobalCashCardTransactionResponse response = globalCashCardService.loadCard(keyField, withdrawalAmount);
		logger.info(String.format("[GCC_loadCard] wm account %s withdrawing from wm amount %s", keyField, withdrawalAmount));

		if (!GCC_SUCCESS.equals(response.getStatus())) {

			transaction.setBankAccountTransactionStatus(new BankAccountTransactionStatus(BankAccountTransactionStatus.REJECTED));
			logger.error(String.format("[GCC_loadCard] failed response: \n %s", response.getRawResponse()));
		}

		return transaction;
	}

	@Override
	public void updateBankTransactionsStatusAsync(Long userId, List<Long> transactionIds, String notes, String statusCode) {
		logger.info("Updating Bank Account Transaction Status...." + statusCode);
		eventRouter.sendEvent(new UpdateBankTransactionsStatusEvent(userId, transactionIds, notes, statusCode));
	}

	@Override
	public void markBankAccountTransactionProcessing(List<Long> transactionIds) {
		Assert.notNull(transactionIds);
		registerTransactionDAO.markBankAccountTransactionProcessing(transactionIds);
	}

	@Override
	public List<RegisterTransaction> copyStatusToChildren(Long txId) {
		Assert.notNull(txId);
		RegisterTransaction transaction = registerTransactionDAO.get(txId);
		Assert.notNull(transaction);

		List<RegisterTransaction> childTxs = registerTransactionDAO.findChildTransactions(txId);
		for (RegisterTransaction childTx : childTxs) {
			childTx.copyStatus(transaction);
		}
		return childTxs;
	}

	@Override
	public List<RegisterTransaction> copyStatusToProjectChildren(Long txId) {
		Assert.notNull(txId);
		RegisterTransaction transaction = registerTransactionDAO.get(txId);
		Assert.notNull(transaction);

		List<RegisterTransaction> childTxs = registerTransactionDAO.findProjectChildTransactions(txId);
		for (RegisterTransaction childTx : childTxs) {
			childTx.copyStatus(transaction);
		}
		return childTxs;
	}

	List<RegisterTransaction> copyStatusToGeneralChildren(Long txId) {
		Assert.notNull(txId);
		RegisterTransaction transaction = registerTransactionDAO.get(txId);
		Assert.notNull(transaction);

		List<RegisterTransaction> childTxs = registerTransactionDAO.findGeneralChildTransactions(txId);
		for (RegisterTransaction childTx : childTxs) {
			childTx.copyStatus(transaction);
		}
		return childTxs;
	}


}
