<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:if test="${not empty globalMaintenance}">
	<div class="alert alert-block" style="text-align: center;">
		<h4>${globalMaintenance.title}</h4>
		${wmfmt:autoLink(globalMaintenance.message)}
	</div>
</c:if>
