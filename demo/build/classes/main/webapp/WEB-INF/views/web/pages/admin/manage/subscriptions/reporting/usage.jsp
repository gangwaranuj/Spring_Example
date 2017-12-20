<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Usage" webpackScript="admin">
	<script>
		var config = {
			mode: 'subscriptionReporting'
		}
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">

		<c:import url="/WEB-INF/views/web/partials/admin/manage/subscriptions/tabs.jsp" />

		<div><a href="/admin/manage/subscriptions/reporting/usage/export">Export</a></div>

		<div id="usage_subscription_report">
			<table id="usage_subscription_report_table" class="table table-striped">
				<thead>
				<tr>
					<th>Company Name</th>
					<th>Effective Date</th>
					<th>Existing Payable - Current</th>
					<th>Existing Payable - Past Due</th>
					<th>% On time Payment</th>
					<th>Current Annual Throughput</th>
					<th>Tier Throughput Usage (%)</th>
					<th>Software Tier</th>
					<th>VOR Tier</th>
					<th>Next Software Tier</th>
					<th>Next VOR Tier</th>
				</tr>
				</thead>
				<tbody>
					<%-- Filled with DataTables --%>
				</tbody>
			</table>
		</div>
	</div>

	<script id="company-name-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/admin/manage/company/overview/\${meta.company_id}">\${data}</a>
		</div>
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

	<script id="currency-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if data === null}}
				\${format_currency(0)}
			{{else}}
				\${format_currency(parseFloat(data))}
			{{/if}}
		</div>
	</script>

	<script id="percent-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if data === null}}
				\${parseFloat(0)}%
			{{else}}
				\${parseFloat(data)}%
			{{/if}}
		</div>
	</script>

</wm:admin>
