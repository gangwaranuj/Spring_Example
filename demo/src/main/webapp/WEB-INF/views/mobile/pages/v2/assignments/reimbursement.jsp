<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.reimbursement" scope="request"/>

<div class="wrap reimbursement-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Reimbursement" />
	</jsp:include>

	<div class="grid content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div><%--unit whole--%>

		<div class="unit whole">
			<div class="pricing-summary-container">
				<h3>Budget Details</h3>
				<c:import url="/WEB-INF/views/mobile/partials/assignments/pricing.jsp"/>
			</div>

			<form action="/mobile/assignments/reimbursement/${work.workNumber}" method="post" id="reimbursement-form">
				<wm-csrf:csrfToken />

				<input type="hidden" name="price_negotiation" value="1" />
				<input type="hidden" name="id" value="${work.workNumber}" />

				<label for="additional_expenses">Requested reimbursement amount: </label>
				<input type="text" name="additional_expenses" id="additional_expenses"  placeholder="$">

				<span>Reason for request:</span>
				<textarea class="reimbursement-note" name="note"></textarea>

				<input id="submit-reimbursement"  class="spin" type="submit" name="submit" value="Submit" />
			</form>
		</div><%--unit--%>
	</div><%--grid--%>
</div><%--wrap--%>
