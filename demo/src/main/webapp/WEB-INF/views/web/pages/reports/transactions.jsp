<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Transaction Report" bodyclass="reports" webpackScript="reports" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="Transaction Reports">

	<script>
		var config = {
			mode: 'default'
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />

	<div class="panel">
		<div class="panel-heading">
			<div class="pull-right actions">
				<button type="button" class="button" id="apply-filter-outlet">Refresh Results</button>
				<a class="button" id="export-outlet" href="/reports/transactions.csv">Export CSV</a>
			</div>
			<h2>Transaction Report
				<small class="">(<a class="" href="javascript:void(0);" id="cta-toggle-filters">Hide Filters</a>)</small>
			</h2>
		</div>

		<c:import url="/WEB-INF/views/web/partials/reports/filters.jsp" />
	</div>

	<table id="report_list" class="table-report panel">
		<thead>
		<tr>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>ID</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Scheduled Date</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Approved Date</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Paid Date</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Transaction Date</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Type</th>
			<th style="min-width:250px;"><i class="reports_icon icon-sort icon-small"></i>Title</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Payment</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Fees</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Authorization</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Credits</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Invoice</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Bundle ID</th>
			<c:if test="${not empty customFields}">
				<c:forEach var="field" items="${customFields}">
					<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i><c:out value="${field.fieldName}" /></th>
				</c:forEach>
			</c:if>
		</tr>
		</thead>
		<tbody></tbody>
	</table>

	<script id="cell-title-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.is_show_assignment_title}}
				<a href="/assignments/details/\${meta.work_number}">\${data}</a>
			{{else}}
				\${data}
			{{/if}}
		</div>
	</script>
</wm:app>
