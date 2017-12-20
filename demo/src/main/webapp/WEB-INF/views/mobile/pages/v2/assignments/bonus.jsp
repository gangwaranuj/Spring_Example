<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div class="wrap bonus-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="${title}" />
	</jsp:include>

	<div class="grid content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div><%--unit whole--%>

		<div class="unit whole">
			<div class="pricing-summary-container">
				<c:set var="showBudget" value="true" scope="request"/>
				<h3>Budget Details</h3>
				<c:import url="/WEB-INF/views/mobile/partials/assignments/pricing.jsp"/>
			</div>

			<form action="/mobile/assignments/bonus/${work.workNumber}" id="bonusForm" method="post">
				<wm-csrf:csrfToken />
				<input type="hidden" name="price_negotiation" value="1" id="price_negotiation" />
				<input type="hidden" name="id" value="${work.workNumber}" />
				<label for="bonus">Requested bonus: </label>
				<input type="text" name="bonus" id="bonus" value="" placeholder="$"/>

				<span>Reason for request:</span>
				<textarea name="note" id="negotiation_note"></textarea>

				<input id="submit-bonus" class="spin" type="submit" name="submit" value="Submit"/>
			</form>
		</div>
	</div>
</div>
