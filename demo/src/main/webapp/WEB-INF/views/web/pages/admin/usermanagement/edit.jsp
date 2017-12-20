<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Edit" webpackScript="admin">

	<script>
		var config = {
			mode: 'userEdit'
		};
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
	</div>

	<div class="content">
		<h1 class="strong">Edit Employee</h1>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}"/>
		</c:import>

		<form id="editform" action="/admin/usermanagement/editsave" method="POST" class="form-horizontal">
			<wm-csrf:csrfToken />
			<input type="hidden" name="Action" value="save" id="submitaction"/>
			<input type="hidden" name="id" value="${editform.id}"/>

			<fieldset>
				<div class="control-group">
					<label for="first_name" class="control-label">First Name</label>
					<div class="controls">
						<input type="text" size="30" name="first_name" id="first_name" class="span6" value="${editform.first_name}">
					</div>
				</div>

				<div class="control-group">
					<label for="last_name" class="control-label">Last Name</label>
					<div class="controls">
						<input type="text" size="30" name="last_name" id="last_name" class="span6" value="${editform.last_name}">
					</div>
				</div>

				<div class="control-group">
					<label for="email" class="control-label">Email</label>
					<div class="controls">
						<input type="text" size="30" name="email" id="email" class="span6" value="<c:out value="${editform.email}" />">
					</div>
				</div>

				<div class="control-group">
					<label for="password" class="control-label">Password</label>
					<div class="controls">
						<input type="password" size="30" name="password" class="span3" id="password">
					</div>
				</div>

				<div class="control-group">
					<label for="password_confirm" class="control-label">Password Confirm</label>
					<div class="controls">
						<input type="password" size="30" name="password_confirm" class="span3" id="password_confirm">
					</div>
				</div>

				<div class="control-group">
					<label for="select_roles" class="control-label">WorkMarket Access</label>
					<div class="controls">
						<select name="roles" id="select_roles" multiple='multiple'>
							<c:forEach var="role" items="${roles}">
								<c:set var="contains" value="false"/>
								<c:forEach var="aclRole" items="${user_roles}">
									<c:if test="${aclRole.key eq role.key}">
										<c:set var="contains" value="true" scope="page"/>
									</c:if>
								</c:forEach>
								<option  <c:if test="${contains}">selected="selected"</c:if> value="${role.key}" ><c:out value="${role.value}"/></option>
							</c:forEach>
						</select>
						<span class="help-block">Please select all that apply</span>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">Internal Access</label>
					<div class="controls">
						<ul class="inputs-list">
							<c:forEach var="role" items="${internal_roles}">
								<c:if test="${role.role == 'internal' || role.role == 'wm_crm'}">
									<li>
										<input type="checkbox" name="internal_roles"<c:if test="${fn:contains(editform.internal_roles, role.role)}"> checked=checked</c:if><c:if test="${role.role == 'wm_crm'}"> id="crmcheck" </c:if><c:if test="${role.role == 'internal'}"> id="internal"</c:if> value="${role.role}" />
										<c:out value="${role.role}"/>   |  <c:out value="${role.description}"/>
									</li>
								</c:if>
							</c:forEach>
						</ul>
					</div>
				</div>

				<div id="internal-rights" class="control-group">
					<label class="control-label">Admin Site Permissions</label>
					<div class="controls">
						<ul class="inputs-list">
							<c:forEach var="role" items="${internal_roles}">
								<c:if test="${role.role != 'internal' && role.role != 'wm_crm'}">
									<li>
										<input type="checkbox" name="internal_roles"<c:if test="${fn:contains(editform.internal_roles, role.role)}"> checked=checked</c:if> value="${role.role}"/>
										<c:out value="${role.role}"/>   |  <c:out value="${role.description}"/>
									</li>
								</c:if>
							</c:forEach>
						</ul>
					</div>
				</div>
			</fieldset>

			<div class="form-actions">
				<button type="submit" class="button">Save</button>
				<button type="button" class="button" id="deletesubmit">Delete User</button>
			</div>
		</form>
	</div>

</wm:admin>
