<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/edit_internal_owner/${work.workNumber}" id='form_edit_internal_owner' class='form-stacked' method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="container_id" value="negotiation_messages"/>
	</c:import>

	<div class="clearfix">
		<div class="input">
			<select name="internal_owner" id='internal_owner-dropdown'>
				<c:forEach items="${users}" var="user">
					<option value="${user.key}" <c:out
							value="${(user.key == work.buyer.id) ? 'selected=selected' : ''}"/>  ><c:out value="${user.value}" /></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Update</button>
	</div>
</form>
