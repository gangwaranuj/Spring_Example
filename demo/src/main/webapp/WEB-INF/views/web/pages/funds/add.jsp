<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Add Funds" bodyclass="payment" breadcrumbSection="Payments" breadcrumbSectionURI="/payments" breadcrumbPage="Add Funds" webpackScript="payments">

	<script>
		var config = {
			'payments': ${contextJson}
		};
	</script>

	<c:import url="/payments/dashboard"/>

	<div class="page-header"></div>
	<div id="add_funds_wizard" class="inner-container">
		<div id="add-funds-wrapper">
			<div class="page-header clearfix">
				<h3 class="pull-left">Add Funds</h3>
			</div>

			<div class="alert alert-info">
				<strong>Step 1: Choose your payment method</strong>
			</div>
			<ul>
				<li>
					<input id="creditcard" name="addfundtype" type="radio" value="0">
					<Strong class="radio-text">Credit Card</Strong>
					<img src="${mediaPrefix}/images/cc-accepted.png" alt="Accepted Payments" width="324" height="23" style="padding-left: 10px">
					<p>Funding transfers are instant but a processing fee will be assessed.</p>
					<div class="page-header"></div>
				</li>
				<li>
					<input id="bankaccount" name="addfundtype" type="radio" value="1" <c:if test="${not hasUsaBankAccount}">disabled="disabled"</c:if> />
					<Strong class="radio-text">Bank Account (ACH)</Strong>
					<p>Funding transfers are free but can take 2 to 3 business days. (<b>Note</b>: US Accounts Only)</p>
					<div class="page-header"></div>
				</li>
				<li>
					<input id="wiretransfer" name="addfundtype" type="radio" value="2">
					<Strong class="radio-text">Wire Transfer</Strong>
					<p>Funding transfers are free and typically take less than 24 hours once the account is setup.</p>
					<div class="page-header"></div>
				</li>
				<li>
					<input id="mailacheck" name="addfundtype" type="radio" value="3">
					<Strong class="radio-text">Mail a Check</Strong>
					<p>Funding transfers are free but can take 7 to 10 business days.</p>
					<div class="page-header"></div>
				</li>
				<li>
					<p>Need to generate an invoice for your accounting team? <a href="/funds/invoice">Create a manual invoice</a></p>
				</li>
			</ul>
			<div class="wm-action-container">
				<button type="button" class="button" disabled="disabled" id="addfunds-next">Next Step</button>
			</div>
		</div>
	</div>

	<div id="addcc_view" class="dn">
		<c:import url='/WEB-INF/views/web/partials/funds/addcc.jsp'/>
	</div>

	<div id="addach_view" class="dn">
		<c:import url='/WEB-INF/views/web/partials/funds/addach.jsp'/>
	</div>

	<div id="addwire_view" class="dn">
		<c:import url='/WEB-INF/views/web/partials/funds/addwire.jsp'/>
	</div>

	<div id="addcheck_view" class="dn">
		<c:import url='/WEB-INF/views/web/partials/funds/addcheck.jsp'/>
	</div>

</wm:app>
