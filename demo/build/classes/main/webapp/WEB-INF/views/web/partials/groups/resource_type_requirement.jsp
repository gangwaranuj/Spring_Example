<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<li class="${met_requirement}">
	<i class="wm-icon-<c:out value="${met_requirement == 'yes' ? 'checkmark' : 'x'}"/>"></i>
	<c:choose>
		<c:when test="${criterion.name == 'Employee'}">
			<p>Must be an <strong>employee</strong> of <strong>${company.name}</strong></p>
		</c:when>
		<c:when test="${criterion.name == 'Contractor'}">
			<p>Must be a <strong>contractor</strong> for <strong>${company.name}</strong></p>
		</c:when>
		<c:otherwise>
			<p>Must be a <strong>contractor</strong> for, or an <strong>employee</strong> of <strong>${company.name}</strong></p>
		</c:otherwise>
	</c:choose>
</li>
