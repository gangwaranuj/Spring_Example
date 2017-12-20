<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:forEach var="action" items="${profileCompleteness.missingActions}">
	<c:set var="showItem" value="${true}" />
	<c:if test="${action.code eq 'drugTest'}">
		<sec:authorize access="hasFeature('screening-drug')" var="showItem" />
	</c:if>
	<c:if test="${action.code eq 'background'}">
		<sec:authorize access="hasFeature('background_check_international')" var="showItem" />
	</c:if>

	<c:if test="${showItem}">
		<ul>
			<li class="profile-<c:out value="${fn:toUpperCase(action.code)}"/>">
				<c:set var="actionUrl">
					<spring:message code="profile.actions.${action.code}.url"/>
				</c:set>
				<a href="<c:url value="${actionUrl}"/>">
					<c:if test="${action.code eq 'background' && isCanadian}">
						<span class="label warning"> NEW!</span>
					</c:if>
					<c:out value="${action.description}"/>
				</a>
			</li>
		</ul>
	</c:if>
</c:forEach>
