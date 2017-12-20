<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Routed">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<table id="data_list" class="table table-striped">
		<tbody>
			<c:forEach var="value" items="${summary_routed}">
				<tr>
					<td><a href="<c:url value='/admin/manage/company/overview/${value[0]}'/>"><c:out value="${value[1]}"/></a></td>
					<td><fmt:formatNumber value="${value[2]}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

</wm:admin>
