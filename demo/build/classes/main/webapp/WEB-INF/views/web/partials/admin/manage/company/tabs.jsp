<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<ul class="nav wm-tabs">
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'overview'}"> -active</c:if>"><a href="/admin/manage/company/overview/${requestScope.company.id}">Overview</a></li>
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'work'}"> -active</c:if>"><a href="/admin/manage/company/work/${requestScope.company.id}">Assignments</a></li>
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'resources'}"> -active</c:if>"><a href="/admin/manage/company/resources/${requestScope.company.id}">Resources</a></li>
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'report'}"> -active</c:if>"><a href="/admin/manage/company/report/${requestScope.company.id}">Report</a></li>
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'sales'}"> -active</c:if>"><a href="/admin/manage/company/sales/${requestScope.company.id}">Sales Info</a></li>
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'finances'}"> -active</c:if>"><a href="/admin/manage/company/finances/${requestScope.company.id}">Finances</a></li>
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'pricing'}"> -active</c:if>"><a href="/admin/manage/company/pricing/${requestScope.company.id}">Pricing</a></li>
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'statistics'}"> -active</c:if>"><a href="/admin/manage/company/statistics/${requestScope.company.id}">Statistics</a></li>
	<li class="wm-tab <c:if test="${requestScope.companyView eq 'features'}"> -active</c:if>"><a href="/admin/manage/company/features/${requestScope.company.id}">Features</a></li>
</ul>
