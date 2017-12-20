<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fmr" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Summary Detail">

<c:import url="/breadcrumb">
	<c:param name="pageId" value="adminAccountingSummary" />
	<c:param name="admin" value="true" />
</c:import>

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content accounting-summary">
	<div class="summary-title">
		<h1>
			From <fmt:formatDate value="${summary.previousRequestDate.time}" type="both" dateStyle="short" timeStyle="medium" timeZone="${currentUser.timeZoneId}" /> to <fmt:formatDate value="${summary.requestDate.time}" type="both" dateStyle="short" timeStyle="medium" timeZone="${currentUser.timeZoneId}" />
		</h1>
		<h2>Cash Items</h2>
		<a class="icon icon-file-download export-link" href="/admin/accounting/export_summary_detail/${summaryId}">Export to CSV</a>
	</div>
	<div id="summary_view">
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Money In</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Fast Funds</td>
					<td><fmt:formatNumber value="${summary.moneyInFastFunds}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyInFastFundsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Subscription Credits - Paid Invoices</td>
					<td><a href="/admin/accounting/export_money_in_credit_memo_details_subscription/${summaryId}"><fmt:formatNumber value="${summary.moneyInSubscriptionCreditMemo}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_money_in_credit_memo_details_subscription/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.moneyInSubscriptionCreditMemoHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
				<tr>
					<td>Professional Service Credits - Paid Invoices</td>
					<td><a href="/admin/accounting/export_money_in_credit_memo_details_prof_services/${summaryId}"><fmt:formatNumber value="${summary.moneyInProfServicesCreditMemo}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_money_in_credit_memo_details_prof_services/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.moneyInProfServicesCreditMemoHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
				<tr>
					<td>Checks</td>
					<td><fmt:formatNumber value="${summary.moneyInChecks}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyInChecksHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>ACH</td>
					<td><fmt:formatNumber value="${summary.moneyInAch}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyInAchHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Wire Transfers</td>
					<td><fmt:formatNumber value="${summary.moneyInWire}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyInWireHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Credit Card Deposits</td>
					<td><fmt:formatNumber value="${summary.moneyInCreditCard}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyInCreditCardHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				</tbody>
			</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Money Out</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Withdrawals (US)</td>
					<td><fmt:formatNumber value="${summary.moneyOutWithdrawals}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutWithdrawalsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Withdrawals (Non-US)</td>
					<td><fmt:formatNumber value="${summary.moneyOutNonUSAWithdrawals}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutNonUSAWithdrawalsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Withdrawals PayPal</td>
					<td><fmt:formatNumber value="${summary.moneyOutPayPalWithdrawal}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutPayPalWithdrawalHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Withdrawals Global Cash Card</td>
					<td><fmt:formatNumber value="${summary.moneyOutGCCWithdrawal}" type="currency"  /></td>
					<td><fmr:formatNumber value="${summary.moneyOutGCCWithdrawalHistorical}" type="currency" /> </td>
				</tr>
				<tr>
					<td>Transaction Fees - Software</td>
					<td><fmt:formatNumber value="${summary.moneyOutTransactionalVorSoftwareFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutTransactionalVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Transaction Fees - VOR</td>
					<td><fmt:formatNumber value="${summary.moneyOutTransactionalVorVorFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutTransactionalVorVorFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Transaction Fees - Software (NVOR)</td>
					<td><fmt:formatNumber value="${summary.moneyOutTransactionalNonVorSoftwareFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutTransactionalNonVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Subscription VOR - Software Fees</td>
					<c:choose>
						<c:when test="${summaryDetailSoftwareSize == 0}">
							<td><fmt:formatNumber value="${summary.moneyOutSubscriptionVorSoftwareFee}" currencySymbol="$" type="currency"/></td>
						</c:when>
						<c:otherwise>
							<td><a href="/admin/accounting/export_summary_subs_sw_fees/${summaryId}"><fmt:formatNumber value="${summary.moneyOutSubscriptionVorSoftwareFee}" currencySymbol="$" type="currency"/></a></td></a>
						</c:otherwise>
					</c:choose>
					<td><fmt:formatNumber value="${summary.moneyOutSubscriptionVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Subscription VOR - VOR Fees</td>
					<c:choose>
					 <c:when test="${summaryDetailVorSize == 0}">
							<td><fmt:formatNumber value="${summary.moneyOutSubscriptionVorVorFee}" currencySymbol="$" type="currency"/></td>
						</c:when>
						<c:otherwise>
							<td><a href="/admin/accounting/export_summary_subs_vor_fees/${summaryId}"><fmt:formatNumber value="${summary.moneyOutSubscriptionVorVorFee}" currencySymbol="$" type="currency"/></a></td>
						</c:otherwise>
					</c:choose>
					<td><fmt:formatNumber value="${summary.moneyOutSubscriptionVorVorFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Subscription NVOR - Software Fees</td>
					<c:choose>
						<c:when test="${summaryDetailNonVorSize == 0}">
							<td><fmt:formatNumber value="${summary.moneyOutSubscriptionNonVorSoftwareFee}" currencySymbol="$" type="currency"/></td>
						</c:when>
						<c:otherwise>
							<td><a href="/admin/accounting/export_summary_subs_nonvor_sw_fees/${summaryId}"><fmt:formatNumber value="${summary.moneyOutSubscriptionNonVorSoftwareFee}" currencySymbol="$" type="currency"/></a></td>
						</c:otherwise>
					</c:choose>
					<td><fmt:formatNumber value="${summary.moneyOutSubscriptionNonVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Professional Services Fees</td>
					<td><fmt:formatNumber value="${summary.moneyOutProfessionalServiceFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutProfessionalServiceFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>PayPal Fees</td>
					<td><fmt:formatNumber value="${summary.moneyOutPayPalFees}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutPayPalFeesHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Credit Card Fees</td>
					<td><fmt:formatNumber value="${summary.moneyOutCreditCardFees}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutCreditCardFeesHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fast Funds Receivable Payments</td>
					<td><fmt:formatNumber value="${summary.moneyOutFastFundsReceivablePayments}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutFastFundsReceivablePaymentsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fast Funds Fees</td>
					<td><fmt:formatNumber value="${summary.moneyOutFastFundsFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutFastFundsFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Background Checks</td>
					<td><fmt:formatNumber value="${summary.moneyOutBackgroundChecks}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutBackgroundChecksHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Drug Tests</td>
					<td><fmt:formatNumber value="${summary.moneyOutDrugTests}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutDrugTestsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Deposit Return Fees</td>
					<td><fmt:formatNumber value="${summary.moneyOutDepositReturnFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutDepositReturnFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Withdrawal Return Fees</td>
					<td><fmt:formatNumber value="${summary.moneyOutWithdrawalReturnFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutWithdrawalReturnFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Late Payment Fees</td>
					<td><fmt:formatNumber value="${summary.moneyOutLatePaymentFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutLatePaymentFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Miscellaneous Fees</td>
					<td><fmt:formatNumber value="${summary.moneyOutMiscellaneousFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutMiscellaneousFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
		</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Miscellaneous Cash</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Cash Out Total (Debits)</td>
					<td><fmt:formatNumber value="${summary.moneyOutDebitTransactions}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutDebitTransactionsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Cash In Total (Credits)</td>
					<td><fmt:formatNumber value="${summary.moneyOutCreditTransactions}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutCreditTransactionsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
		</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Other Cash Debits</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>ACH Deposit Return</td>
					<td><fmt:formatNumber value="${summary.debitAchDepositReturn}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitAchDepositReturnHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Advance Repayment</td>
					<td><fmt:formatNumber value="${summary.debitAdvanceRepayment}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitAdvanceRepaymentHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Assignment Payment Reversal</td>
					<td><fmt:formatNumber value="${summary.debitAssignmentPaymentReversal}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitAssignmentPaymentReversalHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Credit Card Chargeback</td>
					<td><fmt:formatNumber value="${summary.debitCreditCardChargeback}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitCreditCardChargebackHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Credit Card Return</td>
					<td><fmt:formatNumber value="${summary.debitCreditCardRefund}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitCreditCardRefundHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Reclass From Available to Spend</td>
					<td><fmt:formatNumber value="${summary.debitReclassFromAvailableToSpend}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitReclassFromAvailableToSpendHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fast Funds Debit</td>
					<td><fmt:formatNumber value="${summary.debitFastFunds}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitFastFundsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Miscellaneous</td>
					<td><fmt:formatNumber value="${summary.debitMiscellaneous}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitMiscellaneousHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Adjustment</td>
					<td><fmt:formatNumber value="${summary.debitAdjustment}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.debitAdjustmentHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
		</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Other Cash Credits</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tfoot>
				<tr>
					<td>Money On Platform</td>
					<td><fmt:formatNumber value="${summary.totalMoneyOnSystem}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.totalMoneyOnSystemHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tfoot>
			<tbody>
				<tr>
					<td>Withdrawal Returns</td>
					<td><fmt:formatNumber value="${summary.creditAchWithdrawableReturn}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditAchWithdrawableReturnHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Advance</td>
					<td><fmt:formatNumber value="${summary.creditAdvance}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditAdvanceHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Assignment Payment Reversal</td>
					<td><fmt:formatNumber value="${summary.creditAssignmentPaymentReversal}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditAssignmentPaymentReversalHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fee Refund - VOR (Software)</td>
					<td><fmt:formatNumber value="${summary.creditFeeRefundVorSoftware}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditFeeRefundVorSoftwareHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fee Refund - VOR (VOR)</td>
					<td><fmt:formatNumber value="${summary.creditFeeRefundVor}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditFeeRefundVorHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fee Refund - NVOR</td>
					<td><fmt:formatNumber value="${summary.creditFeeRefundNvor}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditFeeRefundNvorHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Background Check Refund</td>
					<td><fmt:formatNumber value="${summary.creditBackgroundCheckRefund}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditBackgroundCheckRefundHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Drug Test Refund</td>
					<td><fmt:formatNumber value="${summary.creditDrugTestRefund}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditDrugTestRefundHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>General Refund</td>
					<td><fmt:formatNumber value="${summary.creditGeneralRefund}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditGeneralRefundHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Marketing Payment</td>
					<td><fmt:formatNumber value="${summary.creditMarketingPayment}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditMarketingPaymentHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Reclass To Available to Withdraw</td>
					<td><fmt:formatNumber value="${summary.creditReclassToAvailableToWithdrawal}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditReclassToAvailableToWithdrawalHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fast Funds Credit</td>
					<td><fmt:formatNumber value="${summary.creditFastFunds}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditFastFundsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fast Funds Fee Refund</td>
					<td><fmt:formatNumber value="${summary.creditFastFundsFeeRefund}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditFastFundsFeeRefundHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Miscellaneous</td>
					<td><fmt:formatNumber value="${summary.creditMiscellaneous}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditMiscellaneousHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Adjustment</td>
					<td><fmt:formatNumber value="${summary.creditAdjustment}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.creditAdjustmentHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
		</table>

		<h2>Accounting Items</h2>
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Throughput</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Transaction VOR</td>
					<td><fmt:formatNumber value="${summary.throughputTransactionalVor}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.throughputTransactionalVorHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Transaction NVOR</td>
					<td><fmt:formatNumber value="${summary.throughputTransactionalNonVor}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.throughputTransactionalNonVorHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Subscription VOR</td>
					<td><fmt:formatNumber value="${summary.throughputSubscriptionVor}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.throughputSubscriptionVorHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Subscription NVOR</td>
					<td><fmt:formatNumber value="${summary.throughputSubscriptionNonVor}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.throughputSubscriptionNonVorHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
		</table>
		<table class="table table-striped table-hover">
			<thead>
			<tr>
				<th>Throughput - Offline</th>
				<th>Daily</th>
				<th>YTD</th>
			</tr>
			</thead>
			<tbody>
			<tr>
				<td>Transaction VOR - Offline</td>
				<td><a href="/admin/accounting/export_offline/${summaryId}?subscription=false&vor=true"><fmt:formatNumber value="${summary.offlineTransactionVOR}" currencySymbol="$" type="currency"/></a></td>
				<td><a href="/admin/accounting/export_offline/${summaryId}?subscription=false&vor=true&ytd=true"><fmt:formatNumber value="${summary.offlineTransactionVORHistorical}" currencySymbol="$" type="currency"/></a></td>
			</tr>
			<tr>
				<td>Transaction NVOR - Offline</td>
				<td><a href="/admin/accounting/export_offline/${summaryId}?subscription=false&vor=false"><fmt:formatNumber value="${summary.offlineTransactionNVOR}" currencySymbol="$" type="currency"/></a></td>
				<td><a href="/admin/accounting/export_offline/${summaryId}?subscription=false&vor=false&ytd=true"><fmt:formatNumber value="${summary.offlineTransactionNVORHistorical}" currencySymbol="$" type="currency"/></a></td>
			</tr>
			<tr>
				<td>Subscription VOR - Offline</td>
				<td><a href="/admin/accounting/export_offline/${summaryId}?subscription=true&vor=true"><fmt:formatNumber value="${summary.offlineSubscriptionVOR}" currencySymbol="$" type="currency"/></a></td>
				<td><a href="/admin/accounting/export_offline/${summaryId}?subscription=true&vor=true&ytd=true"><fmt:formatNumber value="${summary.offlineSubscriptionVORHistorical}" currencySymbol="$" type="currency"/></a></td>
			</tr>
			<tr>
				<td>Subscription NVOR - Offline</td>
				<td><a href="/admin/accounting/export_offline/${summaryId}?subscription=true&vor=false"><fmt:formatNumber value="${summary.offlineSubscriptionNVOR}" currencySymbol="$" type="currency"/></a></td>
				<td><a href="/admin/accounting/export_offline/${summaryId}?subscription=true&vor=false&ytd=true"><fmt:formatNumber value="${summary.offlineSubscriptionNVORHistorical}" currencySymbol="$" type="currency"/></a></td>
			</tr>
			</tbody>
		</table>
		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Subscription Fees</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Subscription VOR - Software Fees</td>
					<td><a href="/admin/accounting/export_summary_acc_rev_subs_vor_sw/${summaryId}"><fmt:formatNumber value="${summary.revenueSubscriptionVorSoftwareFee}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_summary_acc_rev_subs_vor_sw_ytd/${summaryId}"><fmt:formatNumber value="${summary.revenueSubscriptionVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
				<tr>
					<td>Subscription VOR - VOR Fees</td>
					<td><a href="/admin/accounting/export_summary_acc_rev_subs_vor_vor/${summaryId}"><fmt:formatNumber value="${summary.revenueSubscriptionVorVorFee}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_summary_acc_rev_subs_vor_vor_ytd/${summaryId}"><fmt:formatNumber value="${summary.revenueSubscriptionVorVorFeeHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
				<tr>
					<td>Subscription NVOR - Software Fees</td>
					<td><a href="/admin/accounting/export_summary_acc_rev_subs_nvor/${summaryId}"><fmt:formatNumber value="${summary.revenueSubscriptionNonVorSoftwareFee}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_summary_acc_rev_subs_nvor_ytd/${summaryId}"><fmt:formatNumber value="${summary.revenueSubscriptionNonVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
				</tbody>
			</table>

			<table class="table table-striped table-hover">
				<thead>
				<tr>
					<th>Subscription Fees - Credit</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
				</thead>
				<tbody>
				<tr>
					<td>Subscription VOR - Software Fees - Credits</td>
					<td><a href="/admin/accounting/export_acc_credit_memo_details_vor_software/${summaryId}"><fmt:formatNumber value="${summary.creditSubscriptionVorSoftwareFee}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_acc_credit_memo_details_vor_software/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.creditSubscriptionVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
				<tr>
					<td>Subscription VOR - VOR Fees - Credits</td>
					<td><a href="/admin/accounting/export_acc_credit_memo_details_vor_vor/${summaryId}"><fmt:formatNumber value="${summary.creditSubscriptionVorVorFee}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_acc_credit_memo_details_vor_vor/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.creditSubscriptionVorVorFeeHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
				<tr>
					<td>Subscription NVOR - Software Fees - Credits</td>
					<td><a href="/admin/accounting/export_acc_credit_memo_details_nonvor_software/${summaryId}"><fmt:formatNumber value="${summary.creditSubscriptionNonVorSoftwareFee}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_acc_credit_memo_details_nonvor_software/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.creditSubscriptionNonVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
			</tbody>
		</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Professional Services Fees</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Professional Services Fees</td>
					<td><fmt:formatNumber value="${summary.revenueProfessionalServiceFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.revenueProfessionalServiceFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
			<tbody>
				<tr>
					<td>Professional Services Fees - Credits</td>
					<td><a href="/admin/accounting/export_acc_credit_memo_details_prof_services/${summaryId}"><fmt:formatNumber value="${summary.creditProfessionalServiceFee}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_acc_credit_memo_details_prof_services/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.creditProfessionalServiceFeeHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
			</tbody>
		</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Other Fees</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Deposit Return Fees</td>
					<td><fmt:formatNumber value="${summary.revenueDepositReturnFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.revenueDepositReturnFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Withdrawal Return Fees</td>
					<td><fmt:formatNumber value="${summary.revenueWithdrawalReturnFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.revenueWithdrawalReturnFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Late Payment Fees</td>
					<td><fmt:formatNumber value="${summary.revenueLatePaymentFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.revenueLatePaymentFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Miscellaneous Fees</td>
					<td><fmt:formatNumber value="${summary.revenueMiscellaneousFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.revenueMiscellaneousFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
		</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Deferred Subscription Fees</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Subscription VOR - Software Fees</td>
					<td><fmt:formatNumber value="${summary.defRevenueSubscriptionVorSoftwareFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.defRevenueSubscriptionVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Subscription VOR - VOR Fees</td>
					<td><fmt:formatNumber value="${summary.defRevenueSubscriptionVorVorFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.defRevenueSubscriptionVorVorFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Subscription NVOR - Software Fees</td>
					<td><fmt:formatNumber value="${summary.defRevenueSubscriptionNonVorSoftwareFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.defRevenueSubscriptionNonVorSoftwareFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Professional Services Fees</td>
					<td><fmt:formatNumber value="${summary.defRevenueProfessionalServiceFee}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.defRevenueProfessionalServiceFeeHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
		</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Receivables</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
                <tr>
                    <td>Subscription Fees Receivables</td>
                    <td><fmt:formatNumber value="${summary.subscriptionFeeReceivables}" currencySymbol="$" type="currency"/></td>
                    <td><fmt:formatNumber value="${summary.subscriptionFeeReceivablesHistorical}" currencySymbol="$" type="currency"/></td>
                </tr>
				<tr>
					<td>Subscription Fees Receivables - Credits</td>
					<td><a href="/admin/accounting/export_summary_acc_credit_subscription_receivables/${summaryId}"><fmt:formatNumber value="${summary.subscriptionCreditMemoReceivables}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_summary_acc_credit_subscription_receivables/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.subscriptionCreditMemoReceivablesHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
                <tr>
                    <td>Professional Services Fees Receivables</td>
                    <td><fmt:formatNumber value="${summary.professionalServiceFeeReceivables}" currencySymbol="$" type="currency"/></td>
                    <td><fmt:formatNumber value="${summary.professionalServiceFeeReceivablesHistorical}" currencySymbol="$" type="currency"/></td>
                </tr>
				<tr>
					<td>Professional Services Fees Receivables - Credits</td>
					<td><a href="/admin/accounting/export_summary_acc_credit_prof_services_receivables/${summaryId}"><fmt:formatNumber value="${summary.profServicesCreditMemoReceivables}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_summary_acc_credit_prof_services_receivables/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.profServicesCreditMemoReceivablesHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>
				<tr>
					<td>Other Fees Receivables</td>
					<td><fmt:formatNumber value="${summary.adHocServiceFeeReceivables}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.adHocServiceFeeReceivablesHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
				<tr>
					<td>Fast Funds Receivables</td>
					<td><a href="/admin/accounting/export_summary_fast_funds_receivables/${summaryId}"><fmt:formatNumber value="${summary.fastFundsFeeReceivables}" currencySymbol="$" type="currency"/></a></td>
					<td><a href="/admin/accounting/export_summary_fast_funds_receivables/${summaryId}?ytd=true"><fmt:formatNumber value="${summary.fastFundsFeeReceivablesHistorical}" currencySymbol="$" type="currency"/></a></td>
				</tr>


			</tbody>
		</table>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th>Other</th>
					<th>Daily</th>
					<th>YTD</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>ACH Verification</td>
					<td><fmt:formatNumber value="${summary.moneyOutAchVerifications}" currencySymbol="$" type="currency"/></td>
					<td><fmt:formatNumber value="${summary.moneyOutAchVerificationsHistorical}" currencySymbol="$" type="currency"/></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>

</wm:admin>
