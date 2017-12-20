<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
	<c:param name="bundle" value="${bundle}"/>
</c:import>

<p>
	<c:choose>
		<c:when test="${isAdmin}">
			Add an Expense Reimbursement to cover any out-of-pocket costs that
			<c:out value="${work.activeResource.user.name.firstName}" /> <c:out value="${work.activeResource.user.name.lastName}" />
			might incur in order to complete this assignment.
		</c:when>
		<c:otherwise>
			Request an expense reimbursement to cover any out-of-pocket costs you've
			incurred or will have to incur in order to complete this assignment.
		</c:otherwise>
	</c:choose>
</p>

<form action='/assignments/reimbursement/${work.workNumber}' id='reimbursementForm' method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name='price_negotiation' value='1' id='price_negotiation'/>
	<input type="hidden" name="pricing" value="${work.pricing.id}"/>
	<input type="hidden" id="wmFee" name="wmFee" value="${wmfmt:escapeJavaScript(workFee)}"/>
	<input type="hidden" id="maxFee" name="maxFee" value="${wmfmt:escapeJavaScript(maxWorkFee)}"/>

	<c:import url="/WEB-INF/views/web/partials/assignments/details/expense_display.jsp">
		<c:param name="readOnly" value="false"/>
	</c:import>

	<sec:authorize access="!principal.editPricingCustomAuth">
		<c:set var="disable" value="disabled" />
	</sec:authorize>

	<div class="wm-action-container">
		<button type="submit" class="button" ${disable}>Submit</button>

		<c:if test="${disable == 'disabled'}">
			<div style="color:red; clear:both;">You are not authorized to submit this request. Please contact your manager or account administrator.</div>
		</c:if>
	</div>

</form>
