<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${work.pricing.additionalExpenses > 0}">
	<li class="additional-expenses">
		<div>Approved Increase: </div>
		<div class="currency"><fmt:formatNumber value="${work.pricing.additionalExpenses}" currencySymbol="$" type="currency"/></div>
	</li>
</c:if>
