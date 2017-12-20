package com.workmarket.domains.payments.service;

import com.google.common.base.Optional;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.GeneralTransaction;
import com.workmarket.domains.model.account.ProjectTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.service.exception.account.InsufficientFundsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class AccountRegisterServicePrefundImpl extends AccountRegisterServiceAbstract implements AccountRegisterService {

	private static final Log logger = LogFactory.getLog(AccountRegisterServicePrefundImpl.class);

	public Long[] acceptWork(WorkResource workResource) throws InsufficientFundsException {
		return acceptWork(workResource, RegisterTransactionType.RESOURCE_COMMITMENT_TO_RECEIVE_PAY, RegisterTransactionType.BUYER_COMMITMENT_TO_PAY);
	}

	@Override
	public boolean isWorkAuthorized(Work work) {
		WorkCostDTO accountRegisterMoniesDTO = calculateCostOnSentWork(work);
		WorkResourceTransaction workResourceTransaction;
		if (work.isWorkBundle()) {
			workResourceTransaction = registerTransactionDAO.findWorkBundleAuthorizationTransaction(work.getId());
			if (workResourceTransaction != null && !RegisterTransactionType.BUYER_COMMITMENT_TO_PAY_WORK_BUNDLE.equals(workResourceTransaction.getRegisterTransactionType().getCode())) {
				return false;
			}
		} else {
			workResourceTransaction = registerTransactionDAO.findWorkResourcePendingCommitmentTransaction(work.getId());
		}
		return workResourceTransaction != null && workResourceTransaction.getAmount().abs().compareTo(accountRegisterMoniesDTO.getTotalBuyerCost()) >= 0;
	}

	@Override
	protected WorkResourceTransaction createAuthorizationTransaction(Work work, WorkCostDTO accountRegisterMoniesDTO, WorkResource authorizeWorkResource, boolean updateSummaries) {
		Assert.notNull(work);
		AccountRegister workRegister = findDefaultRegisterForCompany(work.getCompany());

		WorkResourceTransaction workResourceTransaction = null;
		if (authorizeWorkResource != null) {
			workResourceTransaction = registerTransactionDAO.findWorkResourcePendingCommitmentTransaction(authorizeWorkResource.getWork().getId());
		}

		if (workResourceTransaction != null) {
			// If new commitment has a higher totalBuyerCost, update. Another solution might be to use voidPending... and create new register_transaction.
			if (accountRegisterMoniesDTO.getTotalBuyerCost().compareTo(workResourceTransaction.getAmount().abs()) == 1) {
				RegisterTransactionExecutor transactionExecutor  = registerTransactionExecutableFactory.newInstance(RegisterTransactionExecutor.createBeanName(Boolean.TRUE, RegisterTransactionType.BUYER_COMMITMENT_TO_PAY, work));
				workResourceTransaction.setPendingFlag(Boolean.FALSE);
				workResourceTransaction.setEffectiveDate(Calendar.getInstance());
				transactionExecutor.reverseSummaries(workResourceTransaction);

				if (updateSummaries) {
					verifySufficientBuyerFunds(work, accountRegisterMoniesDTO, false);
				}

				workResourceTransaction = transactionExecutor.execute(work, authorizeWorkResource, workRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate(), updateSummaries);
			}
		} else {
			// calculate charge and see if the user can pay for it
			if (updateSummaries) {
				verifySufficientBuyerFunds(work, accountRegisterMoniesDTO, true);
			}

			RegisterTransactionExecutor transactionExecutor = registerTransactionExecutableFactory.newInstance(RegisterTransactionExecutor.createBeanName(Boolean.TRUE, RegisterTransactionType.BUYER_COMMITMENT_TO_PAY, work));
			workResourceTransaction = transactionExecutor.execute(work, authorizeWorkResource, workRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate(), updateSummaries);

			if (updateSummaries) {
				if (work.hasProject() && work.getProject().isReservedFundsEnabled()) {
					ProjectTransaction projectTransaction = new ProjectTransaction();
					projectTransaction.setProject(work.getProject());
					projectTransaction.setParentTransaction(workResourceTransaction);

					RegisterTransactionExecutor projectRegisterTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.REMOVE_FUNDS_FROM_PROJECT);
					projectRegisterTransactionsAbstract.setPending(Boolean.TRUE);
					projectRegisterTransactionsAbstract.execute(workRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate(), projectTransaction);

				} else {
					GeneralTransaction generalTransaction = new GeneralTransaction();
					generalTransaction.setParentTransaction(workResourceTransaction);

					RegisterTransactionExecutor generalRegisterTransactionsAbstract = registerTransactionExecutableFactory.newInstance(RegisterTransactionType.REMOVE_FUNDS_FROM_GENERAL);
					generalRegisterTransactionsAbstract.setPending(Boolean.TRUE);
					generalRegisterTransactionsAbstract.execute(workRegister, accountRegisterMoniesDTO.getTotalBuyerCost().negate(), generalTransaction);
				}
			}
		}
		return workResourceTransaction;
	}

	protected void verifySufficientBuyerFunds(Work work, WorkCostDTO accountRegisterMoniesDTO, Boolean accept) throws InsufficientFundsException {
		if (companyService.doesCompanyHaveReservedFundsEnabledProject(work.getCompany().getId())) {
			validateProject(work.getCompany(), work.getProject(), accountRegisterMoniesDTO);
		} else {
			validateAvailableCash(work.getCompany(), accountRegisterMoniesDTO);
		}
	}

	@Override
	protected void verifySufficientBuyerFunds(Company company, Project project, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientFundsException {
		if (companyService.doesCompanyHaveReservedFundsEnabledProject(company.getId())) {
			validateProject(company, project, accountRegisterMoniesDTO);
		} else {
			validateAvailableCash(company, accountRegisterMoniesDTO);
		}
	}

	private void validateProject(Company company, Project project, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientFundsException {
		if (project != null && project.isReservedFundsEnabled()) {
			if (accountRegisterMoniesDTO.getTotalBuyerCost().compareTo(project.getReservedFunds()) == Constants.GREATER_THAN) {
				throw new InsufficientFundsException(" Buyer has project budget of:" + project.getReservedFunds().toString()
						+ " while the totalBuyerCost is:"
						+ accountRegisterMoniesDTO.getTotalBuyerCost()
						+ " companyId:" + company.getId());

			}
		} else {
			validateGeneralCash(company, accountRegisterMoniesDTO);
		}
	}

	private void validateAvailableCash(Company company, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientFundsException {
		BigDecimal availableCash = calcSufficientBuyerFundsByCompany(company.getId());
		logger.debug("availableCash:" + availableCash + ", " + accountRegisterMoniesDTO.toString());
		// -1, 0, or 1 as this BigDecimal is numerically less than, equal to, or greater than val.
		if (accountRegisterMoniesDTO.getTotalBuyerCost().compareTo(availableCash) == 1) {
			throw new InsufficientFundsException(" Buyer has availableCash of:" + availableCash.toString()
					+ " while the totalBuyerCost is:"
					+ accountRegisterMoniesDTO.getTotalBuyerCost()
					+ " companyId:" + company.getId());
		}
	}

	private void validateGeneralCash(Company company, WorkCostDTO accountRegisterMoniesDTO) throws InsufficientFundsException {
		BigDecimal generalCash = calculateGeneralCashByCompany(company.getId());
		if (accountRegisterMoniesDTO.getTotalBuyerCost().compareTo(generalCash) == 1) {
			throw new InsufficientFundsException(" Buyer has general cash of:" + generalCash.toString()
					+ " while the totalBuyerCost is:"
					+ accountRegisterMoniesDTO.getTotalBuyerCost()
					+ " companyId:" + company.getId());
		}
	}

	@Override
	public BigDecimal calcSufficientBuyerFundsByCompany(Long companyId) {
		Assert.notNull(companyId);
		Optional<AccountRegisterSummaryFields> fieldsOpt = accountRegisterSummaryFieldsDAO.findAccountRegisterSummaryByCompanyId(companyId);
		return (fieldsOpt.isPresent()) ? fieldsOpt.get().getAvailableCash() : BigDecimal.ZERO;
	}

	@Override
	public Map<Long, Long> payPaymentTerms(List<WorkWorkResourceAccountRegister> workWorkResourceAccountRegisters) throws InsufficientFundsException {
		throw new UnsupportedOperationException("not implemented");
	}
}
