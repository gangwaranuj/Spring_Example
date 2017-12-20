<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/bulk_approve" class="form-stacked" method="post">
	<wm-csrf:csrfToken />

	<c:forEach var="id" items="${requestScope.ids}">
		<input type="hidden" name="ids" value="${id}" />
	</c:forEach>

	<c:choose>
		<c:when test="${fn:length(requestScope.results) eq 1}"><c:set var="assignment" scope="page">assignment</c:set></c:when>
		<c:otherwise><c:set var="assignment" scope="page">assignments</c:set></c:otherwise>
	</c:choose>

	<p><strong>You are about to approve ${fn:length(requestScope.results)} <c:out value="${pageScope.assignment}"/> (<fmt:formatNumber value="${requestScope.totalDue}" currencySymbol="$" type="currency"/>) for payment. Click <em>Approve</em> to continue and approve the <c:out value="${pageScope.assignment}"/> or <em>Cancel</em> to return to the assignment dashboard.</strong></p>
	<p><small class="meta">By approving an assignment, you are reaffirming the <a href="/tos">Terms of Use Agreement</a> and you agree that you are 100% satisfied with the work performed. Funds will be released to the worker according to the payment terms of the assignment and are non-refundable. If you are not satisfied with any of these assignments, visit the detail page for each and click "I'm Not Satisfied" to have the worker update their work.</small></p>

	<sec:authorize access="!principal.approveWorkCustomAuth OR !hasAnyRole('PERMISSION_APPROVEWORK')">
		<c:set var="disable" value="disabled" />
	</sec:authorize>
	<div class="wm-action-container">
		<button data-modal-close class="button">Close</button>
		<button type="submit" id="bulkapprove"  class="button" ${disable}>Approve</button>
		<c:if test="${disable == 'disabled'}">
			<span style="color:red">You are not authorized to approve or decline this request. Please contact your manager or account administrator to approve or decline</span>
		</c:if>
	</div>

</form>
