<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form name="concern" action="/quickforms/concern" method="POST" id="create_quickform">
	<wm-csrf:csrfToken />
	<input type="hidden" name='id' value="${concern.id}"/>
	<input type="hidden" name='type' value="${concern.type}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="quickform_message"/>
	</c:import>

	<p>Thank you for choosing to report a concern, we love to hear from you. Please briefly describe your concern below &mdash; we&rsquo;ll review and take action immediately.</p>

	<div>
		<div class="input">
			<textarea name="content" id="concern_content" class="span8" rows="5"><c:out value="${concern.content}" /></textarea>
		</div>
	</div>

	<c:if test="${type eq 'invitation' or type eq 'campaign'}">
		<div class="clearfix">
			<label>Name</label>
			<div class="input">
				<input name="name" value="<c:out value="${concern.name}" />" id="name" maxlength="50"/>
			</div>
		</div>
		<div class="clearfix">
			<label>Email</label>
			<div class="input">
				<input name="email" value="<c:out value="${concern.email}" />" id="email" maxlength="50"/>
			</div>
		</div>
	</c:if>

	<div class="text-right form-actions">
		<button type="submit" class="button">Submit</button>
	</div>

</form>