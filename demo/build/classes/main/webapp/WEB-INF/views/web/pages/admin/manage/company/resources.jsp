<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Resources" bodyclass="manage-company">

<c:set var="pageScript" value="wm.pages.admin.manage.company.resources" scope="request" />
<c:set var="pageScriptParams" value="${requestScope.company.id}" scope="request" />

<div class="sidebar admin">
	<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
</div>

<div id="company-page" class="content">
	<c:import url="/WEB-INF/views/web/partials/admin/manage/company/header.jsp"/>
	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="dynamic_messages" />
	</c:import>

	<ul class="nav nav-tabs">
		<li<c:if test="${requestScope.type eq 'employees'}"> class="active"</c:if>><a href="/admin/manage/company/resources/${requestScope.company.id}?type=employees">Employees</a></li>
		<li<c:if test="${requestScope.type eq 'contractors'}"> class="active"</c:if>><a href="/admin/manage/company/resources/${requestScope.company.id}?type=contractors">Contractors</a></li>
	</ul>

	<c:choose>
		<c:when test="${requestScope.type eq 'employees'}">
			<table id="users_list" class="table table-striped">
				<thead>
					<tr>
						<th>Name</th>
						<th>Role</th>
						<th>Last Login</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="4" class="dataTables_empty">Loading data from server</td>
					</tr>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<table id="workers_list" class="table table-striped">
				<thead>
					<tr>
						<th>Name</th>
						<th>Company</th>
						<th>Lane</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="6" class="dataTables_empty">Loading data from server</td>
					</tr>
				</tbody>
			</table>
	</c:otherwise>
	</c:choose>
</div>

<div class="dn">
	<div id="change_lane_popup">
		<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
			<c:param name="containerId" value="dynamic_messages_lane_change"/>
		</c:import>

		<form action="/admin/manage/company/change_lane" method="post" id="form_change_lane">
			<input type="hidden" id="user_id" name="user_id" />
			<input type="hidden" id="lane_id" name="lane_id"/>
			<input type="hidden" id="company_id" name="company_id" value="${requestScope.company.id}"/>

			<p>Are you sure you would like to change this contractor's relationship to <strong class="strong" id="change_lane_to">LANE #</strong></p>

			<div class="wm-action-container">
				<a class="button" onclick="javascript:$.colorbox.close(); return false;">Go Back</a>
				<button type="submit" class="button">Yes</button>
			</div>
		</form>
	</div>
</div>

</wm:admin>
