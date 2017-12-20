<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<ul class="wm-tabs">
	<li class="wm-tab<c:if test="${requestScope.accountingView eq '1099misc'}"> -active</c:if>"><a href="/admin/accounting/form_1099">1099-MISC</a></li>
	<li class="wm-tab<c:if test="${requestScope.accountingView eq 'earnings'}"> -active</c:if>"><a href="/admin/accounting/earnings">Earnings Report</a></li>
	<li class="wm-tab<c:if test="${requestScope.accountingView eq 'earningsDetail'}"> -active</c:if>"><a href="/admin/accounting/non_vor_tax">1099-MISC Data Report</a></li>
	<li class="wm-tab<c:if test="${requestScope.accountingView eq 'taxServiceDetail'}"> -active</c:if>"><a href="/admin/accounting/tax_service_detail">Tax Service Report</a></li>
</ul>
