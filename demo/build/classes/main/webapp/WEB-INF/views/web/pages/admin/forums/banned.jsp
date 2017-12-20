<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Banned">

<c:set var="pageScript" value="wm.pages.admin.forums.bannedUsers" scope="request" />

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

		<div class="content">
			<h1>Banned Users</h1>
			<table class="table table-striped" id="bannedUsersTable">
				<thead>
					<tr>
						<c:forEach var="label" items="${labels}">
							<th>${label}</th>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${bannedUsers}" var="bannedUser">
						<tr>
							<td colspan="7" class="dataTables_empty">Loading data from server</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

	<div class="inner-container">
		<h3>Ban Hammer</h3>
		<form:form action="/admin/forums/ban" method="POST" commandName="form" name="form" id="ban_user_form">

			<wm-csrf:csrfToken />

			<label for="user_id">User: </label>
			<input type="text" name="user_fullname" value="<c:out value="${user_fullname}" />" id="user_fullname"/>
			<form:label path="reason">Reason: </form:label>
			<form:textarea path="reason" class="input-block-level"/>
			<form:hidden path="bannedUserEmail" id="user_id" value="${user_id}"/>

			<button type="submit" class="button">Ban</button>
		</form:form>
	</div>
</div>

<script id="tmpl-action" type="text/template" charset="utf-8">
	<a class="admin-forums-unban" href="javascript:void(0);" id="unban{{= userId }}">Unban</a>
</script>

</wm:admin>
