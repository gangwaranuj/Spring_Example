<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:import url='/WEB-INF/views/web/partials/assignments/details/print.jsp'/>

<c:if test="${!(work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'])}">
	<c:choose>
		<c:when test="${work.resourceConfirmationRequired and work.activeResource.confirmed eq 'false'}">
			<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_confirm.jsp"/>
		</c:when>
		<c:when test="${not empty work.activeResource}">
			<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_checkin.jsp"/>
		</c:when>
	</c:choose>
</c:if>

<c:if test="${not empty work.customFieldGroups}">
	<c:import url='/WEB-INF/views/web/partials/assignments/details/custom_fields.jsp'/>
</c:if>

<c:if test="${( is_admin || is_active_resource || isInternal )}">
	<c:import url='/WEB-INF/views/web/partials/assignments/details/surveys.jsp'/>
</c:if>

<c:if test="${work.partGroup != null}">
	<c:import url="/WEB-INF/views/web/partials/assignments/details/parts.jsp"/>
</c:if>

<c:if test="${not empty work.deliverableRequirementGroupDTO
|| !(work.status.code == workStatusTypes['DRAFT']
|| work.status.code == workStatusTypes['SENT'])}">
	<c:import url='/WEB-INF/views/web/partials/assignments/details/deliverables.jsp'/>
</c:if>

<c:choose>
	<c:when  test="${is_active_resource && work.status.code == workStatusTypes['ACTIVE'] }">
		<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_complete.jsp"/>
	</c:when>
	<c:when test="${is_admin && work.status.code == workStatusTypes['ACTIVE']}">
		<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/buyer_complete.jsp"/>
	</c:when>
</c:choose>
