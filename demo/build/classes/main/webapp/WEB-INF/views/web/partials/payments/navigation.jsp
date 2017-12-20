<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<ul class="nav wm-tabs">
	<c:if test="${currentUser.buyer}">
		<a class="wm-tab <c:if test="${currentView eq 'payables'}">-active</c:if>" href="/payments/invoices/payables">Payables</a>
	</c:if>
	<c:if test="${currentUser.seller || currentUser.dispatcher}">
		<a class="wm-tab <c:if test="${currentView eq 'receivables'}"> -active</c:if>" href="/payments/invoices/receivables">Receivables</a>
	</c:if>
		<a class="wm-tab <c:if test="${currentView eq 'ledger'}"> -active</c:if>" href="/payments/ledger">Ledger</a>
<vr:rope>
	<vr:venue name="OFFLINE_PAY">
		<a class="wm-tab <c:if test="${currentView eq 'offline_ledger'}"> -active</c:if>" href="/payments/offline_ledger">Off-Platform Ledger</a>
	</vr:venue>
	<vr:venue name="OFFLINE_PAY_ALL">
		<a class="wm-tab <c:if test="${currentView eq 'offline_ledger'}"> -active</c:if>" href="/payments/offline_ledger">Off-Platform Ledger</a>
	</vr:venue>
</vr:rope>
</ul>
