package com.workmarket.dao.account;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.AccountingCreditMemoSummaryDetail;
import com.workmarket.domains.model.account.AccountingEndOfYearTaxSummary;
import com.workmarket.domains.model.account.AccountingSummaryDetail;
import com.workmarket.domains.model.account.FastFundsReceivableSummaryDetail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public interface AccountingSummaryDetailDAO extends DAOInterface<AccountingSummaryDetail>{

	List<AccountingSummaryDetail> getMoneyOutSubscriptionSoftwareFeesDetail(Calendar start, Calendar end, boolean companyHasVORServiceType);

	List<AccountingSummaryDetail> getMoneyOutSubscriptionVORFeesDetail(Calendar start, Calendar end);

	List<FastFundsReceivableSummaryDetail> getFastFundsReceivableSummaryDetails(Calendar previousRequestDate, Calendar requestDate);

	List<AccountingSummaryDetail> getAccItemRevSubVorSw(Calendar previousRequestDate, Calendar requestDate);

	List<AccountingSummaryDetail> getAccItemRevSubVorVor(Calendar previousRequestDate, Calendar requestDate);

	List<AccountingSummaryDetail> getAccItemRevSubNVor(Calendar previousRequestDate, Calendar requestDate);

	List<AccountingEndOfYearTaxSummary> getEndOfYearTaxReport(Calendar start, Calendar end);

	List<AccountingCreditMemoSummaryDetail> getCreditMemoTransactions(
		Calendar previousRequestDate, Calendar requestDate, List<Integer> creditMemoTypeIds, boolean filterByPaid,
		boolean paid, boolean filterByVOR, boolean isVOR);

	List<AccountingSummaryDetail> getOfflinePaymentsDetail(Calendar start, Calendar end, boolean subscription, boolean vor);
}
