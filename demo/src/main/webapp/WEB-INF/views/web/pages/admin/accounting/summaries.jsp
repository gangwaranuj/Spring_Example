<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Summaries">

<c:import url="/breadcrumb">
	<c:param name="pageId" value="adminAccountingSummaries" />
	<c:param name="admin" value="true" />
</c:import>

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content accounting-summaries">
	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<a class="button generate-summary" href="<c:url value="/admin/accounting/create_summary"/>">Generate summary</a>

	<table id="data_list" class="table table-striped">
		<thead>
			<tr>
				<th>Summary Date</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="value" items="${summaries}">
				<tr>
					<td>
						<a href="<c:url value="/admin/accounting/summary_detail/${value.id}" />">
							<fmt:formatDate value="${value.requestDate.time}" type="both" dateStyle="medium" timeStyle="medium" timeZone="${currentUser.timeZoneId}" />
						</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

</wm:admin>
