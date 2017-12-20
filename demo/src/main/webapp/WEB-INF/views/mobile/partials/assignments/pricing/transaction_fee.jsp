<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<li class="transaction-fee">
	<div>Transaction Fee (<c:out value="${work.payment.buyerFeePercentage}"/>%): </div>
	<div class="currency"><fmt:formatNumber value="${work.payment.buyerFee}" currencySymbol="$" type="currency"/></div>
</li>
