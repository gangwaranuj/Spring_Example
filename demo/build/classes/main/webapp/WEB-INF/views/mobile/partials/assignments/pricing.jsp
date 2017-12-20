<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="showBudget" value="true" scope="request"/>

<ul class="pricing-summary">
	<c:choose>
		<c:when test="${work.pricing.id == PricingStrategyType.INTERNAL}">
			<li>Internal</li>
		</c:when>
		<c:otherwise>
			<jsp:include page="/WEB-INF/views/mobile/partials/assignments/pricing/base_pricing.jsp" />
			<jsp:include page="/WEB-INF/views/mobile/partials/assignments/pricing/additional_expenses.jsp" />
			<jsp:include page="/WEB-INF/views/mobile/partials/assignments/pricing/bonus.jsp" />
			<jsp:include page="/WEB-INF/views/mobile/partials/assignments/pricing/budgeted_spend.jsp" />
			<c:if test="${isAdmin}">
				<jsp:include page="/WEB-INF/views/mobile/partials/assignments/pricing/transaction_fee.jsp" />
				<jsp:include page="/WEB-INF/views/mobile/partials/assignments/pricing/total_cost.jsp" />
			</c:if>
		</c:otherwise>
	</c:choose>
</ul>
