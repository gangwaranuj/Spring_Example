<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Accounting">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<h4>Tools</h4>
	<ul>
		<li><a href="/admin/accounting/achfunding">ACH Funding Requests</a></li>
		<li><a href="/admin/accounting/withdrawals/ach">ACH Withdrawal Requests</a></li>
		<li><a href="/admin/accounting/withdrawals/paypal">PayPal Withdrawal Requests</a></li>
		<li><a href="/admin/accounting/withdrawals/gcc">GCC Withdrawal Requests</a></li>
		<li><a href="/admin/accounting/nacha?type=inbound">Queue Processing</a></li>
		<li><a href="/admin/accounting/managefunds">Manage Company Funds</a></li>
		<li><a href="/admin/accounting/adhoc_invoices">Issue Ad-hoc Invoice</a></li>
		<li><a href="/admin/accounting/form_1099">Tax Form Issuance Service</a></li>
		<li><a href="https://system.netsuite.com/pages/customerlogin.jsp" target="_blank">Net Suite</a></li>
	</ul>

	<h4>Reports</h4>
	<ul>
		<li><a href="/admin/accounting/summaries">Accounting Reports (JES)</a></li>
		<li><a href="/admin/accounting/workmarket_invoices">Work Market Invoices</a></li>
		<li><a href="/admin/accounting/locked_accounts">Locked Accounts</a></li>
		<li><a href="/admin/reporting/funding_transactions">Funding Transactions</a></li>
		<li><a href="/admin/accounting/vor_nvor_report">Detailed VOR/NVOR Report</a></li>
		<li><a href="/admin/accounting/vor_nvor_status_update">End of Year Tax Service Status Update</a></li>
	</ul>
</div>

</wm:admin>
