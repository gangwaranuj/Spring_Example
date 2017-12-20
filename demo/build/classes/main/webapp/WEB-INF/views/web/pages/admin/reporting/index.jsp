<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="KPIs">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/kpis/kpis_sidebar.jsp" />
</div>

<div class="content">

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<h5>Date:
		<fmt:formatDate value="${summary.createdOn.time}" pattern="MM/dd/yyyy"/>
	</h5>

	<div class="row">
		<div class="span10">
			<table class="table table-striped">
				<thead>
					<tr>
						<th colspan="2">Assignments</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Drafts</td>
						<td><c:out value="${summary.assignments}"/></td>
					</tr>
					<tr>
						<td>Sent</td>
						<td><c:out value="${summary.routed}"/></td>
					</tr>
					<tr>
						<td>Void</td>
						<td><c:out value="${summary.voidAssignments}"/></td>
					</tr>
					<tr>
						<td>Completed</td>
						<td><c:out value="${summary.completed}"/></td>
					</tr>
					<tr>
						<td>Closed</td>
						<td><c:out value="${summary.closedAssignments}"/></td>
					</tr>
					<tr>
						<td>Cancelled</td>
						<td><c:out value="${summary.cancelledAssignments}"/></td>
					</tr>
					<tr>
						<td>Unique Buyers</td>
						<td><a href="<c:url value="/admin/reporting/unique_buyers/${summary.id}"/>"><c:out value="${summary.uniqueCreators}"/></a></td>
					</tr>
				</tbody>
			</table>

			<table class="table table-striped">
				<thead>
					<tr>
						<th colspan="2">Supply</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>New Users</td>
						<td><c:out value="${summary.newUsers}"/></td>
					</tr>
					<tr>
						<td>Drug Tests</td>
						<td><c:out value="${summary.drugTests}"/></td>
					</tr>
					<tr>
						<td>Background Checks</td>
						<td><c:out value="${summary.backgroundChecks}"/></td>
					</tr>
					<tr>
						<td>Public Talent Pools</td>
						<td><c:out value="${summary.publicGroups}"/></td>
					</tr>
					<tr>
						<td>Invitation Only Talent Pools</td>
						<td><c:out value="${summary.inviteOnlyGroups}"/></td>
					</tr>
					<tr>
						<td>Private Talent Pools</td>
						<td><c:out value="${summary.privateGroups}"/></td>
					</tr>
				</tbody>
			</table>

			<table class="table table-striped">
				<thead>
					<tr>
						<th colspan="2">Features</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Invitations</td>
						<td><c:out value="${summary.invitations}"/></td>
					</tr>
					<tr>
						<td>Campaigns</td>
						<td><c:out value="${summary.campaigns}"/></td>
					</tr>
				</tbody>
			</table>

			<table class="table table-striped">
				<thead>
					<tr>
						<th colspan="2">Money</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Cash On Platform</td>
						<td><fmt:formatNumber value="${summary.cashOnPlatform}" currencySymbol="$" type="currency"/></td>
					</tr>
					<tr>
						<td>Payments for closed assignments</td>
						<td><fmt:formatNumber value="${summary.totalAssignmentCost}" currencySymbol="$" type="currency"/></td>
					</tr>
					<tr>
						<td>Assignment Fees</td>
						<td><fmt:formatNumber value="${summary.totalFees}" currencySymbol="$" type="currency"/></td>
					</tr>
					<tr>
						<td>Terms Exposure</td>
						<td><fmt:formatNumber value="${summary.totalMoneyExposedOnTerms}" currencySymbol="$" type="currency"/></td>
					</tr>
                    <tr>
                        <td>Terms Expired</td>
                        <td><fmt:formatNumber value="${summary.termsExpired}" currencySymbol="$" type="currency"/></td>
                    </tr>
                    <tr>
                        <td>Terms Overdue</td>
                        <td><fmt:formatNumber value="${summary.termsOverdue}" currencySymbol="$" type="currency"/></td>
                    </tr>
					<tr>
						<td>Routed</td>
						<td><a href="<c:url value="/admin/reporting/routed_detail/${summary.id}"/>"><fmt:formatNumber value="${summary.totalRoutedToday}" currencySymbol="$" type="currency"/></a></td>
					</tr>
					<tr>
						<td>Drafts Created Today (Potential revenue)</td>
						<td><fmt:formatNumber value="${summary.draftsCreated}" currencySymbol="$" type="currency"/></td>
					</tr>
				</tbody>
			</table>
		</div>

		<div class="span5">
			<table class="table table-striped" id="summary-list">
				<thead>
					<tr>
						<th>Summary Date</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach var="value" items="${summaries}">
					<tr>
						<td>
							<a href="<c:url value="/admin/reporting/index/${value.id}"/>"><fmt:formatDate value="${value.createdOn.time}" pattern="MM/dd/yyyy"/></a>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(wm.pages.admin.reporting);
</script>

</wm:admin>
