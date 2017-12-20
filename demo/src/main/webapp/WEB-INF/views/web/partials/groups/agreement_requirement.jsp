<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<li class="${met_requirement}">
	<i class="wm-icon-<c:out value="${met_requirement == 'yes' ? 'checkmark' : 'x'}"/>"></i>
	<p>${criterion.typeName}: <strong>${criterion.name}</strong></p>
	<c:if test="${met_requirement == 'no'}">
		<a id="agreement_modal_anchor" data-url="${criterion.url}" href="javascript:void(0)" class="button">Agree</a>
	</c:if>
</li>
