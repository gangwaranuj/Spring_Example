<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<li class="budgeted-spend">
	<div>Budgeted Spend: </div>
	<div class="currency"><fmt:formatNumber value="${isAdmin ? work.pricing.maxSpendLimit : work.pricing.maxSpendLimit - work.pricing.additionalExpenses}" currencySymbol="$" type="currency"/></div>
</li>