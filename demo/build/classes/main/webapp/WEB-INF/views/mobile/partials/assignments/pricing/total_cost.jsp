<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<li class="total-cost">
	<div>Total Cost: </div>
	<div class="currency"><fmt:formatNumber value="${work.payment.totalCost}" currencySymbol="$" type="currency"/></div>
</li>