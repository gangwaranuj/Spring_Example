<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/edit_support_contact/${work.workNumber}" id='form_edit_support_contact' class='form-stacked' method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="container_id" value="negotiation_messages"/>
	</c:import>

	<div class="clearfix">
		<div class="input">
			<select name="support_contact" id='support-contact-dropdown'>
				<c:forEach items="${users}" var="user">
					<option value="${user.key}" <c:out
							value="${(user.key == work.buyerSupportUser.id) ? 'selected=selected' : ''}"/>  ><c:out value="${user.value}" /></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Update</button>
	</div>
</form>
