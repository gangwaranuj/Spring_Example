<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Report" bodyclass="manage-company">

<c:set var="barWidth" value="40" />

<div class="sidebar admin">
	<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
</div>

<div class="content report">
	<c:import url="/WEB-INF/views/web/partials/admin/manage/company/header.jsp"/>

	<form:form modelAttribute="filterForm" action="/admin/manage/company/report/${requestScope.company.id}" method="get">
		<strong>Date Range</strong>
		<form:input path="fromDate" id="fromDate" class="span2" size="10" maxlength="10" />
		to
		<form:input path="toDate" id="toDate" class="span2" size="10" maxlength="10" />
		<button type="submit" class="button">Run</button>
	</form:form>

	<h2 class="name">
		<c:out value="${requestScope.company.name}"/>
		<small>(Activated <fmt:formatDate value="${requestScope.company.createdOn.time}" pattern="MM/dd/yyyy"/>)</small>
	</h2>

	<p><small>Report Range <fmt:formatDate value="${requestScope.filterForm.fromDate.time}" pattern="MM/yyyy"/> to <fmt:formatDate value="${requestScope.filterForm.toDate.time}" pattern="MM/yyyy"/></small></p>

	<div class="kpi-data">
		<h5>Top users</h5>

		<table class="simple table table-stripped">
			<thead>
				<tr>
					<th>Name</th>
					<th>Email</th>
					<th>Rating</th>
					<th>Sent</th>
					<th>In Progress</th>
					<th>Approved</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="topUser" items="${topUsers}">
				<tr>
					<td><a href="/admin/manage/profiles/index/<c:out value='${topUser.userNumber}' />"><c:out value="${topUser.firstName}" /> <c:out value="${topUser.lastName}" /></a></td>
					<td><c:out value="${topUser.email}" /></td>
					<td>${wmfn:ratingStars(topUser.rating)}</td>
					<td><c:out value="${topUser.sentAssignments}" /></td>
					<td><c:out value="${topUser.activeAssignments}" /></td>
					<td><c:out value="${topUser.closedAssignments}" /></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>

	<c:forEach var="report" items="${reports}">
		<div class="kpi-data span8">
			<h5><c:out value="${report.key}" /></h5>

			<div class="graph">
				<div class="x-axis">
					<c:forEach var="point" items="${report.value.chartData}" varStatus="status">
						<div class="tick" data-timestamp="${point.x}" style="left:${(barWidth - 11) * status.index}px;">
							${wmfmt:formatMillis("MM/yy", point.x)}
						</div>
					</c:forEach>
				</div>

				<div class="data">
					<c:set var="max" value="0" />
					<c:forEach var="point" items="${report.value.chartData}">
						<c:if test="${point.y > max}">
							<c:set var="max" value="${point.y}" />
						</c:if>
					</c:forEach>

					<c:forEach var="point" items="${report.value.chartData}" varStatus="status">
						<div class="bar" data-x="${point.x}" data-y="${point.y}" style="left:${2 + (barWidth - 11) * status.index}px; height:${point.y / max * 90}%;">
							<span class="value" >
								<fmt:formatNumber value="${point.y}" maxFractionDigits="0" />
							</span>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</c:forEach>
</div>

<script type="text/javascript">
	$(wm.pages.admin.manage.company.report());
</script>

</wm:admin>
