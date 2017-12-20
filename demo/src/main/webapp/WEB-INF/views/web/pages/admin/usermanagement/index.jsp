<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Employee Management">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<h1>Employee Management</h1>
	<p>Add / Modify / Delete admin level employees</p>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<form class="form-inline">
		<input type="hidden" name="user_id" value="${user_id}" id="user_id">
		<label for="user_fullname">
			User: <input type="text" name="user_fullname" value="<c:out value="${user_fullname}" />" id="user_fullname"/>
		</label>
		<a id="add-contractor" href="#" class="button">Give Admin Access To Somebody</a>
		<span class="help-block">
			<span id="selected_user" class="dn"></span>
		</span>
	</form>

	<div class="tabbable">
		<ul class="nav nav-tabs">
			<li class="active"><a href="#employees" data-toggle="tab">Employees</a></li>
			<li><a href="#contractors" data-toggle="tab">Contractors</a></li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane active" id="employees">
				<p>This is a list that are listed as workmarket staff in the live site (i.e. have workmarket set as their user's company).</p>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Email</th>
							<th>Email Confirmed</th>
							<th>Roles</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="user" items="${user_info}">
							<tr>
								<td><c:out value="${user.firstName}" /></td>
								<td><c:out value="${user.lastName}" /></td>
								<td><c:out value="${user.email}" /></td>
								<td><c:out value="${user.emailConfirmed}" /></td>
								<td>
									<c:forEach var="user_role" items="${user.roles}" varStatus="status">
										<c:out value="${user_role}"/>
										<c:if test="${!status.last || eft}">,</c:if>
									</c:forEach>
								</td>
								<td><a href="/admin/usermanagement/edit?id=<c:out value='${user.id}'/>">Edit</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="tab-pane" id="contractors">
				<p>List of contractors (not @workmarket.com email addresses) with access to our admin site & tools (i.e. "internal" role).</p>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Email</th>
							<th>Email Confirmed</th>
							<th>Roles</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="contractor" items="${contractors}">
							<tr>
								<td><c:out value="${contractor.firstName}" /></td>
								<td><c:out value="${contractor.lastName}" /></td>
								<td><c:out value="${contractor.email}" /></td>
								<td><c:out value="${contractor.emailConfirmed}" /></td>
								<td>
									<c:forEach var="contractor_role" items="${contractor.roles}" varStatus="status">
										<c:out value="${contractor_role}"/>
										<c:if test="${!status.last || eft}">,</c:if>
									</c:forEach>
								</td>
								<td><a href="/admin/usermanagement/edit?id=<c:out value='${contractor.id}'/>">Edit</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(wm.pages.admin.usermanagement.index());
</script>

</wm:admin>
