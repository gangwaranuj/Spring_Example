<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Payment Center" bodyclass="payment page-payments" breadcrumbSection="Payments" breadcrumbSectionURI="/payments" breadcrumbPage="Overview" webpackScript="payments">

	<script>
		var config = {
			payments: ${contextJson}
		};
	</script>


	<c:import url="/payments/dashboard" />

	<c:if test="${currentUser.buyer || (currentUser.buyer && currentUser.seller)}">
		<a class="button pull-right" href="/payments/invoices/payables">View all invoices</a>
	</c:if>

	<c:if test="${currentUser.seller || currentUser.dispatcher}">
		<a class="button pull-right" href="/payments/invoices/receivables">View all invoices</a>
	</c:if>
</wm:app>
