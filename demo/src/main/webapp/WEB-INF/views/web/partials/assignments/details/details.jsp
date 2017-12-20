<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="isAdminOrManagerOrDispatcher" value="false" scope="request" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="isAdminOrManagerOrDispatcher" value="true" scope="request" />
</sec:authorize>
<c:choose>
	<c:when test="${isAdminOrManagerOrDispatcher}">
		<c:set var="hidePricing" value="${false}" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" scope="request" />
	</c:otherwise>
</c:choose>
		


<c:if test="${!isWorkBundle && (!hidePricing || currentUser.buyer) && !(work.status.code == workStatusTypes['INPROGRESS'] || work.status.code == workStatusTypes['ACTIVE'] || work.status.code == workStatusTypes['COMPLETE'])}">
	<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_overview.jsp"/>
</c:if>

<c:import url="/WEB-INF/views/web/partials/assignments/details/dispatcher_info.jsp"/>

<c:import url="/WEB-INF/views/web/partials/assignments/details/date_time.jsp"/>

<c:if test="${not empty work.location}">
	<c:import url="/WEB-INF/views/web/partials/assignments/details/location.jsp"/>
</c:if>

<c:import url="/WEB-INF/views/web/partials/assignments/details/client.jsp"/>

<c:import url="/WEB-INF/views/web/partials/assignments/details/buyer_info.jsp"/>

<c:if test="${!isWorkBundle && (!hidePricing || currentUser.buyer) && (work.status.code == workStatusTypes['INPROGRESS'] || work.status.code == workStatusTypes['ACTIVE'])}">
	<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_overview.jsp"/>
</c:if>

<c:if test="${offlinePaymentEnabled}">
	<div class="page-assignment-details--offline-payment-notice">
		Payment for this assignment will occur outside the Work Market platform.
	</div>
</c:if>
