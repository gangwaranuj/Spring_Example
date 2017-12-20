<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.budget_increase" scope="request"/>

<div class="wrap budget-increase-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Budget Increase" />
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

			<form action="/mobile/assignments/budgetincrease/${work.workNumber}" id="budgetIncreaseForm" method="post">
				<wm-csrf:csrfToken />

				<input type="hidden" name="price_negotiation" value="1" id="price_negotiation"/>
				<input type="hidden" name="id" value="${work.workNumber}"/>

				<div>
					<c:choose>
						<c:when test="${work.pricing.id == PricingStrategyType['FLAT']}">
							<label for="flat_price">New total budget:</label>
							<input class="new-total-budget" type="text" name="flat_price" placeholder="$">
						</c:when>
						<c:when test="${work.pricing.id == PricingStrategyType['PER_HOUR']}">
							<label for="max_number_of_hours">New max # of hours:</label>
							<input class="new-number-hours" type="text" name="max_number_of_hours" placeholder="Hours">
						</c:when>
						<c:when test="${work.pricing.id == PricingStrategyType['PER_UNIT']}">
							<label for="max_number_of_units">New max # of units:</label>
							<input class="new-number-units" type="text" name="max_number_of_units" placeholder="Units">
						</c:when>
						<c:when test="${work.pricing.id == PricingStrategyType['BLENDED_PER_HOUR']}">
							<label for="max_blended_number_of_hours">New max # of total hours:</label>
							<input class="new-number-total-hours" type="text" name="max_blended_number_of_hours" placeholder="Hours">
						</c:when>
					</c:choose>
				</div>

				<span>Reason for request:</span>
				<textarea name="note" id="negotiation_note"></textarea>

				<input id="submit-budget-increase" class="spin submit-the-negotiation" type="submit" name="submit" value="Submit"/>
			</form>
		</div><%--unit--%>
	</div><%--grid--%>
</div><%--wrap--%>
