<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
	<c:param name="bundle" value="${bundle}" />
</c:import>

<c:if test="${company.locked}">
	<c:import url="/WEB-INF/views/web/partials/admin/manage/company/unlock_header.jsp"/>
</c:if>
<c:if test="${company.suspended}">
	<c:import url="/WEB-INF/views/web/partials/admin/manage/company/suspend_header.jsp"/>
</c:if>

<c:choose>
	<c:when test="${not empty param.overview && param.overview == 'true'}">
		<h1 class="name">
			<c:choose>
				<c:when test="${company.operatingAsIndividualFlag}">
					<c:out value="${company.effectiveName}"/>
				</c:when>
				<c:otherwise>
					<c:out value="${company.name}"/>
				</c:otherwise>
			</c:choose>
			<small>(ID: ${company.id})</small>
			<small>(<a id="edit_company_info">Edit Info</a>)</small>
		</h1>
	</c:when>
	<c:otherwise>
		<h1 class="name"><c:out value="${company.name}"/></h1>
	</c:otherwise>
</c:choose>


<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/tabs.jsp" />