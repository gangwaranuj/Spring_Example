<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>


<style type="text/css">
	.span3 {
		width: 100px;
	}
</style>

<form:form modelAttribute="form" action="/assignments/edit_price/${work.workNumber}" method="post" name="form" id="form_price" cssClass="form-horizontal">
	<wm-csrf:csrfToken />
	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/> <%-- TODO: use this? --%>

	<div class="message_container"></div>

	<div id="edit-assignment-pricing">
		<div id="price-container">
			<input id="work-fee" value="${wmfmt:escapeJavaScript(workFee / 100)}" type="hidden" />
			<input id="pricing-type" value="${wmfmt:escapeJavaScript(assignment_pricing_type)}" type="hidden" />
			<c:import url="/WEB-INF/views/web/partials/assignments/pricing.jsp"/>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Reprice Work</button>
	</div>
</form:form>
