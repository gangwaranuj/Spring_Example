<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="New" webpackScript="admin">

	<script>
		var config = {
			mode: 'userAdd'
		}
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
	</div>

	<div class="content">
		<h1 class="strong">Create an Employee</h1>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}"/>
		</c:import>

		<form id="addEmployeeForm" action="/admin/usermanagement/new" method="POST" class="form-horizontal">
			<wm-csrf:csrfToken />

			<fieldset>
				<div class="control-group">
					<label for="first_name" class="control-label">First Name</label>
					<div class="controls">
						<input type="text" size="30" name="first_name" id="first_name" class="span6" value="<c:out value="${first_name}" />">
					</div>
				</div>

				<div class="control-group">
					<label for="last_name" class="control-label">Last Name</label>
					<div class="controls">
						<input type="text" size="30" name="last_name" id="last_name" class="span6" value="<c:out value="${last_name}" />">
					</div>
				</div>

				<div class="control-group">
					<label for="email" class="control-label">Email</label>
					<div class="controls">
						<input type="text" size="30" name="email" id="email" class="span6" value="<c:out value="${email}" />">
					</div>
				</div>

				<div class="control-group">
					<label for="new_password" class="control-label">Password</label>
					<div class="controls">
						<input type="password" id="new_password" isize="30" name="password" value="" class="span3">
					</div>
				</div>

				<div class="control-group">
					<label for="password_confirm" class="control-label">Password Confirm</label>
					<div class="controls">
						<input type="password" id="password_confirm" size="30" name="password_confirm" value="" class="span3">
					</div>
				</div>

				<div class="control-group">
					<label for="select_roles" class="control-label">WorkMarket Access</label>
					<div class="controls">
						<select name="roles" id="select_roles" multiple='multiple'>
							<c:forEach var="role" items="${roles}">
								<option value="${role.key}"><c:out value="${role.value}"/></option>
							</c:forEach>
						</select>
					</div>
				</div>

				<div class="control-group">
					<label for="crmcheck" class="control-label">Internal Access</label>
					<div class="controls">
						<ul class="inputs-list">
							<c:forEach var="role" items="${internal_roles}">
								<li><input type="checkbox" name="internal_roles" value="${role.value}" <c:if test="${role.value == 'wm_crm'}"> id="crmcheck" </c:if> /> <c:out value="${role.value}"/></li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</fieldset>

			<div class="wm-action-container">
				<button class="button">Add User</button>
			</div>
		</form>
	</div>

</wm:admin>
