<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/reschedule/${work.workNumber}" id="reschedule-work" class="form-stacked" method="POST">
	<wm-csrf:csrfToken />
	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="negotiation_messages"/>
	</c:import>

	<c:import url="/WEB-INF/views/web/partials/assignments/reschedule_form.jsp" />

	<div class="clearfix">
		<label>Note</label>
		<div class="input">
			<textarea name="notes" style="width:90%;"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" id="reschedule-submit" class="button">Update</button>
	</div>

</form>
