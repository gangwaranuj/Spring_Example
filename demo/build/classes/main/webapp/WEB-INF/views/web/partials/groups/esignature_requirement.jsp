<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<li class="${met_requirement}">
	<c:if test="${not addedHelloSign}">
		<c:set var="addedHelloSign" value="true" scope="request"/>
		<script type="text/javascript" src="https://s3.amazonaws.com/cdn.hellosign.com/public/js/hellosign-embedded.LATEST.min.js"></script>
	</c:if>

	<i class="wm-icon-<c:out value="${met_requirement == 'yes' ? 'checkmark' : 'x'}"/>"></i>
	<p>${criterion.typeName}: <strong>${criterion.name}</strong></p>
	<c:if test="${met_requirement == 'no'}">
		<a href="javascript:void(0)" data-companyuuid="${group.company.uuid}" data-templateuuid="${criterion.requirable.templateUuid}" class="button esig-hellosign-init">Sign</a>
	</c:if>
</li>
