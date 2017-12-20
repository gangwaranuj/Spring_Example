<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Reporting" webpackScript="admin">

	<script>
		var config = {
			mode: 'subscriptionReporting'
		};
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">

		<c:import url="/WEB-INF/views/web/partials/admin/manage/subscriptions/tabs.jsp" />

		<c:if test="${generalData.totalCompanies} >0">
			<div id="general_subscription_report">
				<table class="general_subscription_table">
					<thead>
					<tr>
						<th>Total Number of Companies</th>
						<th>Total Terms Used</th>
						<th>Total Number with VOR</th>
						<th>Total MRR</th>
						<th>Total ARR</th>
					</tr>
					</thead>
					<tbody>
					<tr>
						<td>${generalData.totalCompanies}</td>
						<td><fmt:formatNumber value="${generalData.sumOfTerms}" type="currency" /></td>
						<td>${generalData.totalVorCompanies}</td>
						<td><fmt:formatNumber value="${generalData.sumOfMonthlyRecurringRevenue}" type="currency" /></td>
						<td><fmt:formatNumber value="${generalData.sumOfAnnualRecurringRevenue}" type="currency" /></td>
					</tr>
					</tbody>
				</table>
			</div>
		</c:if>

		<div id="standard_subscription_report">
			<table class="table table-striped" id="standard_subscription_report_table">
				<thead>
				<tr>
					<th>Company Name</th>
					<th>Effective Date</th>
					<th>Signed Date</th>
					<th>Account Manager</th>
					<th>Terms Used</th>
					<th>Subscription Term (months)</th>
					<th>VOR</th>
					<th>Auto Renewal</th>
					<th>Renewal Date</th>
					<th>Duration (months)</th>
					<th>Current Tier Range</th>
					<th>Payment Period</th>
					<th>MRR</th>
					<th>ARR</th>
				</tr>
				</thead>
				<tbody>
					<%-- Filled with DataTables --%>
				</tbody>
			</table>
		</div>
	</div>

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

	<script id="company-name-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/admin/manage/company/overview/\${meta.company_id}">\${data}</a>
		</div>
	</script>

	<script id="currency-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if data === null}}
				\${format_currency(0)}
			{{else}}
				\${format_currency(parseFloat(data))}
			{{/if}}
		</div>
	</script>

	<script id="auto-renewall-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if data == "1"}}
				\${data} - Renewal
			{{else parseInt(data) > 1}}
				\${data} - Renewals
			{{else}}
				None
			{{/if}}
		</div>
	</script>

	<script id="current-tier-range-tmpl" type="text/x-jquery-tmpl">
		<div>
			<span>\${format_currency(parseFloat(meta.current_tier_lower_bound_throughput))} - \${format_currency(parseFloat(data))}</span>
		</div>
	</script>

</wm:admin>
