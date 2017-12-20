<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty param.hideBar || param.hideBar != 'true'}">
	<div class="alert alert-error">
		<c:out value="${company.name}" /> is currently locked because of a past due payment.
		<a class="unlock_company button -small" href="/admin/locks">Visit Unlock Page</a>
	</div>
	<div class="dn">
		<div id="unlock_form_container" class="unlock">
			<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/unlock.jsp"/>
		</div>
	</div>
</c:if>
