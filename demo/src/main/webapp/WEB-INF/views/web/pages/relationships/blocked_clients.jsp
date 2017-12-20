<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Blocked Clients" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Blocked Clients" webpackScript="settings">

	<script>
		var config = {
			mode: 'manage-blocked'
		};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp" />
		</div>
		<div class="content">
			<div class="inner-container">
				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
					<c:param name="containerId" value="dynamic_messages" />
				</c:import>
				<div class="page-header">
					<h3>Blocked Clients</h3>
				</div>
				<c:choose>
					<c:when test="${not empty blocked_clients}">
						<table id ="blocked_clients">
							<thead>
								<tr>
									<th>Client Name</th>
									<th>Date Blocked</th>
									<th>Blocked By</th>
									<th>Actions</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach var="item" items="${blocked_clients}">
								<tr>
									<td>${item.blockedCompany.company.name}</td>
									<td>
										<fmt:formatDate value="${item.modifiedOn.time}" type="both" dateStyle="long" pattern="MMM dd, YYYY, hh:mm a" timeZone="${currentUser.timeZoneId}" />
									</td>
									<td>${item.user.firstName} ${item.user.lastName}</td>
									<td><a class="unblock" href="javascript:void(0);" data-isresource="false" data-id="${item.blockedCompany.company.id}">Unblock</a></td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</c:when>
					<c:otherwise>
						<div class="alert">You currently have no blocked clients.</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</wm:app>
