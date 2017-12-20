<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<h6>Resolution</h6>
<blockquote class="wordwrap"><em><c:out value="${work.resolution}"/></em></blockquote>

<c:choose>
	<c:when test="${isWorkerCompany}" >
		<c:if test="${!hidePricing}">
				<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_close.jsp"/>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_close.jsp"/>
	</c:otherwise>
</c:choose>
