<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form:form modelAttribute="verifyForm" cssClass="form-horizontal" action="/funds/accounts/verify/${id}" method="POST" id="verify_account_form">
	<wm-csrf:csrfToken />
	<c:import url='/WEB-INF/views/web/partials/general/notices_js.jsp'>
		<c:param name="containerId" value="verify_account_message"/>
	</c:import>

	<p>Enter the amounts deposited into your bank account. The deposits usually appear within 2-3 business days after linking your bank account with your Work Market account.</p>

	<div class="control-group">
		<form:label cssClass="control-label" path='amount1'>Deposit 1</form:label>
		<div class="controls" style="width: 4em;">
			<form:input path="amount1" placeholder="00" maxlength="2" size="3" cssClass="span1" />
		</div>
	</div>
	<div class="control-group">
		<form:label cssClass="control-label" path='amount2'>Deposit 2</form:label>
		<div class="controls" style="width: 4em;">
			<form:input path="amount2" placeholder="00" maxlength="2" size="3" cssClass="span1" />
		</div>
	</div>
</form:form>