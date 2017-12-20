<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Summary">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<table id="data_list" class="table table-striped">
		<thead>
			<tr>
				<th>Company Name</th>
				<th>Average Price</th>
				<th>Total Created</th>
				<th>Total Routed</th>
				<th>Total Void</th>
				<th>Total Accepted</th>
				<th>Total Canceled</th>
				<th>Total Closed</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="value" items="${summary_buyers}">
				<tr>
					<td><a href="<c:url value='/admin/manage/company/overview/${value.companyId}'/>"><c:out value="${value.companyName}"/></a></td>
					<td><fmt:formatNumber value="${value.averageAssignmentPrice}" currencySymbol="$" type="currency"/></td>
					<td><c:out value="${value.createdAssignments}"/></td>
					<td><c:out value="${value.routedAssignments}"/></td>
					<td><c:out value="${value.voidAssignments}"/></td>
					<td><c:out value="${value.activeAssignments}"/></td>
					<td><c:out value="${value.cancelledAssignments}"/></td>
					<td><c:out value="${value.closedAssignments}"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

</wm:admin>
