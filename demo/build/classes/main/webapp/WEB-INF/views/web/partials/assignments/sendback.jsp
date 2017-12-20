<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/sendback/${work.workNumber}" class="form-stacked" method="post" accept-charset="utf-8">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>
	<input type="hidden" name="redirect" value="${redirect}"/>

	<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />
	<jsp:include page="/WEB-INF/views/web/partials/general/notices_js.jsp" />

	<div class="clearfix">
		<label for="reason">Please provide as much description as possible.</label>
		<div class="input">
			<textarea id="reason" name="reason" rows="5" class="span10"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Send Back</button>
	</div>
</form>
