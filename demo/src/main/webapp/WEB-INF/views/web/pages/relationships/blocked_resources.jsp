<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Blocked Workers" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Blocked Workers" webpackScript="settings">

	<script>
		var config = {
			mode: 'manage-blocked'
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />

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
					<h3>Blocked Workers</h3>
				</div>
				<c:choose>
					<c:when test="${not empty blocked_resources}">
						<table id="blocked_resources">
							<thead>
							<tr>
								<th>Worker Name</th>
								<th>Date Blocked</th>
								<th>Blocked By</th>
								<th>Actions</th>
							</tr>
							</thead>
							<tbody>
							<c:forEach var="item" items="${blocked_resources}">
								<tr>
									<td><a href="<c:url value="/profile/${item.id}"/>"><c:out value="${item.name}" /></a></td>
									<td>
										<fmt:formatDate value="${item.createdOn}" type="both" dateStyle="long" pattern="MMM dd, YYYY, hh:mm a" timeStyle="medium" timeZone="${currentUser.timeZoneId}" />
									</td>
									<td>${item.blockingUser}</td>
									<td><a class="unblock" href="javascript:void(0);" data-isresource="true" data-id="${item.actualId}">Unblock</a></td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</c:when>
					<c:otherwise>
						<div class="alert">You currently have no blocked workers.</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>

</wm:app>
