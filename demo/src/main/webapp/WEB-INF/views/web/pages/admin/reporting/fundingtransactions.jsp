<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin>

<c:import url="/breadcrumb">
	<c:param name="pageId" value="adminReportingFundingTransactions" />
	<c:param name="admin" value="true" />
</c:import>

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<form method="get" enctype="multipart/form-data">
		<input type="text" id="fromDate" name="fromDate" class="span2"  placeholder="MM/DD/YYYY" > to
		<input type="text" id="toDate" name="toDate" class="span2"  placeholder="MM/DD/YYYY">
		<input type="submit" value="Run" class="button" />
	</form>

	<table id="transaction_list" class="table table-striped">
		<thead>
			<tr>
				<th>Company Name</th>
				<th>Amount</th>
				<th>Type</th>
				<th>Date</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="value" items="${funding_transactions}">
				<tr>
					<td><c:out value="${value.accountRegister.company.effectiveName}"/></td>
					<td><fmt:formatNumber value="${value.amount}" currencySymbol="$" type="currency"/></td>
					<td><c:out value="${value.registerTransactionType.description}"/></td>
					<td><c:out value="${value.createdOn.time}"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<script>
	$(function() {
		$( "#fromDate" ).datepicker();
		$( "#toDate" ).datepicker();
	});
</script>

</wm:admin>
