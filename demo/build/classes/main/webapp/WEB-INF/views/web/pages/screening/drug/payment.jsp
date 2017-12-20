<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Payment" bodyclass="screening" webpackScript="settings">

	<script>
		var config = {
			mode: 'screening',
			usaPrice: '${screeningPrices['bkgrdchk']}',
			intlPrice:  '${screeningPrices['bkgrdchkIN']}'
		};
	</script>

	<jsp:include page="/WEB-INF/views/web/partials/general/notices.jsp">
		<jsp:param name="bundle" value="${requestScope.bundle}"/>
	</jsp:include>

	<div class="content">
		<div class="inner-container">
			<fieldset>
				<legend>Payment Options:
					<small class="meta">You will be charged <fmt:formatNumber value="${requestScope.screeningPrice}" currencySymbol="$" type="currency"/> for your Sterling drug screen, regardless of whether or not you pass or fail.</small>
				</legend>
			</fieldset>

			<form:form action="/screening/drug/payment" modelAttribute="paymentForm" method="post" id="screeningForm" accept-charset="utf-8" cssClass="form-horizontal">
				<wm-csrf:csrfToken />
				<jsp:include page="/WEB-INF/views/web/partials/screening/payment_form.jsp"/>

				<div class="wm-action-container">
					<a class="button" href="/home">Cancel</a>
					<button type="submit" class="button">Process Payment</button>
				</div>
			</form:form>
		</div>
	</div>
</wm:app>
