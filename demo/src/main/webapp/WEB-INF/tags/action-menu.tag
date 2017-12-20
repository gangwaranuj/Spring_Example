<%@ tag description="Action Menu" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="buttonClassList" required="false" %>
<%@ attribute name="classList" required="false" %>

<c:choose>
	<c:when test="${shouldShowMDL}">
		<button id="${id}" class="mdl-button mdl-js-button mdl-button--icon ${buttonClassList}">
			<i class="material-icons">more_vert</i>
		</button>
		<ul class="mdl-menu mdl-menu--bottom-left mdl-js-menu mdl-js-ripple-effect ${classList}" for="${id}">
			<jsp:doBody />
		</ul>
	</c:when>
	<c:otherwise>
		<select class="action-menu">
			<jsp:doBody />
		</select>
	</c:otherwise>
</c:choose>
