<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Reports" bodyclass="reports" webpackScript="reports" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="Report">

	<script>
		var config = {
			mode: 'default'
		};
	</script>

	<div class="panel">
		<div class="panel-heading">
			<div class="pull-right actions">
				<button type="button" class="button" id="apply-filter-outlet">Refresh Results</button>
				<a class="button" id="export-outlet" href="/reports/export">Export CSV</a>
			</div>
			<h2>
				<c:choose>
					<c:when test="${filterForm.buyerReport}">
						<c:choose>
							<c:when test="${filterForm.budgetReport}">
								Budget/Expenses Report
							</c:when>
							<c:otherwise>
								Company Report
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>Earnings Report</c:otherwise>
				</c:choose>
				<h4>
					<span id="from-date-outlet">
					<fmt:formatDate value="${filterForm.filters.from_date.time}" pattern="MMMM d, yyyy" /></span>
					&mdash;
					<span id="to-date-outlet"><fmt:formatDate value="${filterForm.filters.to_date.time}" pattern="MMMM d, yyyy" /></span>
					<small class="">(<a class="" href="javascript:void(0);" id="cta-toggle-filters">Hide Filters</a>)</small>
				</h4>
			</h2>
		</div>
		<div>
			<c:import url="/WEB-INF/views/web/partials/message.jsp" />
			<c:import url="/WEB-INF/views/web/partials/reports/filters.jsp" />
		</div>
	</div>


	<table id="report_list" class="table-report">
		<thead>
		<tr>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>ID</th>
			<th style="min-width:250px;"><i class="reports_icon icon-sort icon-small"></i>Title</th>
			<c:if test="${filterForm.budgetReport}">
				<th style="min-width:250px;"><i class="reports_icon icon-sort icon-small"></i>Create Date</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Type</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Amount</th>
				<th style="min-width:250px;"><i class="reports_icon icon-sort icon-small"></i>Note</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Approval Status</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Approve Date</th>
				<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Approver name</th>
			</c:if>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Internal Owner</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Client</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Worker First Name</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Worker Last Name</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Status</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Labels</th>
			<th style="min-width:125px;"><i class="reports_icon icon-sort icon-small"></i>Address One</th>
			<th style="min-width:125px;"><i class="reports_icon icon-sort icon-small"></i>Address Two</th>
			<th style="min-width:125px;"><i class="reports_icon icon-sort icon-small"></i>City</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>State</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Postal Code</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Country</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Latitude</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Longitude</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Date Sent</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Month Sent</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Year Sent</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Assignment Window Start</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Assignment Window End</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Scheduled Time</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Date Completed</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Month Completed</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Year Completed</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Date Closed</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Month Closed</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Year Closed</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Assignment Cost</th>

			<c:choose>
				<c:when test="${filterForm.buyerReport}">
					<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Transaction Fee</th>
					<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Pending Approval Cost With Fee</th>
					<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Total Cost</th>
				</c:when>
				<c:otherwise>
					<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Sales Tax Flag</th>
					<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Sales Tax Rate</th>
					<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Taxes Due</th>
				</c:otherwise>
			</c:choose>

			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Pay Terms</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Payment Due Date</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Hours Budgeted</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Hours Worked</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Invoice</th>
			<th nowrap="nowrap"><i class="reports_icon icon-sort icon-small"></i>Bundle ID</th>

			<c:if test="${not empty customFields}">
				<c:forEach var="field" items="${customFields}">
					<th nowrap="nowrap"><c:out value="${field.fieldName}" /></th>
				</c:forEach>
			</c:if>
		</tr>
		</thead>
		<tbody></tbody>
	</table>
</wm:app>
