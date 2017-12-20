<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:import url='/WEB-INF/views/web/partials/message.jsp'/>

<c:if test="${currentUser.seller || currentUser.dispatcher}">
	<div class="boxes container -payment-center">
		<div class="tile -past-due">
			<a class="tile-content" href="/payments/invoices/receivables/past-due" data-action="push-state">
				<h2>Past Due Receivables</h2>
				<hr/>
				<small class="currency"><fmt:formatNumber value="${sellerSums.pastDue}" currencySymbol="$" type="currency"/></small>
			</a>
			<a class="tile-cta" href="/payments/invoices/receivables/past-due" data-action="push-state">
				Due to You
			</a>
		</div>
		<div class="-current-payables tile">
			<div class="tile-content">
				<a href="/payments/invoices/receivables/upcoming-due" data-action="push-state">
					<h2>Current Receivables</h2>
					<hr/>
					<small class="currency"><fmt:formatNumber value="${sellerSums.upcomingDue}" currencySymbol="$" type="currency"/></small>
				</a>
				<c:if test="${hasFastFunds}">
					<c:if test="${currentView eq 'receivables'}">
						<small>
							<span class="label label-success">NEW</span> <strong>FastFunds</strong>
							<br />
							Instantly transfer your funds when you see <span style="white-space: nowrap;">'Get Paid Now'</span> on select invoices below.
						</small>
					</c:if>
				</c:if>
			</div>
			<a class="tile-cta" href="/payments/invoices/receivables/upcoming-due" data-action="push-state">
				Receivable Invoices
			</a>
		</div>
		<div class="-contractor-earnings tile">
			<a class="tile-content" href="/payments/ledger" data-action="push-state">
				<h2>Contractor Earnings</h2>
				<hr/>
				<small class="currency"><fmt:formatNumber value="${sellerSums.paidYtd}" currencySymbol="$" type="currency"/></small>
			</a>
			<a class="tile-cta" href="/payments/ledger" data-action="push-state">
				Contractor Earnings (YTD)
			</a>
		</div>
		<div class="-available-withdraw tile">
			<div id="earnings_box" class="tile-content">
				<a href="/funds/withdraw" id="withdrawLink" rel="withdraw">
					<h2>Available to Withdraw</h2>
					<hr/>
					<small class="currency"><fmt:formatNumber value="${accountSummary.withdrawableCash}" currencySymbol="$" type="currency"/></small>
				</a>

				<c:if test="${showGccBanner and currentUser.seller}">
					<p id="gcc_link">
						<span class="label label-success">NEW</span> <a href="/funds/accounts/gcc">Get a WM Visa Card!</a>
					</p>
				</c:if>
				<span id="joyride-withdraw">
					<a href="/funds/withdraw" id="withdrawLink" class="tile-cta" rel="withdraw">Withdraw Funds</a>
				</span>
			</div>
		</div>
	</div>
</c:if>
<c:if test="${currentUser.buyer}">
	<div class="boxes container -payment-center">
		<div class="-past-due tile">
			<%--
			show sum of all invoices where due date is in past and status is unpaid
			clicking should switch user to the Payables tab with filtering to only past due items
			--%>
			<a class="tile-content" href="/payments/invoices/payables/past-due" data-action="push-state">
				<h2>Past Due Payables</h2>
				<hr/>
				<small class="currency"><fmt:formatNumber value="${buyerSums.pastDue}" currencySymbol="$" type="currency"/></small>
			</a>
			<a class="tile-cta" href="/payments/invoices/payables/past-due" data-action="push-state">
				Past Due
			</a>
		</div>
		<div class="-current-payables tile">
			<%--
			show sum of all invoices where due date is not in the past and status is unpaid
			clicking should switch user to the Payables tab with filtering to only unpaid items
			--%>
			<a class="tile-content" href="/payments/invoices/payables/due" data-action="push-state">
				<h2>Current Payables</h2>
				<hr/>
				<small class="currency"><fmt:formatNumber value="${buyerSums.upcomingDue}" currencySymbol="$" type="currency"/></small>
			</a>
			<a class="tile-cta" href="/payments/invoices/payables/due" data-action="push-state">
				Payable Invoices
			</a>
		</div>
		<div class="-cash-balance tile">
			<div class="tile-content">
				<a data-action="push-state" href="/payments/ledger">
					<h2>Cash Balance</h2>
					<hr/>
					<small class="currency"><fmt:formatNumber value="${spendLimit}" currencySymbol="$" type="currency"/></small>
				</a>
				<c:if test="${hasProjectBudgetEnabled}">
					<small>Unreserved Cash: <span class="currency"><fmt:formatNumber value="${generalCash}" currencySymbol="$" type="currency"/></span></small>
					<small>Reserved Funds: <span class="currency"><fmt:formatNumber value="${projectCash}" currencySymbol="$" type="currency"/></span></small>
					<a class="showModalAllocateFunds button" href="/funds/allocate-budget" id="allocateFundsLink">Manage Project Funds</a>
				</c:if>
				<a class="tile-cta" href="/payments/ledger" data-action="push-state">
					View Ledger
				</a>
			</div>
		</div>
		<div class="-available-spend tile">
			<a class="tile-content" href="/payments/ledger">
				<h2>Available to Spend</h2>
				<hr/>
				<small>For labor: <span class="currency"><fmt:formatNumber value="${spendLimit + apLimit}" currencySymbol="$" type="currency"/></span></small>
				<small>Cash: <span class="currency"><fmt:formatNumber value="${spendLimit}" currencySymbol="$" type="currency"/></span></small>
				<br/>
				<c:if test="${apLimit > 0}">
					<small class="-api-limit">
						<span class="tooltipped tooltipped-n" aria-label="Terms represent the amount of work you can send without prefunding your account with cash. You can not use your Terms for your payables.">
							<i class="wm-icon-question-filled"></i>
						</span>
						Available Terms: <span class="currency"><fmt:formatNumber value="${apLimit}" currencySymbol="$" type="currency"/></span>
					</small>
				</c:if>
			</a>
			<span id="joyride-add">
				<a href="/funds/add" id="AddNewLink" class="tile-cta intro-add-funds" rel="add">Add Funds</a>
			</span>
		</div>
	</div>
</c:if>
