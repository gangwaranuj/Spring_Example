<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="control-group">
	<div class="controls">
		<c:if test="${40 > available_balance}">
			<p>
				<form:radiobutton path="paymentType" value="account" id="payment-type-account" disabled="true"/>
				Pay with funds on my account
			</p>
		</c:if>

		<c:if test="${40 <= available_balance}">
			<p>
				<form:radiobutton path="paymentType" value="account" id="payment-type-account"/>
				Pay with funds on my account ( <i><b>Account Balance: </b></i> $ ${available_balance} )
			</p>
		</c:if>
		<p><form:radiobutton path="paymentType" value="cc" id="payment-type-cc" checked="checked"/> Pay with a credit card</p>
	</div>
</div>

<div id="payment-types">
	<div id="payment-account">
		<c:if test="${screeningPrice > spendLimit}">
			<div class="alert-message alert-error">Not enough funds on your account. <a href="/funds/add">Add some &raquo;</a></div>
		</c:if>
	</div>

	<div id="payment-cc">
		<jsp:include page="/WEB-INF/views/web/partials/screening/cc_payment_form.jsp"/>
	</div>
</div>



