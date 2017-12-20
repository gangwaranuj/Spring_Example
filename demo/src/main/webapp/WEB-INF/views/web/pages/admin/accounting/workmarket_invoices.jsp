<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<wm:admin pagetitle="Invoices" webpackScript="admin">

	<script>
		var config = {
			mode: 'invoices'
		};
	</script>

	<style>
		label{
			display: inline-block;
			vertical-align: middle;
			float: left;
			text-align: left;
			padding-right: 50px;
		}
	</style>

	<c:set var="hasAccountingRole" value="false" scope="request"/>
	<sec:authorize access="hasRole('ROLE_WM_ACCOUNTING')">
		<c:set var="hasAccountingRole" value="true" scope="request"/>
	</sec:authorize>

	<c:import url="/breadcrumb">
		<c:param name="pageId" value="adminAccountingWorkMarketInvoices" />
	</c:import>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp"/>

		<h1>Outstanding Work Market Invoices</h1>
		</br>

		<div id="wm_invoices_container">
			<div>
				<label>Status:
					<select name="invoice_status" class="span2">
						<c:forEach items="${invoiceStatusTypes}" var="statusType">
							<option value="${statusType}"><c:out value="${statusType}"/></option>
						</c:forEach>
					</select>
					Pending payment balance: <fmt:formatNumber value="${pendingInvoicesTotal}" currencySymbol="$" type="currency"/>
				</label>
				<label>Company: <input name="company_search" id="company_search" type="search" /></label>
				<label>Invoice #: <input name="invoice_search" id="invoice_search" type="search" /></label>
			</div>

			<table id="wm_invoices_table" class="table table-striped">
				<thead>
				<tr>
					<th>Type</th>
					<th>Name</th>
					<th>Invoice Number</th>
					<th>Company</th>
					<th>Issue Date</th>
					<th>Due Date</th>
					<th>Payment Date</th>
					<th>Amount</th>
					<th>Days Past Due</th>
					<th>Status</th>
					<th>Revenue Month</th>
					<th>Action</th>
				</tr>
				</thead>
				<tbody>
					<%-- Filled with DataTables --%>
				</tbody>
			</table>
		</div>
	</div>

	<script id="invoice-type-tmpl" type="text/x-jquery-tmpl">
		<div>
			\${( $data.typeCode = data ),''}
			{{if typeCode == 'adHoc'}}
				Ad-Hoc
			{{else typeCode == 'subscription'}}
				Subscription
			{{else typeCode == 'creditMemo'}}
				Credit Memo
			{{/if}}
		</div>
	</script>

	<script id="invoice-id-tmpl" type="text/x-jquery-tmpl">
		<div><a target="_blank" href="/payments/invoices/print_service_invoice/\${meta.id}">\${data}</a></div>
	</script>

	<script id="invoice-company-name-tmpl" type="text/x-jquery-tmpl">
		<div><a target="_blank" href="/admin/manage/company/overview/\${meta.company_id}">\${data}</a></div>
	</script>

	<script id="invoice-action-tmpl" type="text/x-jquery-tmpl">
		\${( $data.hasAccoutingRole = ${hasAccountingRole} ),''}
		{{if hasAccoutingRole}}
			{{if meta.isCreditMemo}}
				<div></div>
			{{else}}
				{{if meta.isCreditMemoIssuable}}
					<div><a id="create_credit_memo" target="" href="/admin/accounting/credit_memo?invoiceId=\${meta.id}"><i class="wm-icon-plus"/></a></div>
				{{else}}
				 	<div></div>
				{{/if}}
			{{/if}}
		{{else}}
			<div></div>
		{{/if}}
	</script>

	<script type="text/javascript">
		function format_currency(num) {
			num = (num || 0).toString().replace(/\$|\,/g, '');
			if (!isFinite(num))
				num = "0";

			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();

			if (cents < 10)
				cents = "0" + cents;

			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));

			return (((sign) ? '' : '-') + '$' + num + '.' + cents);
		}
	</script>

	<script id="invoice-amount-tmpl" type="text/x-jquery-tmpl">
		<div>\${format_currency(data)}</div>
	</script>

</wm:admin>
