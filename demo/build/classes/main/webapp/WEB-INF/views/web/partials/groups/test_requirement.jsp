<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<li class="${met_requirement}">
	<i class="wm-icon-<c:out value="${met_requirement == 'yes' ? 'checkmark' : 'x'}"/>"></i>
	<p>Passed <strong>${criterion.name}</strong></p>
	<c:if test="${met_requirement == 'no'}">
	<a href="javascript:void(0)" data-url="<c:url value="${criterion.url}"/>" class="button trigger-apply">Take Test</a>
	</c:if>
</li>
