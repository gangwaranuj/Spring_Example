<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>

<c:set var="pageScript" value="wm.pages.mobile.withdraw" scope="request"/>
<c:set var="pageScriptParams" value="${paypalFees}, ${paypalCountryCodes}" scope="request"/>
<c:set var="backUrl" value="/mobile" scope="request" />

<div class="wrap funds">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Funds" />
	</jsp:include>

	<div class="grid content">
		<div id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div>

		<div class="section less-important">
			<div class="unit whole mobile-list">
				<span>
					<div class="count funds-title">Current Receivables</div>
					<div class="status"><fmt:formatNumber value="${sellerSums.upcomingDue}" currencySymbol="$" type="currency"/></div>
				</span>
			</div>
		</div>

		<div class="section late-stage">
			<div class="unit whole mobile-list">
				<span>
					<div class="count funds-title">Contractor Earnings</div>
					<div class="status"><fmt:formatNumber value="${sellerSums.paidYtd}" currencySymbol="$" type="currency"/></div>
				</span>
			</div>
		</div>

		<div class="section past-due-stage">
			<div class="unit whole mobile-list">
				<span>
					<div id="available-count" class="count funds-title">Past Due Receivables</div>
					<div class="status"><fmt:formatNumber value="${sellerSums.pastDue}" currencySymbol="$" type="currency"/></div>
				</span>
			</div>
		</div>

		<div class="section funds-stage">
			<div class="unit whole mobile-list">
				<span>
					<div class="count funds-title">Available to Withdraw</div>
					<div class="status"><fmt:formatNumber value="${available_balance}" currencySymbol="$" type="currency"/></div>
				</span>
			</div>
		</div>

		<c:if test="${has_verified_taxentity}">
			<div class="unit whole withdraw">
				<div class="withdraw-container">
					<h4>Withdraw Funds</h4>
						<sf:form action="/mobile/funds/withdraw" method="POST" id="withdrawFundsForm" modelAttribute="withdrawFundsForm" accept-charset="utf-8">
							<wm-csrf:csrfToken/>

							<label>Transfer to:</label>
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
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-select-arrow-down.jsp" />

							<div class="config dn" rel="ach ppa gcc">
								<hr/>
								<label class="control-label" path="amount">Amount to withdraw:</label>
								<div class="input-prepend">
									<span class="add-on">$</span>
									<sf:input path="amount" type="number" placeholder="0.00" maxlength="20" step="any"/>
								</div>
							</div>

							<div class="config dn" rel="ach">
								<hr/>
								<label>Transfer to Bank:</label>
								<div>
									<strong class="final-amount"><span class="amount-outlet">$0.00</span> USD</strong>
								</div>
							</div>

							<div class="config dn" rel="gcc">
								<hr/>
								<label>Transfer to Visa card:</label>
								<div>
									<strong class="final-amount"><span class="amount-outlet">$0.00</span> USD</strong>
								</div>
							</div>

							<div class="config dn" rel="ppa">
								<hr/>
								<label>PayPal fee:</label>
								<span class="fee-calc-outlet">$1.00</span>
								<hr/>
								<label>Transfer to PayPal:</label>
								<div>
									<strong class="final-amount"><span class="amount-outlet">$0.00</span> USD</strong>
								</div>
							</div>

							<button id="withdrawBtn" disabled class="withdraw-button">Withdraw Funds</button>
						</sf:form>
				</div>
			</div>
		</c:if>


		<div class="unit whole home-footer-links">
			<a class="home-help-link spin" href="/mobile/help">Need some help?</a>
			<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-signout.jsp"/>
			<a href="/logout" id="logout">Sign Out</a>
			<c:if test="${empty cookie['wm-app-platform'].value}">
				<%-- Only show full site link if we are NOT using an app --%>
				<p><a href="/?site_preference=normal">View Desktop Site</a></p>
			</c:if>
		</div>


	</div>
</div>
