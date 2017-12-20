<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="met_or_expired_requirement" value="${met_requirement}"/>
<c:if test="${met_requirement == 'yes' and criterion.expired}">
	<c:set var="met_or_expired_requirement" value="expired"/>
</c:if>
<li class="${met_or_expired_requirement}">
	<i class="wm-icon-<c:out value="${met_requirement == 'yes' ? 'checkmark' : 'x'}"/>"></i>
	<p>${criterion.typeName}: <strong>${criterion.name}</strong></p>
	<c:if test="${criterion.expired}">
		<strong>(expired)</strong>
	</c:if>
	<c:if test="${criterion.expired or met_requirement == 'no'}">
		<a href="javascript:void(0)" data-url="<c:url value="${criterion.url}"/>" class="button trigger-apply">Add/Update</a>
	</c:if>
</li>
