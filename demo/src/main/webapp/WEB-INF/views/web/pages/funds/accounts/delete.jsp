<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<form action="/funds/accounts/delete/${id}" method="post" class="form-stacked" id="delete-account">
	<wm-csrf:csrfToken />
	<p>
		<fmt:message key="funds.account.delete.remove_account"/>
		<c:if test="${fn:length(accounts) == 1}">
			<fmt:message key="funds.account.delete.not_able_add_new"/>
		</c:if>
	</p>
</form>