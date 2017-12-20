<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Agreements" bodyclass="accountSettings" webpackScript="settings">

	<script>
		var config = {
			mode: 'manageAgreements'
		};
	</script>

	<sec:authorize access="hasRole('PERMISSION_ACCESSMMW')" var="hasMmwSidebar"/>

	<c:if test="${hasMmwSidebar}">
		<div class="row_sidebar_left">

		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>
	</c:if>

	<div class="content">
		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}"/>
		</c:import>
		<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
			<c:param name="containerId" value="dynamic_messages"/>
		</c:import>
		<div class="inner-container">
			<div class="page-header">
				<a class="button pull-right" href="/agreements/add">New Agreement</a>
				<h3>Agreements</h3>
			</div>

			<table id="agreements_list">
				<thead>
				<tr>
					<th width="275">Agreement Name</th>
					<th>Updated</th>
					<th>Added By</th>
					<th width="50" class="text-center">View</th>
					<th width="50" class="text-center">Edit</th>
					<th width="50" class="text-center">Delete</th>
				</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>
	<c:if test="${hasMmwSidebar}">
		</div>
	</c:if>

</wm:app>
