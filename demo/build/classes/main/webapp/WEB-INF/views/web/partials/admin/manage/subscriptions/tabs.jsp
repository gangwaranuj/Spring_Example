<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="nav nav-tabs">
	<li <c:if test="${requestScope.reportingView eq 'standard'}">class="active"</c:if>><a href="/admin/manage/subscriptions/reporting/standard">Standard</a></li>
	<li <c:if test="${requestScope.reportingView eq 'usage'}">class="active"</c:if>><a href="/admin/manage/subscriptions/reporting/usage">Usage</a></li>
</ul>