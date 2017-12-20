<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="well-b2">
	<div class="well-content">
		<h4>
			Expense Reimbursement Request
			<span class="label label-${is_active_resource ? "warning" : "success"}">
			${is_active_resource ? "Pending Approval" : "Action Required"}
			</span>
		</h4>

		<c:choose>
			<c:when test="${is_active_resource}">
				<p><c:out value="${negotiation.requestedBy.name.getFullName()}" /> requested an Expense Reimbursement of
					<fmt:formatNumber value="${negotiation.pricing.additionalExpenses}" currencySymbol="$" type="currency"/>
				</p>
			</c:when>
			<c:when test="${isAdminOrInternal}">
				<p>
					Expense Reimbursement request of <fmt:formatNumber value="${negotiation.pricing.additionalExpenses}" currencySymbol="$" type="currency"/>
					by <c:out value="${negotiation.requestedBy.name.getFullName()}" />
				</p>
				<c:if test="${not empty negotiation.note}">
					<blockquote><em><c:out value="${negotiation.note.text}"/></em></blockquote>
				</c:if>
			</c:when>
		</c:choose>


		<c:import url="/WEB-INF/views/web/partials/assignments/details/expense_display.jsp">
			<c:param name="readOnly" value="true"/>
		</c:import>

		<sec:authorize access="!principal.editPricingCustomAuth">
			<c:set var="disable" value="disabled" />
		</sec:authorize>

		<c:choose>
			<c:when test="${isAdminOrInternal}">
					<form action="/assignments/accept_negotiation/${work.workNumber}" class="wm-action-container">
						<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
						<a rel="prompt_decline_negotiation" data-negotiation-id="${negotiation.encryptedId}" class="decline-negotiation button -small" ${disable}>Decline</a>
						<button type="submit" class="accept-negotiation button -small" ${disable}>Approve</button>
					</form>
					<c:if test="${disable == 'disabled'}">
						<div class="alert alert-danger">You are not authorized to approve or decline this request. Please contact your manager or account administrator to approve or decline.</div>
					</c:if>
			</c:when>
			<c:otherwise>
				<form action="/assignments/cancel_negotiation/${work.workNumber}" class="wm-action-container">
					<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
					<button type="submit" class="accept-negotiation button">Cancel Request</button>
				</form>
			</c:otherwise>
		</c:choose>

	</div>
</div>
