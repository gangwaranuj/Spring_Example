package com.workmarket.domains.payments.service;

import ch.lambdaj.function.convert.PropertyExtractor;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.service.option.OptionsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class AccountRegisterAuthorizationServiceImpl implements AccountRegisterAuthorizationService {

	private static final Log logger = LogFactory.getLog(AccountRegisterAuthorizationServiceImpl.class);

	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterServicePrefundImpl;
	@Autowired @Qualifier("accountRegisterServicePaymentTermsImpl")
	private AccountRegisterService accountRegisterServicePaymentTermsImpl;
	@Qualifier("workOptionsService") @Autowired private OptionsService<AbstractWork> workOptionsService;
	@Autowired private WorkService workService;
	@Autowired private WorkResourceService workResourceService;

	@Override
	public WorkAuthorizationResponse authorizeWork(long workId) {
		return authorizeWork((Work) workService.findWork(workId));
	}

	@Override
	public WorkAuthorizationResponse authorizeWork(Work work) {
		Assert.notNull(work);

		// when assignment has offline payment, we skip the account register updates
		if (workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true") ||
			workService.isOfflinePayment(work)) {
			return WorkAuthorizationResponse.SUCCEEDED;
		}

		try {
			if (work.hasPaymentTerms()) {
				return accountRegisterServicePaymentTermsImpl.authorizeWork(work);
			}
			return accountRegisterServicePrefundImpl.authorizeWork(work);

		} catch (InsufficientBudgetException | InsufficientFundsException e) {
			logger.error("error authorizing work " + work.getId(), e);
		}
		return WorkAuthorizationResponse.UNKNOWN;
	}

	@Override
	public void deauthorizeWork(Long workId) {
		Assert.notNull(workId);

		deauthorizeWork((Work) workService.findWork(workId));
	}

	@Override
	public void deauthorizeWork(Work work) {
		Assert.notNull(work);

		try {
			if (work.hasPaymentTerms()) {
				accountRegisterServicePaymentTermsImpl.revertAccountRegisterPendingTransactions(work);
				return;
			}
			accountRegisterServicePrefundImpl.revertAccountRegisterPendingTransactions(work);
		} catch (AccountRegisterConcurrentException e) {
			logger.error("error deauthorizing work " + work.getId(), e);
		}
	}

	@Override
	public WorkAuthorizationResponse verifyFundsForAuthorization(User user, Work work, BigDecimal workTotalCost) {
		Assert.notNull(user);
		Assert.notNull(work);
		Assert.notNull(workTotalCost);

		return verifyFundsForAuthorization(user, workTotalCost, work.hasPaymentTerms());
	}

	@Override
	public WorkAuthorizationResponse verifyFundsForAuthorization(User user, BigDecimal workTotalCost, boolean hasPaymentTerms) {
		Assert.notNull(user);
		Assert.notNull(workTotalCost);

		try {
			if (hasPaymentTerms) {
				return accountRegisterServicePaymentTermsImpl.verifyFundsForAuthorization(workTotalCost, user, null);
			}
			return accountRegisterServicePrefundImpl.verifyFundsForAuthorization(workTotalCost, user, null);

		} catch (Exception e) {
			logger.error("Error verifying Funds For Authorization " + user.getId(), e);
		}
		return WorkAuthorizationResponse.UNKNOWN;
	}

	@Override
	public WorkAuthorizationResponse registerWorkInBundleAuthorization(long workId) {
		Work work = workService.findWork(workId);
		Assert.notNull(work);
		Assert.isTrue(work.isInBundle(), "Work is not in a bundle");

		if (work.hasPaymentTerms()) {
			return accountRegisterServicePaymentTermsImpl.registerWorkInBundleAuthorization(work);
		}
		return accountRegisterServicePrefundImpl.registerWorkInBundleAuthorization(work);
	}

	@Override
	public WorkAuthorizationResponse authorizeContractors(Set<PeopleSearchResult> contractors, Work work, WorkRoutingResponseSummary responseSummary) {
		Assert.notNull(responseSummary);
		Assert.notNull(contractors);

		if (isNotEmpty(contractors)) {
			WorkAuthorizationResponse responseType = authorizeWork(work);
			if (responseType.fail()) {
				responseSummary.addToWorkAuthorizationResponse(responseType, convert(contractors, new PropertyExtractor("userNumber")));
				return responseType;
			}
		}
		return WorkAuthorizationResponse.SUCCEEDED;
	}

	@Override
	public void acceptWork(WorkResource workResource) {
		Assert.notNull(workResource);

		// when assignment has offline payment, we skip the account register updates
		if (workOptionsService.hasOption(workResource.getWork(), WorkOption.MBO_ENABLED, "true") ||
			workService.isOfflinePayment(workResource.getWork())) {
			return;
		}

		if (workResource.getWork().hasPaymentTerms()) {
			accountRegisterServicePaymentTermsImpl.acceptWork(workResource);
			return;
		}
		accountRegisterServicePrefundImpl.acceptWork(workResource);
	}

	@Override
	public void acceptWork(Long workId) {
		Assert.notNull(workId);

		WorkResource workResource = workResourceService.findActiveWorkResource(workId);
		Assert.notNull(workResource);

		acceptWork(workResource);
	}

	@Override
	public void repriceWork(Work work) {
		Assert.notNull(work);

		// when assignment has offline payment, we skip the account register updates
		if (workOptionsService.hasOption(work, WorkOption.MBO_ENABLED, "true") ||
			workService.isOfflinePayment(work)) {
			return;
		}
		if (work.hasPaymentTerms()) {
			accountRegisterServicePaymentTermsImpl.repriceWork(work);
			return;
		}
		accountRegisterServicePrefundImpl.repriceWork(work);
	}

	@Override
	public BigDecimal findRemainingAuthorizedAmountByWorkBundle(long workBundleId) {
		return accountRegisterServicePrefundImpl.findRemainingAuthorizedAmountByWorkBundle(workBundleId);
	}

	@Override
	public WorkCostDTO calculateCostOnSentWork(Work work) {
		Assert.notNull(work);

		if (work.hasPaymentTerms()) {
			return accountRegisterServicePaymentTermsImpl.calculateCostOnSentWork(work);
		}
		return accountRegisterServicePrefundImpl.calculateCostOnSentWork(work);
	}

	@Override
	public void authorizeOnCompleteWork(WorkResource workResource) {
		Assert.notNull(workResource);

		if (workService.isOfflinePayment(workResource.getWork())) {
			accountRegisterServicePrefundImpl.completeOfflinePayment(workResource);
		} else if (workResource.getWork().hasPaymentTerms()) {
			accountRegisterServicePaymentTermsImpl.completeWork(workResource);
		} else {
			accountRegisterServicePrefundImpl.completeWork(workResource);
		}
	}

	@Override
	public void authorizeOnCompleteWork(Long workId, double overridePrice) {
		Assert.notNull(workId);

		final WorkResource workResource = workResourceService.findActiveWorkResource(workId);
		Assert.notNull(workResource);

		workResource.getWork().getPricingStrategy().getFullPricingStrategy().setOverridePrice(BigDecimal.valueOf(overridePrice));

		authorizeOnCompleteWork(workResource);
	}

	@Override
	public void voidWork(Work work) {
		Assert.notNull(work);

		if (work.hasPaymentTerms()) {
			accountRegisterServicePaymentTermsImpl.voidWork(work);
			return;
		}
		accountRegisterServicePrefundImpl.voidWork(work);
	}
}
