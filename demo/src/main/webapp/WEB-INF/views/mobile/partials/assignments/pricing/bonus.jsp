<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${work.pricing.bonus > 0}">
	<li class="bonus">
		<div>Bonus: </div>
		<div class="currency"><fmt:formatNumber value="${work.pricing.bonus}" currencySymbol="$" type="currency"/></div>
	</li>
</c:if>