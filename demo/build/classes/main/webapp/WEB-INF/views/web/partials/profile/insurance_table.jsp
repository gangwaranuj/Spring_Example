<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>


<c:set var="insurances" value="${(empty param.current_or_pending or param.current_or_pending == 'pending') ? unverified_insurance : current_insurance}"/>
<c:set var="heading" value="${(empty param.current_or_pending or param.current_or_pending == 'pending') ? 'Insurance Pending Verification' : 'Current Insurance'}"/>

<c:if test="${not empty insurances}">
	<div class="page-header">
		<h4>${heading}</h4>
	</div>
	<table>
		<thead>
			<tr>
				<th>Type</th>
				<th>Vendor</th>
				<th>#</th>
				<th>Coverage</th>
				<th>Issued</th>
				<th class="text-center">Delete</th>
			</tr>
		</thead>
		<c:forEach var="insurance" items="${insurances}">
			<tr>
				<td>${insurance.insurance.name}</td>
				<c:choose>
					<c:when test="${insurance.notApplicableOverride}">
						<td colspan="4">I attest that my state does not require me to carry workers compensation insurance.</td>
					</c:when>
					<c:otherwise>
						<td><c:out value="${insurance.provider}"/></td>
						<td><c:out value="${insurance.policyNumber}"/></td>
						<td><fmt:formatNumber value="${insurance.coverage}" currencySymbol="$" type="currency"/></td>
						<td>
						<c:choose>
						<c:when test="${not empty insurance.issueDate}">
						<fmt:formatDate value="${insurance.issueDate.time}" pattern="MM/yyyy" />
						</c:when>
						<c:otherwise>-</c:otherwise>
						</c:choose>
						</td>
					</c:otherwise>
				</c:choose>
				<td class="actions">
					<a href="<c:url value="/profile-edit/insuranceremove?id=${insurance.id}" />">
					<i class="wm-icon-trash icon-large muted"></i></a>
				</td>
			</tr>
		</c:forEach>
	</table>
</c:if>
