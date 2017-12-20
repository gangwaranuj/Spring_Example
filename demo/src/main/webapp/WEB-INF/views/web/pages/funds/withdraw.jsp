<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Withdraw Funds" bodyclass="payment page-funds" breadcrumbSection="Payments" breadcrumbSectionURI="/payments" breadcrumbPage="Withdraw Funds" webpackScript="payments">

	<script>
		var config = {
			'payments': ${contextJson}
		};
	</script>

	<c:import url="/payments/dashboard"/>

	<c:if test="${has_verified_taxentity}">
		<div id="withdraw_funds" class="withdraw-funds inner-container form-horizontal">

			<div class="page-header">
				<h3>Withdraw Funds</h3>
				<vr:rope>
					<vr:venue name="COMPANY" bypass="true">
						<div class="promotion -intuit">
							<div class="third-party-logo -inline -square -intuit-qb"></div>
							Wondering how much you're really making?
							<a
								target="_blank"
								href="https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=withdrawfund&cid=IPD_workmarket_withdrawfund_QBSE&utm_email=${email}"
							>
								Find Out with QuickBooks
							</a>
						</div>
					</vr:venue>
				</vr:rope>
			</div>
			<div class="messages"></div>
			<c:import url="/WEB-INF/views/web/partials/message.jsp"/>

			<sf:form action="/funds/withdraw" method="POST" id="withdrawFundsForm" modelAttribute="withdrawFundsForm" accept-charset="utf-8">
				<wm-csrf:csrfToken/>

				<c:if test="${showGccBanner}">
					<div class="alert alert-info">
						<p>
							<span class="label success">NEW:</span>
							<strong>Looking to set up direct deposit?</strong>
						</p>
						<p>
							Apply today for a Work Market Visa Card, a free and faster alternative to ACH and
							PayPal. Withdraw your platform earnings to the card automatically and use it
							anywhere Visa is accepted.
						</p>
						<a class="button" href="/funds/accounts/gcc">Apply for a Card</a>
					</div>
				</c:if>
				<div class="messages"></div>
				<p>Funds earned on the Work Market platform are available for withdrawal via electronic transfer (ACH) to
					your bank account, PayPal account, or WM Visa Card. Requests received by 4pm EST will be processed the
					same business day. It may take 2-3 business days to complete the transfer depending on your bank's policies and holiday
					schedule.</p>

				<div class="control-group">
					<sf:label class="control-label" path="account"><strong>Transfer to:</strong></sf:label>
					<div class="controls">
						<sf:select path="account" id="account" cssClass="span5">
							<sf:option name="" value="">- Select -</sf:option>
							<c:forEach items="${accounts}" var="account">
								<c:choose>
									<c:when test="${account.type eq 'PPA'}">
										<sf:option value="${account.id}" data-type="${account.type}" data-country="${(account.country.id == ('CAN' || 'USA')) ? 'ITL' : account.country.id}" >PayPal</sf:option>
									</c:when>
									<c:when test="${account.type eq 'GCC'}">
										<sf:option value="${account.id}" data-type="${account.type}" data-country="${account.country.id}">Work Market Visa Card (${account.keyfieldLastFour})
										</sf:option>
									</c:when>
									<c:otherwise>
										<sf:option value="${account.id}" data-type="${account.type}" data-country="${account.country.id}"><c:out value="${account.accountDescription}" /> (free)</sf:option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</sf:select>
						<span class="help-block"><a href="/funds/accounts/new?return_to_page=payments">Add a financial account</a></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><strong>Available to withdraw:</strong></label>
					<div class="controls currency-control">
						<span><fmt:formatNumber value="${available_balance}" currencySymbol="$" type="currency"/> USD</span>
					</div>
				</div>

				<div class="config dn" rel="ach ppa gcc">
					<hr/>
					<div class="control-group">
						<label class="control-label" path="amount"><strong>Amount to withdraw:</strong></label>
						<div class="controls">
							<div class="input-prepend">
								<span class="add-on">$</span>
								<sf:input path="amount" cssClass="span2"/> USD
							</div>
						</div>
					</div>
				</div>

				<div class="config dn" rel="ach">
					<hr/>
					<div class="alert alert-success" id="canada-funds-message">Funds withdrawn to a Canadian bank account will be deposited in Canadian Dollars (CAD $). Due to
						fluctuations in exchange rates the final amount deposited may vary slightly from when initiated.
					</div>
					<div class="control-group">
						<label class="control-label"><strong>Transfer to Bank:</strong></label>

						<div class="controls currency-control">
							<strong><span class="amount-outlet">$0.00</span> USD</strong>
						</div>
					</div>
				</div>

				<div class="config dn" rel="gcc">
					<hr/>
					<div class="control-group">
						<label class="control-label"><strong>Transfer to Visa card:</strong></label>

						<div class="controls currency-control">
							<strong><span class="amount-outlet">$0.00</span> USD</strong>
						</div>
					</div>
				</div>

				<div class="config dn" rel="ppa">
					<hr/>
					<div class="control-group">
						<label class="control-label">PayPal fee:</label>

						<div class="controls">
							<span class="fee-calc-outlet">$1.00</span>
								<span class="help-block"><a href="http://j.mp/NCmgsj" target="_blank">Why is there a PayPal
									fee?</a></span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><strong>Transfer to PayPal:</strong></label>

						<div class="controls currency-control">
							<strong><span class="amount-outlet">$0.00</span> USD</strong>
						</div>
					</div>
				</div>

				<div class="wm-action-container">
					<button id="closeBtn" type="button" class="button">Cancel</button>
					<button id="withdrawBtn" type="submit" class="button" disabled="disabled">Withdraw Funds</button>
				</div>

					<span class="help-block">
						By clicking &ldquo;Withdraw Funds&rdquo;, you are reaffirming that you have read and agree to the <a
							href="/tos">Work Market Terms of Use Agreement</a>.
					</span>

			</sf:form>
		</div>
	</c:if>

</wm:app>
