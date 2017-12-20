<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Assignment Feedback Report" bodyclass="reports" webpackScript="reports" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="Assignment Ratings">

	<script>
		var config = {
			mode: 'default'
		};
	</script>

	<h3>Buyer Ratings Report</h3>
	<p>
		<a class="icon icon-xls" id="export-outlet" href="/reports/assignment_feedback.csv">Export CSV</a>
		<a class="icon icon-gear" href="javascript:void(0);" id="cta-toggle-filters">Show Filters</a>
	</p>

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />
	<c:import url="/WEB-INF/views/web/partials/reports/filters.jsp" />
	<c:import url="/WEB-INF/views/web/partials/reports/review.jsp" />

	<table id="report_list" class="table-report">
		<thead>
			<tr>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>ID</th>
				<th style="min-width:150px;" nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Title</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Worker</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Internal Owner</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Rating Date</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Paid Date</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Rating</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Review</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Payment Timeliness</th>
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
