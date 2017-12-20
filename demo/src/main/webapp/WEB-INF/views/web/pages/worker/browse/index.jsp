<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<wm:app pagetitle="Browse Work" bodyclass="page-worker-browse" webpackScript="worker" breadcrumbSection="Browse Work" breadcrumbSectionURI="/worker/browse" breadcrumbPage="Overview">
	<c:set var="hasAnyRoleAdminManagerDispatcher" value="false" />
	<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
		<c:set var="hasAnyRoleAdminManagerDispatcher" value="true" />
	</sec:authorize>

	<c:choose>
  	<c:when test="${isWorkerCompany}" >
			<c:set var="hasDispatchEnabled" value="true" />
		</c:when>
		<c:otherwise>
			<c:set var="hasDispatchEnabled" value="false" />
		</c:otherwise>
	</c:choose>
	<script>
		var config = {
			feedTrackerEl: '.worker-browse--find-work-container',
			'worker': ${contextJson},
			firstName: '${wmfmt:escapeJavaScript(currentUser.firstName)}',
			isBuyer: ${currentUser.buyer},
			limit: '25',
			distance: '${currentUser.maxTravelDistance}',
			postalCode: '${not empty currentUser.postalCode ? currentUser.postalCode : 10001}',
			companyHidesPricing: ${currentUser.companyHidesPricing},
			hasAnyRoleAdminManagerDispatcher: ${hasAnyRoleAdminManagerDispatcher},
			hasDispatchEnabled: ${hasDispatchEnabled},
			email: '${currentUser.email}'
		}
	</script>

	<div id="worker-browse">
		<c:if test="${currentUser.seller || currentUser.dispatcher}">
			<div class="container">
				<div class="worker-browse--find-work-container">
					<h3 class="page-header">
						<wm:branding name="Feed" /> - <span class="gray-brand small">Find Great Work Near You</span>
					</h3>
					<c:import url="/WEB-INF/views/web/partials/feed/shared/feed.jsp" />
				</div>
			</div>
		</c:if>
	</div>

	<jsp:include page="/WEB-INF/views/web/partials/general/analytics/retargeting.jsp" />

</wm:app>
