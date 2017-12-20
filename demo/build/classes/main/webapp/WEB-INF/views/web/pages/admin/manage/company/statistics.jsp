<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:choose>
	<c:when test="${empty requestScope.companyView}">
		<wm:app pagetitle="Statistics Report" webpackScript="reports" bodyclass="statisticsReport" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="Statistics Report">
			<link href="${mediaPrefix}/statistics.css" rel="stylesheet" type="text/css"/>
			<jsp:include page="/WEB-INF/views/web/pages/admin/manage/company/statistics/index.jsp"/>
		</wm:app>
	</c:when>
	<c:otherwise>
		<wm:admin webpackScript="reports">
			<link href="${mediaPrefix}/statistics.css" rel="stylesheet" type="text/css"/>
			<jsp:include page="/WEB-INF/views/web/pages/admin/manage/company/statistics/index.jsp"/>
		</wm:admin>
	</c:otherwise>
</c:choose>
