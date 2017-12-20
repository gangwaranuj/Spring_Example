<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<h4>Money</h4>
<table>
	<thead>
		<tr>
			<th>On Account</th>
			<th>Available to Spend</th>
			<th>In Progress</th>
			<th>Avail to Withdraw</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td><fmt:formatNumber value="${requestScope.money_summary.total}" currencySymbol="$" type="currency"/></td>
			<td><fmt:formatNumber value="${requestScope.money_summary.available}" currencySymbol="$" type="currency"/></td>
			<td><fmt:formatNumber value="${requestScope.money_summary.inProgress}" currencySymbol="$" type="currency"/></td>
			<td><fmt:formatNumber value="${requestScope.money_summary.earnedAvailable}" currencySymbol="$" type="currency"/></td>
		</tr>
	</tbody>
</table>
