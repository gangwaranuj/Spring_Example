<%@ tag description="Radio Input" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="active" required="false" %>

<c:choose>
	<c:when test="${active}">
		<span class="active">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/icon-${name}.jsp"/>
		</span>
	</c:when>
	<c:otherwise>
		<jsp:include page="/WEB-INF/views/web/partials/svg-icons/icon-${name}.jsp"/>
	</c:otherwise>
</c:choose>
