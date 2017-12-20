<%--TODO: not a page - move to partial--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
	<c:param name="bundle" value="${bundle}"/>
</c:import>

<form action="/payments/statements/pay/${id}" method="POST" class="form-stacked">
	<wm-csrf:csrfToken />
	<input type="hidden" name="statementId" value="${id}" />
	<p>You are about to pay the statement for the period
			${wmfmt:formatCalendar('MM/d/yyyy', statement.periodStartDate)}
		&mdash;
			${wmfmt:formatCalendar('MM/d/yyyy', statement.periodEndDate)}

		(<fmt:formatNumber value="${statement.remainingBalance}" currencySymbol="$" type="currency"/>).
		Click <em>Pay</em> to continue and pay the invoice or <em>Cancel</em> to return to
		the invoices listing.</p>

	<c:if test="${doesCompanyHaveReservedFundsEnabledProject}">
		<table>
			<thead>
			<tr>
				<td>Cash Account</td>
				<td>Cash Balance</td>
				<td>Number of Invoices</td>
				<td>Invoice Balance</td>
			</tr>
			</thead>
			<tbody>
			<c:if test= "${fn:length(general_invoices) > 0}">
				<tr>
					<td>Unreserved Cash</td>
					<td><fmt:formatNumber value="${general_cash}" currencySymbol="$" type="currency"/></td>
					<td><c:out value="${fn:length(general_invoices)}" /></td>
					<td><fmt:formatNumber value="${not empty general_totalDue ? general_totalDue : 0}" currencySymbol="$" type="currency"/></td>
				</tr>
			</c:if>
			<c:forEach var="project" items="${project_list}">
				<c:if test="${fn:length(project.invoices) > 0}">
					<tr>
						<td><c:out value="${project.project.name}" /></td>
						<td><fmt:formatNumber value="${project.project.reservedFunds}" currencySymbol="$" type="currency"/></td>
						<td>${fn:length(project.invoices)}</td>
						<td><fmt:formatNumber value="${project.sumOfInvoices}" currencySymbol="$" type="currency"/></td>
					</tr>
				</c:if>
			</c:forEach>
			</tbody>
		</table>
	</c:if>

	<div class="wm-action-container">
		<button class="button">Pay</button>
	</div>
</form>
