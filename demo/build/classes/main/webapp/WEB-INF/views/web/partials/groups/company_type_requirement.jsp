<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<li class="${met_requirement}">
	<i class="wm-icon-<c:out value="${met_requirement == 'yes' ? 'checkmark' : 'x'}"/>"></i>
	<p>Company is a <strong>${criterion.name}</strong></p>
</li>
