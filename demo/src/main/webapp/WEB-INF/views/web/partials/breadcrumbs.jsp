<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<ul class="breadcrumb clear">
	<c:forEach var="item" items="${breadrcumbs}">
		<li class="fl"><a href="<c:url value="${item.url}"/>"><c:out value="${item.name}"/></a>
			<span class="divider">&raquo;</span></li>
	</c:forEach>
	<li class="fl active"><c:out value="${activeBreadcrumb}"/></li>

	<c:if test="${not empty additionalBreadcrumbs}">
		<c:out value="${additionalBreadcrumbs}" escapeXml="false"/>
	</c:if>
</ul>
