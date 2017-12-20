<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div
	<c:if test="${not empty requestScope.containerId or not empty param.containerId}">
		id="<c:out value="${requestScope.containerId}" default="${param.containerId}" />"
	</c:if>
	class="alert tac dn" data-alert="alert">
	<a href="javascript:void(0);" onclick="javascript:close_message(this); return false;" class="close">x</a>
	<div></div>
</div>
