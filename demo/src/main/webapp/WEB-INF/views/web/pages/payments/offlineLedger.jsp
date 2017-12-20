<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Ledger" bodyclass="payment page-payments" webpackScript="payments">

	<script>
		var config = {
			payments: ${contextJson}
		};
	</script>

	<c:import url="/payments/dashboard" />

	<div class="inner-container" id="ledger_container">
		<vr:rope>
			<vr:venue name="COMPANY" bypass="true">
				<div class="promotion -main -intuit">
					<img class="third-party-logo" src="${mediaPrefix}/images/intuit.svg" alt="Intuit QuickBooks Logo">
					<p>
						Track your money ins and outs, and budget for quarterly taxes.
						<a
							target="_blank"
							href="https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=ledger&cid=IPD_workmarket_Ledger_QBSE&utm_email=${email}"
						>
								Learn More with QuickBooks
						</a>
					</p>
				</div>
			</vr:venue>
		</vr:rope>
		<c:import url="/WEB-INF/views/web/partials/payments/navigation.jsp" />
		<div class="table">
			<div class="table--header">
				<h1 class="table--title">General Ledger Transactions</h1>
				<form action="/payments/offline_ledger" id="filters" class="pull-right">
					From
					<input type="text" name="transaction_date_from" value="<fmt:formatDate value="${defaultFromDate.time}" pattern="MM/dd/yyyy"/>" id="start_date" class="span2" placeholder="MM/DD/YYYY" maxlength="10" />
					to
					<input type="text" name="transaction_date_to" value="<fmt:formatDate value="${defaultToDate.time}" pattern="MM/dd/yyyy"/>" id="end_date" class="span2" placeholder="MM/DD/YYYY" maxlength="10" />
					<button type="submit" class="button -small">Apply</button>
					<button type="reset" class="button -small">Clear</button>
				</form>
			</div>
			<table id="offline_activity_list">
				<thead>
					<tr>
						<th width="5%">Date</th>
						<th width="15%">Type</th>
						<th>Description</th>
						<th width="15%" class="tar">Debit</th>
						<th width="15%" class="tar">Credit</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>

</wm:app>
