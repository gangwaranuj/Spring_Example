<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="financial">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/kpis/kpis_sidebar.jsp" />
</div>

<div class="content">
	<div class="row">
		<div class="span12">
			<h2>Financial KPIs</h2>
			<form method="get" enctype="multipart/form-data">
				<div class="row">
					<div class="span4">
						<h4>Industry</h4>
						<input type="checkbox" value="true" id="industry" name="industry">Break out by industry
					</div>
					<div class="span6">
						<h4>Funding Type Filter</h4>
						<div class="row">
							<div class="span4">
								<select id="fundingType"  name="fundingType">
									<option value="">Any</option>
									<option value="cash">Cash</option>
									<option value="terms">Terms</option>
								</select>
							</div>
							<div class="span2">
								<input type="submit" value="Go" class="button" />
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>

	<h4>Throughput (Monthly)</h4>
	<table id="monthlyThroughput" class="table table-striped">
		<thead>
			<tr>
			<c:forEach items="${monthList}" var="month">
				<th><c:out value="${month}" /></th>
			</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Throughput (Weekly)</h4>
	<table id="weeklyThroughput" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${weekList}" var="week">
					<th><c:out value="${week}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Throughput (Daily)</h4>
	<table id="dailyThroughput" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${dayList}" var="day">
					<th><c:out value="${day}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Transaction Fees (Monthly)</h4>
	<table id="monthlyTransactionFees" class="table table-striped">
		<thead>
			<tr>
				<c:forEach items="${monthList}" var="month">
					<th><c:out value="${month}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Transaction Fees (Weekly)</h4>
	<table id="weeklyTransactionFees" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${weekList}" var="week">
					<th><c:out value="${week}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Transaction Fees (Daily)</h4>
	<table id="dailyTransactionFees" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${dayList}" var="day">
					<th><c:out value="${day}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Assignments Created (Monthly)</h4>
	<table id="monthlyAssignmentsCreated" class="table table-striped">
		<thead>
			<tr>
				<c:forEach items="${monthList}" var="month">
					<th><c:out value="${month}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Assignments Created (Weekly)</h4>
	<table id="weeklyAssignmentsCreated" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${weekList}" var="week">
					<th><c:out value="${week}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Assignments Created (Daily)</h4>
	<table id="dailyAssignmentsCreated" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${dayList}" var="day">
					<th><c:out value="${day}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Avg Assignments Value (Monthly)</h4>
	<table id="monthlyAvgAssignmentsValue" class="table table-striped">
		<thead>
			<tr>
				<c:forEach items="${monthList}" var="month">
					<th><c:out value="${month}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Avg Assignments Value (Weekly)</h4>
	<table id="weeklyAvgAssignmentsValue" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${weekList}" var="week">
					<th><c:out value="${week}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Avg Assignments Value (Daily)</h4>
	<table id="dailyAvgAssignmentsValue" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${dayList}" var="day">
					<th><c:out value="${day}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<!-- No data
	<h4>Available Buyer Funds (Monthly)</h4>
	<table id="monthlyAvailableBuyerFunds" class="table table-striped">
		<thead>
			<tr>
				<c:forEach items="${monthList}" var="month">
					<th><c:out value="${month}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
	-->

	<h4>Withdrawable Cash (Monthly)</h4>
	<table id="monthlyWithdrawableCash" class="table table-striped">
		<thead>
			<tr>
				<c:forEach items="${monthList}" var="month">
					<th><c:out value="${month}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Total Cash (Monthly)</h4>
	<table id="monthlyTotalCash" class="table table-striped">
		<thead>
			<tr>
				<c:forEach items="${monthList}" var="month">
					<th><c:out value="${month}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<script type="text/javascript">
	$(wm.pages.admin.kpis.financial('${wmfmt:escapeJavaScript(fundingType)}'));
</script>

</wm:admin>
