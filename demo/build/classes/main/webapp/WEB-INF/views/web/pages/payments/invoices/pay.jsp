<%--TODO - not a page - move to partial--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:import url="/WEB-INF/views/web/partials/message.jsp" />

<form action="/payments/invoices/pay-confirm" id="form_pay_invoices" class="form-stacked" method="post">
	<wm-csrf:csrfToken />
	<c:forEach var="id" items="${invoiceIds}">
		<input type="hidden" name="ids[]" value="${id}" />
	</c:forEach>

		<c:choose>
			<c:when test="${isBundled}">
				<p>
					You are about to pay <c:out value="${fn:length(invoices)}" /> <c:out value="${wmfmt:pluralize('bundle', fn:length(invoices))}" /> (<fmt:formatNumber value="${not empty totalDue ? totalDue : 0}" currencySymbol="$" type="currency"/>).
					Click <em>Pay</em> to continue and pay the <c:out value="${wmfmt:pluralize('bundle', fn:length(invoices))}" /> or <em>Cancel</em> to return to the payables listing.
				</p>
			</c:when>
			<c:otherwise>
				<p>
					You are about to pay <c:out value="${fn:length(invoices)}" /> <c:out value="${wmfmt:pluralize('invoice', fn:length(invoices))}" /> (<fmt:formatNumber value="${not empty totalDue ? totalDue : 0}" currencySymbol="$" type="currency"/>).
					Click <em>Pay</em> to continue and pay the <c:out value="${wmfmt:pluralize('invoice', fn:length(invoices))}" /> or <em>Cancel</em> to return to the invoices listing.
				</p>
			</c:otherwise>
		</c:choose>
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
</form>