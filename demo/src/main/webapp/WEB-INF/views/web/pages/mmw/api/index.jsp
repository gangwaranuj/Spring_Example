<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="API Access" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="API Access">

<div class="row_wide_sidebar_left">
	<div class="sidebar">
		<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
	</div>

	<div class="content">
		<div class="inner-container">
			<div class="page-header clear">
				<h3>API Access</h3>
			</div>
			<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
				<c:param name="bundle" value="${bundle}"/>
			</c:import>


			<div class="alert alert-info">
				<p>Work Market has developed an API for creating assignments, retrieving assignment updates, and viewing
					assignment details. Generate your API key to start working with the Work Market API. Or review the
					documentation before getting started.</p>

				<div class="alert-actions">
					<a class="button" href="<c:url value="/mmw/api/generate"/>">Generate new API Key</a>
					<a class="button" href="http://developer.workmarket.com/">API Documentation</a>
				</div>
			</div>

			<c:if test="${not empty apiTokens}">
				<table>
					<thead>
						<tr>
							<th>Token</th>
							<th>Secret</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="item" items="${apiTokens}">
							<tr>
								<td><code class="text-info"><c:out value="${item.token}"/></code></td>
								<td><code class="text-info"><c:out value="${item.secret}"/></code></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</div>
	</div>
</div>
</wm:app>
